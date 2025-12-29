
# Rolos Monitor (Spring Boot + WebFlux + SSE)

Painel web para monitoramento de rolos consumindo **Alarm API V2 (/alarm-list)** do Vantage IoT.

## Requisitos
- Java 17+
- Maven 3.9+

## Como rodar
1. Compile e suba o servidor:
   ```bash
   mvn spring-boot:run
   ```
2. Acesse `http://localhost:8080/` no navegador.
3. Faça **login** com seu e-mail e senha (não são armazenados em código). O backend chamará o **Cognito** para obter o **idToken** e renovará automaticamente **5 min antes de expirar**.
4. O painel atualiza automaticamente a cada **5 segundos** via **SSE**.
5. Clique em **Desconectar** para encerrar a sessão e parar o streaming.

## Configurações
- Alarm API base: `https://586fqbdcb0.execute-api.us-east-1.amazonaws.com/v1`
- Endpoint usado: `/alarm-list` com `deviceType=conveyorPosition`.
- `idSite` configurado em `src/main/resources/application.yml`.
- O cabeçalho **Authorization** leva o próprio `idToken` (sem `Bearer`).

> **Segurança:** credenciais de login não são persistidas. Para produção, considere habilitar **RefreshToken** no App Client do Cognito ou usar um cofre de segredos.
