package com.nimbleways.springboilerplate.exceptions;

public class UnsupportedProductTypeException extends RuntimeException {
    public UnsupportedProductTypeException(String message) {
        super(message);
    }
}
