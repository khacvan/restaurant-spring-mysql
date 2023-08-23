package com.restaurant.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorHelper {
    /**
     * Extracts and returns a list of error messages from a BindingResult object.
     *
     * @param bindingResult The BindingResult containing validation errors.
     * @return A list of error messages.
     */
    public static List<String> getAllError(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
