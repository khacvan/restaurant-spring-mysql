package com.restaurant.exception;

import java.io.Serial;

public class ApplicationException extends Exception{
    @Serial
    private static final long serialVersionUID = 1L;
    public ApplicationException(String message) {
        super(message);
    }
}

