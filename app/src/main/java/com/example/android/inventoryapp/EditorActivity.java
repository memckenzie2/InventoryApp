package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;


public class EditorActivity extends AppCompatActivity {

    //EditText fields from the "Add an Item to Inventory" screen activity_editor.xml
    private EditText editName;
    private EditText editPrice;
    private EditText editQuantity;
    private EditText editSupplier;
    private EditText editSupPhone;

    private void insertItem(){
        //Retrieves user input from Edit Text fields
        String productName = editName.getText().toString().trim();
        String productPrice = editPrice.getText().toString().trim();
        String productQuantity = editQuantity.getText().toString().trim();
        String productSupplier = editSupplier.getText().toString().trim();
        String productSupPhone = editSupPhone.getText().toString().trim();

        //Convert numerical values from string to appropriate type
        Double productPriceDouble = Double.parseDouble(productPrice);
        Integer productQuantityInt = Integer.parseInt(productQuantity) ;

        //Create database helper and pass it current context
        InventoryDbHelper inventoryDbHelper = new InventoryDbHelper(this);

        // Set the database inventory.dp to be in write mode
        SQLiteDatabase dbInventory = inventoryDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys and item attributes are the values.
        //Item attributes are pulled
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ItemEntry.COLUMN_PRICE, productPrice);
        values.put(ItemEntry.COLUMN_QUANTITY, productQuantity);
        values.put(ItemEntry.COLUMN_SUPPLIER, productSupplier);
        values.put(ItemEntry.COLUMN_SUPPLIER_PHONE, productSupPhone);

        //Create a new row to store the Item attributes in. Return long to check for error code.
        long newRowId = dbInventory.insert(ItemEntry.TABLE_NAME, null, values);

        // Show a toast message to indicate if the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving inventory item " + productName+ ". Please retry.", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Inventory item "+ productName+ " saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }

    }

    //Adds menu to Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    //Adds functionality to respond to click by a user in the menu in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Fill in
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Fill in
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}


