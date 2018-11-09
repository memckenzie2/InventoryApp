package com.example.android.inventoryapp;

/**
 * An inventory app that stores data input by the user in a database and displays the current databas contents to a list.

 * Implementation of this app is based on the projects from the Udacity Android Basics nano-degree.
 * Each class has a list of the apps and or other projects that laid the foundation for that class including a link to the relevant source codes.
 *
 * This MainActivity class is based upon the layout introduced for the Pets App
 * Source Code found here: Source Code found here: https://github.com/udacity/ud845-Pets/
 */

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.ItemEntry;


//Import itemEntry class so you have access to column names, etc.

//Public Domain Image Sourced from https://commons.wikimedia.org/wiki/File:US-UK-blend.png

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * TextView that is displayed when the inventory list is empty
     */
    private TextView emptyStateView;
    private ListView listView;

    private static final int INVENTORY_LOADER = 22;
    InventoryCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use main_activity.xml as layout.
        setContentView(R.layout.inventory_list);

        //Setup empty state view
        emptyStateView = findViewById(R.id.empty_state_view);

        //Finds list view to populate with inventory list
        listView = findViewById(R.id.inventory_list);

        //Sets an empty view for when their is no inventory list
        listView.setEmptyView(emptyStateView);

        //Create instance of adapter for a list of items in the inventory but with a null cursor.
        adapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(adapter);

        // Setup an item click listener for when an item is selected.
        //Launches a full item details view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Create an intent that displays the full item view
                Intent intent = new Intent(MainActivity.this, ItemDetailsActivity.class);

                //Createa content URI for the item clicked on
                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                //Set the URI as the data field of the intent so that the ItemDetailView will display its details
                intent.setData(currentItemUri);

                //Launch the ItemDetailActivity
                startActivity(intent);
            }
        });

        //Create Cursor Loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
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

    // Called when a new Cursor Loader needs to be created and returns a cursor loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_PRODUCT_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
        };

        return new CursorLoader(this, ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    // Called when a previously created loader has finished loading. Swaps cursor for new cursor with updated data.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Swaps for cursor with updated data
        adapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable. Swaps the cursor for a null one.
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Ensures that the cursor gets closed out so we are no longer using it.
        adapter.swapCursor(null);
    }
}