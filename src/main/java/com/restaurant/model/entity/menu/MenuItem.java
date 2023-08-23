package com.restaurant.model.entity.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.constant.MenuItemConstant;
import com.restaurant.model.entity.BaseEntity;
import com.restaurant.model.entity.bill.BillDetails;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = MenuItemConstant.MENU_TABLE_NAME)
public class MenuItem extends BaseEntity {
    @Column(length = 128, nullable = false, unique = true,name = MenuItemConstant.COLUMN_NAME)
    private String name;

    @Column(length = 128, name = MenuItemConstant.COLUMN_IMAGE)
    private String image;

    @Column(length = 254, name = MenuItemConstant.COLUMN_DESCRIPTION)
    private String description;

    @Column(name = MenuItemConstant.COLUMN_PRICE)
    private Double price;

    @Column(name = MenuItemConstant.COLUMN_IN_STOCK)
    private Integer inStock;

    @Column(name = MenuItemConstant.COLUMN_ENABLED)
    private Boolean enabled;

    @JsonIgnore
    @OneToMany(mappedBy = MenuItemConstant.MAPPED_BY_MENU_ITEM, cascade = CascadeType.ALL)
    private List<AdditionalDetails> additionalDetails = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = MenuItemConstant.MAPPED_BY_MENU_ITEM, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillDetails> billDetails = new ArrayList<>();
}
