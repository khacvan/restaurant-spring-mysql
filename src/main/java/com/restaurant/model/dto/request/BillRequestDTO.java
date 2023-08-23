package com.restaurant.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BillRequestDTO {
    private Integer id;

    @Valid
    @NotNull(message = "Order items must not be null")
    private List<BillDetailsRequestDTO> billDetailsRequestDTOS;
}
