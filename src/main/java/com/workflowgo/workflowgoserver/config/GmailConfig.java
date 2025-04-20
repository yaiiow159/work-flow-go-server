package com.workflowgo.workflowgoserver.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class GmailConfig {

    @Bean
    public Gmail gmail(AppProperties appProperties) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        UserCredentials userCred = UserCredentials.newBuilder()
                .setClientId(appProperties.getAuth().getClientId())
                .setClientSecret(appProperties.getAuth().getClientSecret())
                .setRefreshToken(appProperties.getAuth().getRefreshToken())
                .build();

        HttpCredentialsAdapter credentialsAdapter = new HttpCredentialsAdapter(userCred);

        return new Gmail.Builder(httpTransport, jsonFactory, credentialsAdapter)
                .setApplicationName("WorkflowGo")
                .build();
    }
}
