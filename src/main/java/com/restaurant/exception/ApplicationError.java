package com.restaurant.exception;

import lombok.Data;
import java.util.Date;

/**
 * A class representing application-level errors.
 *
 * This class encapsulates information about an error that occurs within the application,
 * including the error message, status code, and the time the error occurred.
 */
@Data
public class ApplicationError {
    /**
     * The error message associated with the application error.
     */
    private String message;

    /**
     * The HTTP status code indicating the error.
     */
    private String status;

    /**
     * The timestamp when the error occurred.
     */
    private Date time;

    /**
     * Constructs an instance of ApplicationError with the provided message and status.
     *
     * @param message The error message.
     * @param status The HTTP status code associated with the error.
     */
    public ApplicationError(String message, String status) {
        super();
        this.message = message;
        this.status = status;
    }
}
