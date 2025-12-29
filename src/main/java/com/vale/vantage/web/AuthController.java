
package com.vale.vantage.web;

import com.vale.vantage.auth.CredentialStore;
import com.vale.vantage.auth.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

class LoginRequest {
    public String username;
    public String password;
}

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;
    private final CredentialStore credentialStore;

    public AuthController(TokenService tokenService, CredentialStore credentialStore) {
        this.tokenService = tokenService;
        this.credentialStore = credentialStore;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest req) {
        String user = req.username == null ? null : req.username.trim();
        String pass = req.password;
        return tokenService.loginMono(user, pass)
                .then(Mono.just(ResponseEntity.ok("OK")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).body("Falha no login: " + e.getMessage())));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me() {
        String username = credentialStore.getUsername();
        if (username == null || !tokenService.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Não autenticado"));
        }
        return ResponseEntity.ok(Map.of("username", username));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        tokenService.logout();
        return ResponseEntity.ok("OK");
    }
}
