package com.restaurant.validation;

import com.restaurant.exception.ApplicationRuntimeException;
import com.restaurant.model.entity.bill.Bill;
import com.restaurant.util.DateUtils;
import java.util.Date;

/**
 * Validator class for validating bills.
 */
public class BillValidator implements ApplicationValidation<Bill> {

    /**
     * Validates the data request for a bill.
     *
     * @param bill The bill to be validated.
     * @throws ApplicationRuntimeException If the validation fails.
     */
    @Override
    public void validateDataRequest(Bill bill) {
        if (bill.getBillDetails().isEmpty()) {
            throw new ApplicationRuntimeException("Cannot create bill with empty order items.");
        }
    }

    /**
     * Validates if a bill can be deleted.
     *
     * @param bill The bill to be validated for deletion.
     * @throws ApplicationRuntimeException If the validation for deletion fails.
     */
    @Override
    public void validateDelete(Bill bill) {
        if (bill == null) {
            throw new ApplicationRuntimeException("Bill with the given ID does not exist.");
        }
        if (bill.isPaid()) {
            throw new ApplicationRuntimeException("Cannot delete a paid bill.");
        }
        if (DateUtils.getDaysToDelete(bill.getCreatedDate(), new Date()) < 14) {
            throw new ApplicationRuntimeException("Bill cannot be deleted as it was created less than 2 weeks ago.");
        }
    }
}
