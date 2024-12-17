package com.ptit.e_commerce_website_be.do_an_nhom.configs;

public class Constant {
    //cookie
    public static final int COOKIE_EXPIRATION_TIME = 86400;

    //img
    public static final int MAXIMUM_IMAGES_PER_PRODUCT = 5;

    // excel
    public static final String xlsx = ".xlsx";
    public static final String HEADER_ALL_LIST_INVENTORY[] = {"id","product id", "name", "sku code", "price", "import price", "quantity", "warehouse", "create at"};
    public static final String HEADER_ALL_LIST_IMPORT[] = {"id","product id", "name", "sku code", "price", "import price", "quantity", "warehouse", "supplier", "location",  "create at"};
    public static String SHEET_NAME = "sheetForInventoryData";
    public static final String HEADER_ALL_LIST_PRODUCT[] = {"id", "name", "min price", "total sold", "brand", "categories", "image", "rate star", "quantity", "description",  "create at"};
}

