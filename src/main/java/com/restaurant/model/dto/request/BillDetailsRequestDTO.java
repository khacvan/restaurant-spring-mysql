package com.restaurant.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
public class BillDetailsRequestDTO {
    @NotNull(message = "Menu item ID must not be null")
    @Positive(message = "Menu item ID must be a positive number")
    private Integer menuItemId;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;
}
