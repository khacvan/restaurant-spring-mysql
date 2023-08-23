package com.restaurant.model.entity.bill;

import com.restaurant.constant.BillConstant;
import com.restaurant.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = BillConstant.TABLE_NAME)
public class Bill extends BaseEntity {
    @OneToMany(mappedBy = BillConstant.MAPPED_BY_BILL, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillDetails> billDetails = new ArrayList<>();

    @Column(name = BillConstant.COLUMN_TOTAL_PRICE)
    private Double totalPrice;

    @Getter(value = AccessLevel.NONE)
    @Column(name = BillConstant.COLUMN_IS_PAID)
    private Boolean isPaid;

    public Boolean isPaid() {
        return isPaid;
    }
}
