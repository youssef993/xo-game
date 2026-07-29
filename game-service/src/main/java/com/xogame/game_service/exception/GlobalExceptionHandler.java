package com.xogame.game_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ProblemDetail handleNotFound(
            GameNotFoundException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problem.setTitle("Partie introuvable");
        problem.setType(URI.create("/errors/game-not-found"));
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(InvalidGameActionException.class)
    public ProblemDetail handleInvalidAction(
            InvalidGameActionException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problem.setTitle("Action de jeu refusée");
        problem.setType(URI.create("/errors/invalid-game-action"));
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Les données envoyées sont invalides"
        );

        problem.setTitle("Erreur de validation");
        problem.setProperty(
                "errors",
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        error -> error.getField(),
                                        error -> error.getDefaultMessage(),
                                        (first, second) -> first
                                )
                        )
        );

        return problem;
    }
}