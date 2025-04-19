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

    @Setter
    @Getter
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;
        private String authorizedRedirectUris;
    }

    @Setter
    @Getter
    public static class Cors {
        private String[] allowedOrigins;
    }

}
