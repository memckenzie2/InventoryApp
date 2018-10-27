package com.example.android.inventoryapp;

/**
 * This InventoryAdapter class is based upon the Adapter class introduced in the Miwok App.
 * Source Code found here: https://github.com/udacity/ud839_Miwok
 * <p>
 * Also based upon previously submitted AudioBook and Library Tour App. Source Code found
 * * here: https://github.com/memckenzie2/AudiobookApp
 * * and here: https://github.com/memckenzie2/CentralLibraryTour
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class InventoryAdapter extends ArrayAdapter<InventoryItem> {
    public InventoryAdapter(@NonNull Context context, ArrayList<InventoryItem> loc) {
        super(context, 0, loc);
    }

    @Override
    public View getView(final int locationPosition, View convertableView, ViewGroup parentView) {
        View itemView = convertableView;
        //Inflates a view if no view has been made or reuses current view if it has.
        //Check to see if view exists
        if (itemView == null) {
            //inflates view
            itemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.inventory_item, parentView, false);
        }

        // Get the InventoryItem object for the given position in the list
        final InventoryItem currentItem = getItem(locationPosition);

        //Find views from inventory_item.xml
        TextView productView = itemView.findViewById(R.id.product);
        TextView priceView = itemView.findViewById(R.id.price);

        TextView quantityView = itemView.findViewById(R.id.quantity);

        TextView supplierView = itemView.findViewById(R.id.supplier);

        TextView suppPhoneView = itemView.findViewById(R.id.supplier_phone);


        //Updates the view with the inventory item's data.
        productView.setText(currentItem.getProductName());
        priceView.setText(Double.toString(currentItem.getPrice()));
        quantityView.setText(Integer.toString(currentItem.getQuantity()));
        supplierView.setText(currentItem.getSupplier());
        suppPhoneView.setText(currentItem.getSupplierPhone());
        return itemView;
    }
}
