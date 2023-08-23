package com.restaurant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.Date;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles application exceptions and returns an internal server error response.
     *
     * @param exception The application exception.
     * @return A ResponseEntity containing an ApplicationError.
     */
    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApplicationError> handleApplicationException(ApplicationException exception) {
        return createErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles application runtime exceptions and returns a bad request response.
     *
     * @param exception The application runtime exception.
     * @return A ResponseEntity containing an ApplicationError.
     */
    @ExceptionHandler(ApplicationRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApplicationError> handleApplicationRuntimeException(ApplicationRuntimeException exception) {
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates an error response entity for the given exception and HTTP status.
     *
     * @param exception The exception to handle.
     * @param httpStatus The HTTP status for the response.
     * @return A ResponseEntity containing an ApplicationError.
     */
    private ResponseEntity<ApplicationError> createErrorResponse(Exception exception, HttpStatus httpStatus) {
        ApplicationError applicationError = new ApplicationError(exception.getMessage(), String.valueOf(httpStatus.value()));
        applicationError.setTime(new Date());
        return new ResponseEntity<>(applicationError, httpStatus);
    }
}
