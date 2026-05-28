package com.sivaram.ecommapi.exception;

public class UnauthorizedException extends RuntimeException     {
    public UnauthorizedException(String message) {
        super(message);
    }
}
