package com.workflowgo.workflowgoserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();
    private final Cors cors = new Cors();
    private final Email email = new Email();

    @Setter
    @Getter
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;
        private String authorizedRedirectUris;
        private String refreshToken;
        private String clientId;
        private String clientSecret;
    }

    @Setter
    @Getter
    public static class Cors {
        private String[] allowedOrigins;
        
        public String[] getAllowedOrigins() {
            if (allowedOrigins != null && allowedOrigins.length == 1 && allowedOrigins[0].contains(",")) {
                return allowedOrigins[0].split(",");
            }
            return allowedOrigins;
        }
    }

    @Setter
    @Getter
    public static class Email {
        private String senderEmail;
        private String verificationSubject;
        private String verificationTemplate;
        private int verificationCodeExpirationMinutes;
    }
}
