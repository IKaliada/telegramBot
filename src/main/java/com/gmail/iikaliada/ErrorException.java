package com.gmail.iikaliada;

public class ErrorException extends RuntimeException {
    public ErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
