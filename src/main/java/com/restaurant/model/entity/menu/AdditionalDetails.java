package com.restaurant.model.entity.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.constant.AdditionalDetailsConstant;
import com.restaurant.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(name = AdditionalDetailsConstant.TABLE_NAME)
public class AdditionalDetails extends BaseEntity {
    @Column(length = 255, name = AdditionalDetailsConstant.COLUMN_NAME, nullable = false)
    private String name;

    @Column(length = 255, name = AdditionalDetailsConstant.COLUMN_VALUE)
    private String value;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;
}
