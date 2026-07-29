package com.xogame.game_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class WebSocketAuthenticationInterceptor
        implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null
                || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authorization =
                accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authorization)
                || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BadCredentialsException(
                    "Token WebSocket manquant"
            );
        }

        String tokenValue = authorization
                .substring(BEARER_PREFIX.length())
                .trim();

        if (!StringUtils.hasText(tokenValue)) {
            throw new BadCredentialsException(
                    "Token WebSocket vide"
            );
        }

        Jwt jwt = jwtDecoder.decode(tokenValue);

        AbstractAuthenticationToken authentication =
                jwtAuthenticationConverter.convert(jwt);

        if (authentication == null) {
            throw new BadCredentialsException(
                    "Authentification WebSocket invalide"
            );
        }

        accessor.setUser(authentication);

        log.info(
                "Connexion STOMP authentifiée : utilisateur={}, authorities={}",
                authentication.getName(),
                authentication.getAuthorities()
        );

        return message;
    }
}
