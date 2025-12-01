package com.brunothecoder.sweepstakes.api.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req){

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation error");
        problem.setDetail("One or more fields are invalid");
        problem.setType(URI.create("https://bolaodafirma.com/errors/validation"));
        problem.setInstance(URI.create(req.getRequestURI()));

        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .toList();

        problem.setProperty("errors", errors);

        return problem;
    }

    @ExceptionHandler
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req){

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Business error");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://bolaodafirma.com/errors/business"));
        problem.setInstance(URI.create(req.getRequestURI()));

        return problem;
    }

    @ExceptionHandler
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest req){

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Entity not found");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://bolaodafirma.com/errors/not-found"));
        problem.setInstance(URI.create(req.getRequestURI()));

        return problem;
    }
}
