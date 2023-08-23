package com.restaurant.validation;

import com.restaurant.exception.ApplicationRuntimeException;
import com.restaurant.model.entity.menu.MenuItem;

/**
 * Validator class for validating menu items.
 */
public class MenuItemValidator implements ApplicationValidation<MenuItem> {

    /**
     * Validates the data request for a menu item.
     *
     * @param menuItem The menu item to be validated.
     * @throws ApplicationRuntimeException If the validation fails.
     */
    @Override
    public void validateDataRequest(MenuItem menuItem) {
        if (menuItem.getName() == null || menuItem.getName().isEmpty()) {
            throw new ApplicationRuntimeException("Menu name must not be empty");
        }
        if (menuItem.getPrice() != null && menuItem.getPrice() < 0.0) {
            throw new ApplicationRuntimeException("Price must not be null and must be greater than 0");
        }
        if (menuItem.getInStock() == null || menuItem.getInStock() < 0) {
            throw new ApplicationRuntimeException("In stock must not be null and must be greater than or equal to 0");
        }
    }

    /**
     * Validates if a menu item can be deleted.
     *
     * @param menuItem The menu item to be validated for deletion.
     * @throws ApplicationRuntimeException If the validation for deletion fails.
     */
    @Override
    public void validateDelete(MenuItem menuItem) {
        if (!menuItem.getEnabled()) {
            throw new ApplicationRuntimeException("Can't delete disabled menu item");
        }
        if (menuItem.getBillDetails().stream().anyMatch(orderItem -> !orderItem.getBill().isPaid())) {
            throw new ApplicationRuntimeException("Cannot delete menu item with id " + menuItem.getId() + ". It exists in unpaid bills.");
        }
    }
}
