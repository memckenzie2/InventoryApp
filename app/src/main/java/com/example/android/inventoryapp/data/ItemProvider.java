package com.example.android.inventoryapp.data;

/**
 * Implementation of this class is based on the Pet Shelter App from the Udacity Android Basics nano-degree.
 * Source Code found here: https://github.com/udacity/ud845-Pets/
 * <p>
 * This content provider was created using the template found here: https://gist.github.com/udacityandroid/7cf842c9f191f89559c333ef895bc415
 * <p>
 * Thic content provider delivers items from the inventory database to other classes or apps
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

/**
 * {@link ContentProvider} for Inventory app.
 */
public class ItemProvider extends ContentProvider {

    // Tag for the log messages
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();


    //URI matcher code for when we want the content URI for the inventory table
    private static final int INVENTORY = 100;

    //URI matcher code for when we want a single item from the inventory table
    private static final int INVENTORY_ID = 101;

    /* A URI matcher that matches the content URI with a code to determine
        if it is retrieveing a single inventory item or an entire table.
       The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // A static initializer that runs when this class is first called.
    static {

        //*UPDATE COMMENTS*
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        //*UPDATE COMMENTS*
        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);

        //*UPDATE COMMENTS*
        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    //Database helper object
    private InventoryDbHelper inventoryDbHelp;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        inventoryDbHelp = new InventoryDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database from the database helper
        SQLiteDatabase database = inventoryDbHelp.getReadableDatabase();

        // AS cursor to hold the results of the query
        Cursor cursor;

        // Returns the uriMatcher match code
        int match = uriMatcher.match(uri);

        //Switch statement to determine id the uriMatcher was able to match it to a recognized code
        switch (match) {
            //The case for when a subset of the inventory table is returned in the cursor and not just a single inventory record
            case INVENTORY:
                //Sets the ID to a question mark that we will replace will our selection arguments
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            //The case for when a single inventory record is contained in the cursor
            case INVENTORY_ID:
                //Sets the ID to a question mark that we will replace will our selection arguments
                selection = ItemEntry._ID + "=?";

                //Sets the number of selection arguments
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            //The default behaviour
            default:
                //URI not recognized so indicate error to user by thowing an exception
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    /**
     * Insert new data into the provider with the given ContentValues if trying to insert into full database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        /*Switch statement to determine id the uriMatcher was able to match it to a
        to a case where insertion is allowed (aka only for full databse not for individual item)*/
        switch (match) {
            case INVENTORY:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Inserts a new item into the inventory database with the contentvalue provided.
     * Returns new URI for the row where it was inserted.
     * Put this into separate method for easy of reading/logic flow
     */
    private Uri insertItem(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = inventoryDbHelp.getWritableDatabase();

        // Check that the product name is not null
        String productName = values.getAsString(ItemEntry.COLUMN_PRODUCT_NAME);

        if (productName == null) {
            throw new IllegalArgumentException("Product Name is required.");
        }

        // Check that the product name is not null
        Double price = values.getAsDouble(ItemEntry.COLUMN_PRICE);

        //Price must be present and greater than 0
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price is required.");
        }

        //Quantity and suppler name can be null as they are not required fields. If quantity is null defaults to 0
        // Check that the suppliers phone is valid
        String supplierPhone = values.getAsString(ItemEntry.COLUMN_SUPPLIER_PHONE);

        //supplierPhone is allowed to be null but if not null must be a valid phone number
        if (supplierPhone != null) {
            if (isPhoneValid(supplierPhone)) {
                throw new IllegalArgumentException("Valid Supplier Phone Number is required.");
            }
        }

        //Insert the item into a new row in the database and return the row ID
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);

        //If id is -1 then the insertion above failed and an error code should be logged and return null to indicate failure.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notifies of change so that the cursor knows there is a change so the information can be reloaded.
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the URI for the row we added (i.e. URI with ID appended)
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                //Pull out ID for item to know which row to update
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (if given).
     * Returns the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If no values will be updated, then don't try to update and return 0 rows
        if (values.size() == 0) {
            return 0;
        }

        //Ensure input is valid before updating. Makes sure each item exists before checking
        if (values.containsKey(ItemEntry.COLUMN_PRODUCT_NAME)) {
            // Check that the product name is not null
            String productName = values.getAsString(ItemEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Product Name is required.");
            }
        }

        if (values.containsKey(ItemEntry.COLUMN_PRICE)) {
            // Check that the product name is not null
            Double price = values.getAsDouble(ItemEntry.COLUMN_PRICE);

            //Price must be present and greater than 0
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Price is required.");
            }
        }

        if (values.containsKey(ItemEntry.COLUMN_SUPPLIER_PHONE)) {
            // Check that the suppliers phone is valid
            String supplierPhone = values.getAsString(ItemEntry.COLUMN_SUPPLIER_PHONE);

            //supplierPhone is allowed to be null but if not null must be a valid phone number
            if (supplierPhone != null) {
                if (isPhoneValid(supplierPhone)) {
                    throw new IllegalArgumentException("Valid Supplier Phone Number is required.");
                }
            }
        }

        //Open writable database and update table
        SQLiteDatabase databaseUpdate = inventoryDbHelp.getWritableDatabase();

        //Update the database and return number of rows updated
        int rowsUpdated = databaseUpdate.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        //If update was made, notify of change.
        if (rowsUpdated != 0) {
            //Notifies of change so that the cursor knows there is a change so the information can be reloaded.
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Returns number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = inventoryDbHelp.getWritableDatabase();

        //Track number of rows deleted/removed
        int rowsRemoved = 0;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // Delete all rows that match the selection and selection args
                rowsRemoved = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                // Delete a single row given by the ID in the URI. Pulls ID from URI to determine what to delete.
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsRemoved = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsRemoved != 0) {
            //Notifies of change so that the cursor knows there is a change so the information can be reloaded.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsRemoved;
    }

    /**
     * Returns the MIME type of data for the content URI.
     * */
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            //The case for a list from the inventory
            case INVENTORY:
                return ItemEntry.CONTENT_LIST_TYPE;

            //The case for a single inventory item
            case INVENTORY_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    public boolean isPhoneValid(String phone) {
        return !PhoneNumberUtils.isGlobalPhoneNumber(phone);
    }
}