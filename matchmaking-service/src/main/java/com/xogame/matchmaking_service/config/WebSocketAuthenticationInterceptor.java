package com.xogame.matchmaking_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthenticationInterceptor
        implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    private final JwtAuthenticationConverter
            jwtAuthenticationConverter;

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

        if (accessor == null) {
            return message;
        }

        /*
         * On authentifie uniquement le CONNECT.
         *
         * Spring conserve ensuite automatiquement
         * le Principal pour SUBSCRIBE, SEND et DISCONNECT.
         */
        if (StompCommand.CONNECT.equals(
                accessor.getCommand()
        )) {
            String authorizationHeader =
                    accessor.getFirstNativeHeader(
                            "Authorization"
                    );

            if (!StringUtils.hasText(
                    authorizationHeader
            )) {
                throw new IllegalArgumentException(
                        "Header Authorization absent dans le CONNECT STOMP"
                );
            }

            if (!authorizationHeader.startsWith(
                    "Bearer "
            )) {
                throw new IllegalArgumentException(
                        "Le header Authorization doit commencer par Bearer"
                );
            }

            String token =
                    authorizationHeader.substring(7);

            Jwt jwt = jwtDecoder.decode(token);

            AbstractAuthenticationToken authentication =
                    jwtAuthenticationConverter.convert(jwt);

            if (authentication == null) {
                throw new IllegalStateException(
                        "Impossible de convertir le JWT en Authentication"
                );
            }

            authentication.setAuthenticated(true);

            /*
             * Très important :
             * permet l'utilisation de
             * /user/queue/matchmaking.
             */
            accessor.setUser(authentication);
        }

        return message;
    }
}