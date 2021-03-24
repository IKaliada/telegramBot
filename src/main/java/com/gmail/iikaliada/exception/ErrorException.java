package com.gmail.iikaliada.exception;

public class ErrorException extends RuntimeException {

    public ErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorException(String message) {
        super(message);
    }
}
