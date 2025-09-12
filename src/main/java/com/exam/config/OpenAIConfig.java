package com.exam.config;


import com.exam.service.ConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIConfig {

    private String url;
    private String ttsUrl;
    private ChatGpt chatgpt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatGpt {
        private List<Model> models;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Model {
            private String name;
            private Settings settings;

            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Settings {
                private int maxTokens;
                private double temperature;
                private double topP;
                private double frequencyPenalty;
                private double presencePenalty;
                private String language;
                private UserPreferences userPreferences;

                @Data
                @AllArgsConstructor
                @NoArgsConstructor
                public static class UserPreferences {
                    private boolean detailedExplanations;
                    private boolean conciseInteractions;
                    private boolean interactiveApplications;
                }
            }
        }
    }

    @Bean
    @Qualifier("openaiRestTemplate")
    public RestTemplate openaiRestTemplate(ConfigService configService) {
        RestTemplate restTemplate = new RestTemplate();
        // Not: her istekte DB’den okumak anahtarı “canlı” güncel tutar.
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String dbKey = configService.getCredentials(); // DB > YAML
            if (dbKey == null) {
                throw new IllegalStateException("OpenAI API anahtarı bulunamadı (DB veya YAML).");
            }
            request.getHeaders().setBearerAuth(dbKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
