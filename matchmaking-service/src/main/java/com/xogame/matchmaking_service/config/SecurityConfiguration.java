package com.xogame.matchmaking_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {
                })

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/actuator/health/**"
                        )
                        .permitAll()

                        /*
                         * Handshake HTTP seulement.
                         * Le JWT STOMP sera vérifié ensuite.
                         */
                        .requestMatchers(
                                "/ws/matchmaking",
                                "/ws/matchmaking/**"
                        )
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/matchmaking/search"
                        )
                        .hasAnyRole(
                                "PLAYER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/matchmaking/status"
                        )
                        .hasAnyRole(
                                "PLAYER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/matchmaking/search"
                        )
                        .hasAnyRole(
                                "PLAYER",
                                "ADMIN"
                        )

                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )

                .build();
    }

    @Bean
    JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new KeycloakRealmRoleConverter()
        );

        /*
         * Important :
         * le Principal WebSocket et le playerId utilisent
         * tous les deux le claim sub.
         */
        converter.setPrincipalClaimName("sub");

        return converter;
    }
}