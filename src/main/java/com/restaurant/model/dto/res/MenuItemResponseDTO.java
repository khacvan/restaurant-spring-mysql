package com.restaurant.model.dto.res;

import com.restaurant.model.entity.menu.AdditionalDetails;
import lombok.*;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponseDTO {
    private Integer id;

    private String name;

    private String image;

    private String description;

    private Double price;

    private Date createdTime;

    private Date updatedTime;

    private Integer inStock;

    private Boolean enabled;

    private List<AdditionalDetails> additionalDetails;
}
