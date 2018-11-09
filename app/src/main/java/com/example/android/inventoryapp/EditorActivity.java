package com.example.android.inventoryapp;

/**
 * Implementation of this app is based on the Pet Shelter App from the Udacity Android Basics nano-degree.
 * Source Code found here: https://github.com/udacity/ud845-Pets/
 *
 * This activity takes input from the user and inputs it into the inventory.db database us the innventoryDbHelper class.
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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    //EditText fields from the "Add an Item to Inventory" screen activity_editor.xml
    private EditText editName;
    private EditText editPrice;
    private EditText editQuantity;
    private EditText editSupplier;
    private EditText editSupPhone;
    //Listen for changes in form
    private boolean itemEntryHasChanged = false;

    //OnTouch Listener to see if user interacted with view
    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemEntryHasChanged = true;
            return false;
        }
    };

    /**
     * Identifier for the item data loader
     */
    private static final int EXISTING_ITEM_LOADER = 44;
    /**
     * Content URI for an existing item. This will be null if the user is adding a new item to the inventory.
     */
    private Uri selectedItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Inflate Editor view activity_editor.xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Pull data from intent on load
        Intent intent = getIntent();
        selectedItemUri = intent.getData();

        if (selectedItemUri == null) {
            // This is a new item for the inventory, so change the app bar to say "Add Inventory Item"
            setTitle(getString(R.string.action_save_item));

        } else {
            //Edit an existing item in the inventroy so update title
            setTitle(getString(R.string.action_edit_item));

            //Turn off clear form option in menu so that user doesn't accidentally clear and save a blank entry
            invalidateOptionsMenu();

            //Load data from the database into the fields
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        //EditText fields from the "Add an Item to Inventory" screen activity_editor.xml
        editName = findViewById(R.id.product_name);
        editPrice= findViewById(R.id.price);
        editQuantity= findViewById(R.id.quantity);
        editSupplier= findViewById(R.id.supplier_name);
        editSupPhone= findViewById(R.id.supplier_phone);

        //Set on touch listener to determine if any changes might have been made
        editName.setOnTouchListener(touchListener);
        editPrice.setOnTouchListener(touchListener);
        editQuantity.setOnTouchListener(touchListener);
        editSupplier.setOnTouchListener(touchListener);
        editSupPhone.setOnTouchListener(touchListener);
    }

    private boolean saveItem(){
        //Retrieves user input from Edit Text fields
        String productName = editName.getText().toString().trim();
        String productPrice = editPrice.getText().toString().trim();
        String productQuantity = editQuantity.getText().toString().trim();
        String productSupplier = editSupplier.getText().toString().trim();
        String productSupPhone = editSupPhone.getText().toString().trim();

        //Check for new item vs editing existing item and ensure fields are completed
        if (selectedItemUri == null && TextUtils.isEmpty(productName) && TextUtils.isEmpty(productPrice) && TextUtils.isEmpty(productQuantity) && TextUtils.isEmpty(productSupplier) && TextUtils.isEmpty(productSupPhone)) {
            //Everything is empty so we don't want to create a new item or save
            Toast.makeText(this, R.string.missing_input, Toast.LENGTH_SHORT).show();
            return false;
        }

        //Ensures require field are not blank - name, price, and quantity
        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productQuantity)) {
            //Everything is empty so we don't want to create a new item or save
            Toast.makeText(this, R.string.missing_input, Toast.LENGTH_SHORT).show();
            return false;
        }

        Integer productQuantityInt = 0;
        Double productPriceDouble = Double.MAX_VALUE;


        //Check that nothing that must be parsed is empty before parsin
        //Quantity
        if (!TextUtils.isEmpty(productQuantity)) {
            productQuantityInt = Integer.parseInt(productQuantity);
        }
        //Price
        if (!TextUtils.isEmpty(productPrice)) {
            productPriceDouble = Double.parseDouble(productPrice);
        }

        //Supplier Phone should be null if it is empty
        if (TextUtils.isEmpty(productSupPhone)) {
            productSupPhone = null;
        }
        //If not a valid phone number inform user to re-enter
        else if (productSupPhone.length() != 12) {
            //TODO Do proper check for valid phone number using regex
            Toast.makeText(this, R.string.valid_phone, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Create a ContentValues object where column names are the keys and item attributes are the values.
        //Item attributes are pulled
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ItemEntry.COLUMN_PRICE, productPriceDouble);
        values.put(ItemEntry.COLUMN_QUANTITY, productQuantityInt);
        values.put(ItemEntry.COLUMN_SUPPLIER, productSupplier);
        values.put(ItemEntry.COLUMN_SUPPLIER_PHONE, productSupPhone);

        if (selectedItemUri == null) {
            //Create a new row to store the Item attributes in. Return long to check for error code.
            Uri newRowURI = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

            // Show a toast message to indicate if the insertion was successful
            if (newRowURI == null) {
                // If the row ID is null, then there was an error with insertion.
                Toast.makeText(this, R.string.error_insert, Toast.LENGTH_SHORT).show();

            } else {
                //Clear user's input from form
                editName.setText("");
                editPrice.setText("");
                editQuantity.setText("");
                editSupplier.setText("");
                editSupPhone.setText("");

                // The insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, R.string.insert, Toast.LENGTH_SHORT).show();
            }
        } else {
            //This is an existing item that has been edited.
            //Check that something was actually changed
            int rowsChanged = getContentResolver().update(selectedItemUri, values, null, null);

            //If no change
            if (rowsChanged == 0) {
                Toast.makeText(this, R.string.error_insert, Toast.LENGTH_SHORT).show();
            }
            //If a row is changed
            else {
                Toast.makeText(this, R.string.insert, Toast.LENGTH_SHORT).show();
            }
        }
       return true;
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
                //Repeatedly try to insert item each time user hits save.
                boolean saved = false;
                //Try to insert the item and finish activity if successful
                saved = saveItem();
                if(saved == true){
                    finish();
                }
                return true;
            // Respond to a click on the "Clear Form" menu option
            case R.id.action_delete:
                //Clear user's input from form
                editName.setText("");
                editPrice.setText("");
                editQuantity.setText("");
                editSupplier.setText("");
                editSupPhone.setText("");
                return true;
            //Overides the up button press to ensure that the dialog for showUnsaveChangesDialog displays
            case android.R.id.home:
                // If no touch/changes detected, use home button as normal
                if (!itemEntryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // If there may be unsaved changes, setup a dialog to warn the user using showUnsavedChangesDialog
                // Create a click listener to determine if user selected that changes should be discarded.
                //Only need to monitor discard button since if the other is selected we stay in the activity and make no changes.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // If user clicked "Discard" button then continue with closing the current activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show dialog that there are unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
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
            editName.setText(cursor.getString(productViewColumn));
            editPrice.setText(Double.toString(cursor.getDouble(priceViewColumn)));
            editQuantity.setText(Integer.toString(cursor.getInt(quantityViewColumn)));
            editSupplier.setText(cursor.getString(supplierViewColumn));
            editSupPhone.setText(cursor.getString(suppPhoneViewColumn));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Clear all input from form
        editName.setText("");
        editPrice.setText("");
        editQuantity.setText("");
        editSupplier.setText("");
        editSupPhone.setText("");
    }

    //Create a dialogue in instances where user may have made changes and is trying to navigate away from editor view
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_msg);
        //Positive means you discard edits
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        //Negative means they may want to save changes first
        builder.setNegativeButton(R.string.keep_changes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "negative" button, so dismiss message and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog as defined above
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Overide back button press to ensure that the dialog for showUnsaveChangesDialog displays
    @Override
    public void onBackPressed() {
        // If no touch/changes detected, use back button as normal
        if (!itemEntryHasChanged) {
            super.onBackPressed();
            return;
        }

        // If there may be unsaved changes, setup a dialog to warn the user using showUnsavedChangesDialog
        // Create a click listener to determine if user selected that changes should be discarded.
        //Only need to monitor discard button since if the other is selected we stay in the activity and make no changes.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // If user clicked "Discard" button then continue with closing the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }



}


