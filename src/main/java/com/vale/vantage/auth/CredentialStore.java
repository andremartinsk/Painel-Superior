
package com.vale.vantage.auth;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Armazena credenciais em memória durante a sessão (apenas enquanto logado).
 * Em produção, preferir refresh token ou armazenar em cofre/criptografado.
 */
@Component
public class CredentialStore {

    private final AtomicReference<String> usernameRef = new AtomicReference<>();
    private final AtomicReference<String> passwordRef = new AtomicReference<>();

    /** Define (ou atualiza) as credenciais da sessão atual. */
    public void set(String username, String password) {
        usernameRef.set(username);
        passwordRef.set(password);
    }

    /** Obtém o usuário armazenado (ou null). */
    public String getUsername() {
        return usernameRef.get();
    }

    /** Obtém a senha armazenada (ou null). */
    public String getPassword() {
        return passwordRef.get();
    }

    /** Limpa as credenciais (logout). */
    public void clear() {
        usernameRef.set(null);
        passwordRef.set(null);
    }

    /** ✅ Indica se existem credenciais válidas armazenadas. */
    public boolean hasCredentials() {
        return usernameRef.get() != null && passwordRef.get() != null;
    }
}
