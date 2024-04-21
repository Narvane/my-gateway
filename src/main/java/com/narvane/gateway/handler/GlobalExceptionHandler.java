package com.narvane.gateway.handler;


import com.narvane.gateway.model.exception.InvalidUserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @AllArgsConstructor
    @Data public static class JsonExceptionModel { private String message; }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonExceptionModel> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JsonExceptionModel(ex.getMessage()));
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<JsonExceptionModel> handleInvalidUserException(InvalidUserException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new JsonExceptionModel(
                        messageSource.getMessage(ex.getMessage(),
                                null, LocaleContextHolder.getLocale()
                        )
                ));
    }

}
