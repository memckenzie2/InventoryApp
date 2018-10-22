package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Import itemEntry class so you have access to column names, etc.
import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

//DB Helper class for Inventroy app. Manages database creation and version management.

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    //Name of the database file.
    private static final String DATABASE_NAME = "inventory.db";

    //Version of the database. If schema is updated, this must be incremented.
    private static final int DATABASE_VERSION = 1;

    //Constructor
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Called on creation of database
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create an inventory table for the database
        String SQL_CREATE_TABLE =  "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_PRICE + " DECIMAL NOT NULL, "
                + ItemEntry.COLUMN_QUANTITY + "INTEGER NOT NULL DEFAULT 0,"
                + ItemEntry.COLUMN_SUPPLIER + " TEXT, "
                + ItemEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TABLE);
    }

    //Called when database is upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Will update in future implementation when this has begun to move past version 1

    }
}
