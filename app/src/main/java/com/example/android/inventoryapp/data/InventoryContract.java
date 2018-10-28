package com.example.android.inventoryapp.data;
//SHOULD I UPDATE TO TWO TABLES - ONE FOR SUPPLIER????

import android.provider.BaseColumns;

/**
 *
 * Implementation of this app is based on the Sunshine app from the Udacity Android Basics nano-degree.
 * Source Code found here: https://gist.github.com/udacityandroid/7450345062a5aa7371e6c30dab785ce7
 *
 */
//Defines table and column names for the inventory database.
public final class InventoryContract {

    /* Inner class that defines the table contents of the item inventory table */
    public static final class ItemEntry implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "item";

        //Table Column constants
        //Unique ID of type INTEGER
        public static final String _ID = BaseColumns._ID;
        //Item selling price of type DECIMAL
        public static final String COLUMN_PRICE = "price";
        //Item's product name of type TEXT
        public static final String COLUMN_PRODUCT_NAME = "productname";
        //Quanitity of items. Of type INTEGER
        public static final String COLUMN_QUANTITY = "quantity";
        //Item's supplier name of type TEXT
        public static final String COLUMN_SUPPLIER = "supplier";
        //Item's supplier's phone number of type TEXT
        public static final String COLUMN_SUPPLIER_PHONE = "supplierphone";

        //Table defaults
        public static final int QUANTITY_UNKNOWN = 0;
    }
    

}