package com.xogame.matchmaking_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            MatchmakingException.class
    )
    public ProblemDetail handleMatchmakingException(
            MatchmakingException exception
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT,
                        exception.getMessage()
                );

        problem.setTitle(
                "Action de matchmaking impossible"
        );

        problem.setType(
                URI.create(
                        "/errors/matchmaking-conflict"
                )
        );

        problem.setProperty(
                "timestamp",
                Instant.now()
        );

        return problem;
    }
}