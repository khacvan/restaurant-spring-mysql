package com.restaurant.model.entity.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.constant.BillDetailsConstant;
import com.restaurant.model.entity.BaseEntity;
import com.restaurant.model.entity.menu.MenuItem;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = BillDetailsConstant.TABLE_NAME)
public class BillDetails extends BaseEntity {
    @Column(name = BillDetailsConstant.COLUMN_QUANTITY)
    private Integer quantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = BillDetailsConstant.COLUMN_BILL_ID)
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = BillDetailsConstant.COLUMN_MENU_ITEM_ID)
    private MenuItem menuItem;

    @Column(name = BillDetailsConstant.COLUMN_UNIT_PRICE)
    private Double unitPrice;
}
