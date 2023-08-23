package com.restaurant.model.dto.request;

import com.restaurant.model.entity.menu.AdditionalDetails;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MenuRequestDTO {
    private Integer id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Image URL must not be blank")
    private String image;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than or equal to 0.01")
    @DecimalMax(value = "9999.99", message = "Price must be less than or equal to 9999.99")
    private Double price;

    @NotNull(message = "In-stock quantity must not be null")
    @Min(value = 0, message = "In-stock quantity must be greater than or equal to 0")
    @Max(value = 1000, message = "In-stock quantity must be less than or equal to 1000")
    private Integer inStock;

    @Size(max = 5, message = "Additional details must not exceed 5 items")
    private List<AdditionalDetails> additionalDetails;
}
