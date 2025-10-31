package com.ms.appointment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("{entity.service.url}")
    private String entityServiceUrl;

    @Value("{user.service.url}")
    private String userServiceUrl;

    @Bean
    public WebClient entityWebClient(){
        return WebClient.builder()
            .baseUrl(entityServiceUrl)
            .build();
    }

    @Bean
    public WebClient userWebClient(){
        return WebClient.builder()
            .baseUrl(userServiceUrl)
            .build();
    }
}
