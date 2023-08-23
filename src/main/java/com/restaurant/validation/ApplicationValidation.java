package com.restaurant.validation;

public interface ApplicationValidation<T> {
    /**
     * Validates the data request for the specified object.
     *
     * @param obj The object to be validated.
     */
    void validateDataRequest(T obj);

    /**
     * Validates the deletion request for the specified object.
     *
     * @param obj The object to be validated for deletion.
     */
    void validateDelete(T obj);
}
