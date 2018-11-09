package com.example.android.inventoryapp;

/**
 * This ItemDetailsActivity class is based elements of the Pets App (Delete item, Cursor Loader, etc.)
 * Source Code found here: Source Code found here: https://github.com/udacity/ud845-Pets/
 */

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        //Update quantity when add/subtract buttons are pressed
        Button addButton = findViewById(R.id.plus);
        Button subtractButton = findViewById(R.id.minus);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Convert value in view into Integer
                int quantityInt = Integer.parseInt(quantity.getText().toString());

                quantityInt = quantityInt + 1;
                ContentValues values = new ContentValues();
                values.put(ItemEntry.COLUMN_QUANTITY, quantityInt);
                int rowsChanged = getContentResolver().update(selectedItemUri, values, null, null);
                //If successfully updated then update the textview so the user can see the change
                if (rowsChanged > 0) {
                    quantity.setText(Integer.toString(quantityInt));
                } else {
                    // If rowChanged is less than 1, then there was an error with the update.
                    Toast.makeText(getBaseContext(), R.string.error_insert, Toast.LENGTH_SHORT).show();
                }

            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Convert value in view into Integer
                int quantityInt = Integer.parseInt(quantity.getText().toString());
                if (quantityInt > 0) {
                    //quantityInt = quantityInt - 1;
                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_QUANTITY, quantityInt);
                    int rowsChanged = getContentResolver().update(selectedItemUri, values, null, null);
                    //If successfully updated then update the textview so the user can see the change
                    if (rowsChanged > 0) {
                        quantity.setText(Integer.toString(quantityInt));
                    } else {
                        // If rowChanged is less than 1, then there was an error with the update.
                        Toast.makeText(getBaseContext(), R.string.error_insert, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        //Launch phone intent when call supplier is pressed
        Button callButton = findViewById(R.id.call_supplier);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String supplierPhone = supPhone.getText().toString();
                if (supplierPhone != getString(R.string.phone_unkown)) {
                    Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + supplierPhone));
                    startActivity(call);
                } else {
                    Toast.makeText(getBaseContext(), R.string.no_supplier_phone, Toast.LENGTH_SHORT).show();
                }

            }
        });


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
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
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

            //Sets supplier name and if it is empty sets to filler string
            String supplierText = cursor.getString(supplierViewColumn);
            if (TextUtils.isEmpty(supplierText)) {
                supplierText = getString(R.string.unkown_supplier);
            }

            supplier.setText(supplierText);

            //Sets supplier phone and if it is empty sets to filler string
            String supplierPhone = cursor.getString(suppPhoneViewColumn);
            if (TextUtils.isEmpty(supplierPhone)) {
                supplierPhone = getString(R.string.phone_unkown);
            }
            supPhone.setText(supplierPhone);
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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        // Call the ContentResolver to delete the item at the given content URI.
        // Pass in null for the selection and selection args because the uri already points to what we want to delete
        int rowsDeleted = getContentResolver().delete(selectedItemUri, null, null);
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
        //close activity since item is now deleted
        finish();
    }
}
