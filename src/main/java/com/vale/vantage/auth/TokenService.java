
package com.vale.vantage.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço de autenticação e gestão do idToken do Cognito.
 * Fluxo totalmente reativo para login; agendador faz renovação preventiva.
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final WebClient cognitoClient;
    private final String clientId;
    private final CredentialStore credentialStore;
    private final ObjectMapper mapper = new ObjectMapper();

    /** Token atual (JWT) */
    private volatile String idToken;

    /** Expiração do token atual */
    private volatile Instant expirationTime;

    public TokenService(
            WebClient.Builder builder,
            CredentialStore credentialStore,
            @Value("${cognito.endpoint}") String cognitoEndpoint,
            @Value("${cognito.client-id}") String clientId
    ) {
        this.cognitoClient = builder.baseUrl(cognitoEndpoint).build();
        this.clientId = clientId;
        this.credentialStore = credentialStore;
    }

    /**
     * Login reativo no Cognito via InitiateAuth (USER_PASSWORD_AUTH).
     * - Serializa manualmente o body para JSON String.
     * - Usa Content-Type exigido: application/x-amz-json-1.1.
     * - Lê a resposta como String e parseia via ObjectMapper.
     */
    public Mono<Void> loginMono(String username, String password) {
        if (username == null || password == null) {
            return Mono.error(new IllegalArgumentException("Credenciais ausentes"));
        }

        // Guarda credenciais em memória da sessão (apenas enquanto logado)
        credentialStore.set(username, password);

        // Monta o body e serializa para JSON String
        Map<String, Object> body = new HashMap<>();
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("PASSWORD", password);
        body.put("AuthParameters", authParams);
        body.put("AuthFlow", "USER_PASSWORD_AUTH");
        body.put("ClientId", clientId);

        final String payloadJson;
        try {
            payloadJson = mapper.writeValueAsString(body);
        } catch (Exception e) {
            return Mono.error(new IllegalStateException("Falha ao serializar body JSON: " + e.getMessage()));
        }

        return cognitoClient.post()
                .header("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth")
                .contentType(MediaType.valueOf("application/x-amz-json-1.1"))
                .bodyValue(payloadJson) // envia String JSON, não Map
                .retrieve()
                // Em caso de erro HTTP, capturar corpo textual
                .onStatus(st -> st.isError(), r ->
                        r.bodyToMono(String.class).flatMap(errBody ->
                                r.toEntity(String.class).flatMap(entity -> {
                                    int code = entity.getStatusCode().value();
                                    log.error("Cognito InitiateAuth falhou. Status={} Body={}", code, errBody);
                                    return Mono.error(new IllegalStateException(
                                            "Falha na autenticação Cognito (HTTP " + code + "): " + errBody));
                                })
                        )
                )
                // ✅ Ler resposta como String (application/x-amz-json-1.1 não é mapeado automaticamente para Map)
                .bodyToMono(String.class)
                // Qualquer etapa pesada/IO fora do event-loop
                .publishOn(Schedulers.boundedElastic())
                .flatMap(json -> {
                    try {
                        // Parse para Map e extrai AuthenticationResult
                        Map<String, Object> resp = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                        Object authResObj = resp.get("AuthenticationResult");
                        if (!(authResObj instanceof Map)) {
                            return Mono.error(new IllegalStateException("Resposta inválida do Cognito (sem AuthenticationResult)"));
                        }
                        Map<?, ?> authRes = (Map<?, ?>) authResObj;

                        Object idTokenObj = authRes.get("IdToken");
                        if (!(idTokenObj instanceof String token)) {
                            return Mono.error(new IllegalStateException("Resposta inválida do Cognito (sem IdToken)"));
                        }

                        Integer expiresIn = null;
                        Object expObj = authRes.get("ExpiresIn");
                        if (expObj instanceof Number n) {
                            expiresIn = ((Number) n).intValue();
                        }

                        Instant exp = extractJwtExpiration(token);
                        if (exp == null && expiresIn != null) {
                            exp = Instant.now().plusSeconds(expiresIn);
                        }

                        this.idToken = token;
                        this.expirationTime = exp;
                        log.info("Token obtido. Expira em: {}", this.expirationTime);
                        return Mono.empty();

                    } catch (Exception e) {
                        log.error("Falha ao parsear resposta do Cognito: {}", e.getMessage());
                        return Mono.error(new IllegalStateException("Falha ao ler resposta do Cognito: " + e.getMessage()));
                    }
                });
    }

    /**
     * Agendador: checa a cada 2 minutos e renova 5 minutos antes de expirar.
     * Roda fora do event-loop; usar blockOptional() aqui é aceitável.
     */
    @Scheduled(fixedDelay = 120_000)
    public void scheduleRefresh() {
        try {
            if (idToken == null || expirationTime == null) return;
            Instant now = Instant.now();
            if (now.isAfter(expirationTime.minusSeconds(300))) {
                if (!credentialStore.hasCredentials()) {
                    log.warn("Sem credenciais em sessão para renovar o token.");
                    return;
                }
                log.info("Token próximo de expirar. Renovando (agendador)...");
                // Rodar em boundedElastic para evitar bloquear event-loop
                loginMono(credentialStore.getUsername(), credentialStore.getPassword())
                        .publishOn(Schedulers.boundedElastic())
                        .blockOptional();
            }
        } catch (Exception e) {
            log.warn("Falha ao renovar token no agendador: {}", e.getMessage());
        }
    }

    /** Logout: limpa token e credenciais em memória da sessão. */
    public synchronized void logout() {
        log.info("Logout: limpando token e credenciais em sessão.");
        this.idToken = null;
        this.expirationTime = null;
        credentialStore.clear();
    }

    /** Retorna token atual; lança erro se não estiver autenticado. */
    public String currentToken() {
        if (idToken == null) throw new IllegalStateException("Não autenticado");
        return idToken;
    }

    /** Extrai 'exp' (epoch seconds) do payload JWT; fallback de 55 minutos se falhar. */
    private Instant extractJwtExpiration(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode node = mapper.readTree(payloadJson);
            if (node.has("exp")) {
                long expSeconds = node.get("exp").asLong();
                return Instant.ofEpochSecond(expSeconds);
            }
        } catch (Exception e) {
            log.warn("Falha ao extrair exp do JWT: {}", e.getMessage());
        }
        return Instant.now().plusSeconds(55 * 60);
    }


    public boolean isAuthenticated() {
        return this.idToken != null && this.expirationTime != null;
    }

}
