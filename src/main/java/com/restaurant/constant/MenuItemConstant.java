package com.restaurant.constant;

public final class MenuItemConstant {
    public static final String MENU_TABLE_NAME = "menu_items";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IN_STOCK = "in_stock";
    public static final String COLUMN_ENABLED = "enabled";
    public static final String MAPPED_BY_MENU_ITEM = "menuItem";
    public static final String URL_IMAGE_DEFAULT = "https://s3-ap-southeast-1.amazonaws.com/interview.ampostech.com/backend/restaurant/";

    private MenuItemConstant() {}
}
