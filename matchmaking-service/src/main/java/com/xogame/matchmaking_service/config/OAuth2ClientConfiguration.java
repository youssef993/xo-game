package com.xogame.matchmaking_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;
import org.springframework.web.client.RestClient;

@Configuration
public class OAuth2ClientConfiguration {

    @Bean
    OAuth2AuthorizedClientManager
    authorizedClientManager(
            ClientRegistrationRepository registrations,
            OAuth2AuthorizedClientService clientService
    ) {
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                registrations,
                clientService
        );
    }

    @Bean
    RestClient gameServiceRestClient(
            RestClient.Builder builder,
            OAuth2AuthorizedClientManager manager
    ) {
        OAuth2ClientHttpRequestInterceptor interceptor =
                new OAuth2ClientHttpRequestInterceptor(
                        manager
                );

        interceptor.setClientRegistrationIdResolver(
                new RequestAttributeClientRegistrationIdResolver()
        );

        return builder
                .requestInterceptor(interceptor)
                .build();
    }
}