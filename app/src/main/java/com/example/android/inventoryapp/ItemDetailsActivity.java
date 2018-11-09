package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

public class ItemDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the item data loader
     */
    private static final int EXISTING_ITEM_LOADER = 44;
    //EditText fields from the "Add an Item to Inventory" screen activity_editor.xml
    private TextView name;
    private TextView price;
    private TextView quantity;
    private TextView supplier;
    private TextView supPhone;
    /**
     * Content URI for an existing item. This will be null if the user is adding a new item to the inventory.
     */
    private Uri selectedItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Inflate Editor view activity_editor.xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_item_record);

        //Pull data from intent on load
        Intent intent = getIntent();
        selectedItemUri = intent.getData();

        //Text fields from the "Item Description" screen inventory_item_record.xml
        name = findViewById(R.id.product_name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        supplier = findViewById(R.id.supplier_name);
        supPhone = findViewById(R.id.supplier_phone);

        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);

    }

    //Adds menu to Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    //Adds functionality to respond to click by a user in the menu in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit:
                //Launch edit activity
                //Create an intent that displays the full item view
                Intent intent = new Intent(ItemDetailsActivity.this, EditorActivity.class);

                //Set the URI as the data field of the intent so that the ItemDetailView will display its details
                intent.setData(selectedItemUri);

                //Launch the ItemDetailActivity
                startActivity(intent);
                return true;

            // Respond to a click on the "Clear Form" menu option
            case R.id.action_delete:
                //TODO Delete item from database
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Create a projection of all items since they will all be displayed at once.
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_PRODUCT_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
                ItemEntry.COLUMN_SUPPLIER,
                ItemEntry.COLUMN_SUPPLIER_PHONE
        };

        return new CursorLoader(this, selectedItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Must move cursor to first row (only item in cursor) if it is non-null
        if (cursor.moveToFirst()) {

            //Finds the correct column for each inventory item's attributes
            int productViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRODUCT_NAME);
            int priceViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRICE);
            int quantityViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_QUANTITY);
            int supplierViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_SUPPLIER);
            int suppPhoneViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_SUPPLIER_PHONE);

            //Updates the view with the inventory item's data from the cursor object
            name.setText(cursor.getString(productViewColumn));
            price.setText(Double.toString(cursor.getDouble(priceViewColumn)));
            quantity.setText(Integer.toString(cursor.getInt(quantityViewColumn)));
            supplier.setText(cursor.getString(supplierViewColumn));
            supPhone.setText(cursor.getString(suppPhoneViewColumn));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //Clear all input from form
        name.setText("");
        price.setText("");
        quantity.setText("");
        supplier.setText("");
        supPhone.setText("");
    }
}
