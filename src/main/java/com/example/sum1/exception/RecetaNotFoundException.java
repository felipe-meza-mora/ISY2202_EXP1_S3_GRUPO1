
package com.example.sum1.exception;

public class RecetaNotFoundException extends RuntimeException {
    public RecetaNotFoundException(String message) {
        super(message);
    }
}