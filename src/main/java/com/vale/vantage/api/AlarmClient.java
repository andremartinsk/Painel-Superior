
package com.vale.vantage.api;

import com.vale.vantage.auth.TokenService;
import com.vale.vantage.model.AlarmResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class AlarmClient {

    private static final Logger log = LoggerFactory.getLogger(AlarmClient.class);

    private final WebClient webClient;
    private final TokenService tokenService;
    private final String baseUrl;
    private final String path;
    private final String siteId;

    public AlarmClient(WebClient.Builder builder,
                       TokenService tokenService,
                       @Value("${vantage.alarm.base-url}") String baseUrl,
                       @Value("${vantage.alarm.path}") String path,
                       @Value("${vantage.site-id}") String siteId) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.tokenService = tokenService;
        this.baseUrl = baseUrl;
        this.path = path;
        this.siteId = siteId;
    }

    /**
     * Chamada reativa à Alarm API V2 (/alarm-list) sem sort para evitar HTTP 422.
     * Se quiser incluir apenas alerts, descomente .queryParam("type", "alert").
     */
    public Mono<AlarmResponse> listAlarms(Integer page, Integer pageSize,
                                          String sort, String sortDirection,
                                          String search, String parentData) {

        String token = tokenService.currentToken(); // lança IllegalStateException se não autenticado

        return webClient.get()
                .uri(uriBuilder -> {
                    var b = uriBuilder
                            .path(path)
                            .queryParam("deviceType", "conveyorPosition")
                            .queryParam("idSite", siteId)
                            .queryParam("page", Optional.ofNullable(page).orElse(1))
                            .queryParam("pageSize", Optional.ofNullable(pageSize).orElse(100));

                    // Se precisarmos filtrar somente alertas:
                    // b = b.queryParam("type", "alert");

                    // Remover sort/sortDirection para evitar 422
                    // .queryParamIfPresent("sort", Optional.ofNullable(sort))
                    // .queryParamIfPresent("sortDirection", Optional.ofNullable(sortDirection))

                    b = b.queryParamIfPresent("search", Optional.ofNullable(search))
                            .queryParamIfPresent("parentData", Optional.ofNullable(parentData)); // "0" ou "1"

                    return b.build();
                })
                // Doc: Authorization deve conter o próprio idToken (sem "Bearer")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                // Tratamento de erros HTTP com corpo textual no log
                .onStatus(st -> st.isError(), r -> r.bodyToMono(String.class).flatMap(errBody -> {
                    log.error("Alarm API V2 falhou (HTTP {}): {}", r.statusCode().value(), errBody);
                    return Mono.error(new IllegalStateException(
                            "Alarm API V2 falhou (HTTP " + r.statusCode().value() + "): " + errBody));
                }))
                .bodyToMono(AlarmResponse.class);
    }
}
