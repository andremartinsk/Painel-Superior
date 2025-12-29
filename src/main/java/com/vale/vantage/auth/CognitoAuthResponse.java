
package com.vale.vantage.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO da resposta do Cognito InitiateAuth.
 * Mapeia os nomes EXATOS do payload (com maiúsculas) para acessar via getters.
 */
public class CognitoAuthResponse {

    @JsonProperty("AuthenticationResult")
    private AuthenticationResult authenticationResult;

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }
    public void setAuthenticationResult(AuthenticationResult authenticationResult) {
        this.authenticationResult = authenticationResult;
    }

    public static class AuthenticationResult {

        @JsonProperty("IdToken")
        private String idToken;

        @JsonProperty("AccessToken")
        private String accessToken;

        @JsonProperty("ExpiresIn")
        private Integer expiresIn;

        @JsonProperty("TokenType")
        private String tokenType;

        @JsonProperty("RefreshToken")
        private String refreshToken;

        public String getIdToken() {
            return idToken;
        }
        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public Integer getExpiresIn() {
            return expiresIn;
        }
        public void setExpiresIn(Integer expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getTokenType() {
            return tokenType;
        }
        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
