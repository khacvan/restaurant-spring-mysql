package com.restaurant.exception;

import java.io.Serial;

public class ApplicationRuntimeException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public ApplicationRuntimeException(String message) {
        super(message);
    }
}
