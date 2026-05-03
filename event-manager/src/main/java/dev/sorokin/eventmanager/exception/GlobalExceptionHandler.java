package dev.sorokin.eventmanager.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(MethodArgumentNotValidException e) {
        logger.error("Got validation exception", e);

        String detailedMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(","));


        var errorDto = new ErrorMessageResponse("Ошибка валидации запроса", detailedMessage, LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFoundException(EntityNotFoundException e) {
        logger.error("Got exception", e);
        var errorDto = new ErrorMessageResponse("Сущность не найдена", e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }


    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFoundException(EntityExistsException e) {
        logger.error("Got exception", e);
        var errorDto = new ErrorMessageResponse("Сотрудник с таким email уже существует", e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorMessageResponse> handleGenerisException(Exception e) {
        logger.error("Server error", e);
        var errorDto = new ErrorMessageResponse("Server error", e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthorizationException(AuthorizationDeniedException e) {
        logger.error("Handle authorization exception", e);
        var errorDto = new ErrorMessageResponse("Forbidden", e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorDto);
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthenticationException(AuthenticationException e) {
        logger.error("Handle authentication exception", e);
        var errorDto = new ErrorMessageResponse("Unauthorized", e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorDto);
    }

}
