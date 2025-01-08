package com.spring.exceptions;

public class JwtParseFailException extends RuntimeException {

    public JwtParseFailException(String message) {
        super(message);
    }

    public JwtParseFailException(String message, Throwable cause) {
        super(message, cause);
    }
}