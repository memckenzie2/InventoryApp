package com.example.android.inventoryapp.data;

/**
 * Implementation of this class is based on the Pet Shelter App from the Udacity Android Basics nano-degree.
 * Source Code found here: https://github.com/udacity/ud845-Pets/
 * <p>
 * This Contract class layouts the basics of the Inventory SQL Database for an inventory app
 */


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Implementation of this app is based on the Sunshine app from the Udacity Android Basics nano-degree.
 * Source Code found here: https://gist.github.com/udacityandroid/7450345062a5aa7371e6c30dab785ce7
 *
 */
//Defines table and column names for the inventory database.
public final class InventoryContract {

    //Content Authority String and base content string. This is a unqiue name that is used to
    // create the base of the URI that apps will use to contact the content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Path to the tables to be appended to base content URI
    public static final String PATH_INVENTORY = "inventory";

    /* Inner class that defines the table contents of the item inventory table */
    public static final class ItemEntry implements BaseColumns {

        //Full content URI to be used to contact URI for inventory tables
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of inventory items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item in the inventory.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

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