package com.api.veiculos.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String msg,
            HttpServletRequest req
    ) {
        ApiError body = ApiError.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(msg)
                .path(req.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(PlacaDuplicadaException.class)
    public ResponseEntity<ApiError> handlePlacaDuplicada(
            PlacaDuplicadaException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handle401(
            BadCredentialsException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handle403(
            AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Acesso negado", req);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handle404(
            RuntimeException ex, HttpServletRequest req) {
        if (ex.getMessage().contains("não encontrado"))
            return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);

        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }

}
