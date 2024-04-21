package com.narvane.gateway.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InvalidUserException extends RuntimeException {

    @Getter
    private final HttpStatus httpStatus;

    public InvalidUserException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static class UserAlreadyExistException extends InvalidUserException {
        public UserAlreadyExistException() {
            super("user.already.exist", HttpStatus.CONFLICT);
        }

    }

    public static class CredentialsException extends InvalidUserException {

        public CredentialsException() {
            super("not.valid.credentials", HttpStatus.FORBIDDEN);
        }

        public CredentialsException(String message) {
            super(message, HttpStatus.UNAUTHORIZED);
        }

    }

    public static class LoginNotFoundException extends CredentialsException {

        public LoginNotFoundException() {
            super("login.not.found");
        }

    }

    public static class WrongPasswordException extends CredentialsException {

        public WrongPasswordException() {
            super("wrong.password");
        }

    }

    public static class MissingAuthException extends CredentialsException {

        public MissingAuthException() {
            super("missing.authorization");
        }

    }


}
