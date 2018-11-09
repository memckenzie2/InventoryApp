package com.example.android.inventoryapp;

/**
 * Implementation of this class is based on the Pet Shelter App from the Udacity Android Basics nano-degree.
 * Source Code found here: https://github.com/udacity/ud845-Pets/
 * 
 * This Cursor Adapter is used to create a list of inventory items
 */


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context of the app
     * @param c       The cursor object containing the data
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new list item view with no data bound to the views so that it is blank.
     *
     * @param context The context of the app
     * @param cursor  The cursor object containing the data with the pointer pointing at the correct location
     * @param parentView  The parent view that this new view is attached to @return the newly created blank list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parentView) {
        //Inflates the balnk list view to be returned
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parentView, false);
    }

    /**
     * Binds the inventory item's data from the current cursor row pointed to the R.layout.inventory_item.
     *
     * @param itemView    Existing view, the view inflated and returned by newView() method
     * @param context The context of the app
     * @param cursor  The cursor object containing the data with the pointer pointing at the correct location
     */
    @Override
    public void bindView(View itemView, final Context context, final Cursor cursor) {

        final Context mContext = context;

        //Find views from inventory_item.xml
        TextView productView = itemView.findViewById(R.id.product_list);
        TextView priceView = itemView.findViewById(R.id.price_list);
        TextView quantityView = itemView.findViewById(R.id.quantity_list);
        Button saleButton = itemView.findViewById(R.id.sale_button);

        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int quantityViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_QUANTITY);
                int idColumn = cursor.getColumnIndex(InventoryContract.ItemEntry._ID);

                int productQuantityInt = cursor.getInt(quantityViewColumn);
                int id = cursor.getInt(idColumn);
                Uri itemURI = ContentUris.withAppendedId(InventoryContract.ItemEntry.CONTENT_URI, id);
                if (productQuantityInt <= 0) {
                    Toast.makeText(context, R.string.sold_out, Toast.LENGTH_SHORT).show();
                } else {
                    productQuantityInt = productQuantityInt - 1;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.ItemEntry.COLUMN_QUANTITY, productQuantityInt);
                    mContext.getContentResolver().update(itemURI, values, null, null);
                    Toast.makeText(context, R.string.item_sold, Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Finds the correct column for each inventory item's attributes
        int productViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRODUCT_NAME);
        int priceViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRICE);
        int quantityViewColumn = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_QUANTITY);

        //Updates the view with the inventory item's data from the cursor object
        productView.setText(cursor.getString(productViewColumn));
        priceView.setText(Double.toString(cursor.getDouble(priceViewColumn)));
        quantityView.setText(Integer.toString(cursor.getInt(quantityViewColumn)));
    }
}