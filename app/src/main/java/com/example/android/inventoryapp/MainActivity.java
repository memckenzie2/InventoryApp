package com.example.android.inventoryapp;

/**
 * An inventory app that stores data inputby the user in a database.

 * Implementation of this app is based on the projects from the Udacity Android Basics nano-degree.
 * Each class has a list of the apps and or other projects that laid the foundation for that class including a link to the relevant source codes.
 *
 * This MainActivity class is based upon the layout introduced for the Earthquake App.
 * Source Code found here: https://github.com/udacity/ud843-QuakeReport
 *
 * t
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryDbHelper;
//Import itemEntry class so you have access to column names, etc.
import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;

import java.util.ArrayList;

//Public Domain Image Sourced from https://commons.wikimedia.org/wiki/File:US-UK-blend.png

public class MainActivity extends AppCompatActivity {

    /**
     * TextView that is displayed when the inventory list is empty
     */
    private TextView emptyStateView;
    private ListView listView;

    //Database helper to give access to the inventory.db database
    private InventoryDbHelper dbHelper;
    private ArrayList<InventoryItem> inventoryItemArrayList;
    private InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use main_activity.xml as layout.
        setContentView(R.layout.inventory_list);

        //Setup empty state view
        emptyStateView = findViewById(R.id.empty_state_view);

        // Instantiate our database helper class that give us acces to SQLiteOpenHelper
        dbHelper = new InventoryDbHelper(this);

        //Create an ArrayList of InventoryItems to store the inventory in
        inventoryItemArrayList = new ArrayList<InventoryItem>();

        //Targets the ListView in inventory_list.xml
        listView = (ListView) findViewById(R.id.inventory_list);
        adapter = new InventoryAdapter(this, inventoryItemArrayList);

        displayDatabase();

        //Display the InventoryAdapter object we created above in a ListView in inventory_list.xml
        listView.setEmptyView(emptyStateView);
        listView.setAdapter(adapter);


    }


    private void displayDatabase(){
        //Open (or create) database to read query from.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String [] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_PRODUCT_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
                ItemEntry.COLUMN_SUPPLIER,
                ItemEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(ItemEntry.TABLE_NAME, projection, null, null, null, null, null);
        cursor.moveToFirst();

        Integer indID;
        Integer indProdName;
        Integer indPrice;
        Integer indQuant;
        Integer indSupplier;
        Integer indSuppPhone;

        String id;
        String product;
        Double price;
        Integer quantity;
        String supplier;
        String supplierPhone;

        while(cursor.moveToNext()){
            //Add inventory Item to a listView
            indID = cursor.getColumnIndex(ItemEntry._ID);
            indProdName = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_NAME);
            indPrice = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
            indQuant = cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY);
            indSupplier = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER);
            indSuppPhone = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_PHONE);

            id = cursor.getString(indID);
            product = cursor.getString(indProdName);
            price = cursor.getDouble(indPrice);
            quantity = cursor.getInt(indQuant);
            supplier = cursor.getString(indSupplier);
            supplierPhone= cursor.getString(indSuppPhone);

            InventoryItem newItem = new InventoryItem(id, product, price, quantity, supplier, supplierPhone);
            inventoryItemArrayList.add(newItem);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    //Determines which option the user selected and launches appropriate response
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //If user selected add net item to inventory, launch EditorActvity
        if (id == R.id.action_insert_item) {
            Intent editorIntent = new Intent(this, EditorActivity.class);
            startActivity(editorIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}