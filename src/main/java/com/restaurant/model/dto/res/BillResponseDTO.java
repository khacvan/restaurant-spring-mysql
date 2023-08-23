package com.restaurant.model.dto.res;

import com.restaurant.model.entity.bill.BillDetails;
import lombok.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {
    private Integer id;

    private Double totalPrice;

    private Date createdDate;

    private Date updatedDate;

    private Boolean isPaid;

    private List<BillDetails> billDetails;
}
