package com.example.android.inventoryapp;

/**
 * An inventory app that stores data inputby the user in a database.

 * Implementation of this app is based on the projects from the Udacity Android Basics nano-degree.
 * Each class has a list of the apps and or other projects that laid the foundation for that class including a link to the relevant source codes.
 *
 * This MainActivity class is based upon the layout introduced for the Earthquake App.
 * Source Code found here: https://github.com/udacity/ud843-QuakeReport
 */

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

//Public Domain Image Sourced from https://commons.wikimedia.org/wiki/File:US-UK-blend.png

public class MainActivity extends AppCompatActivity {


    /**
     * TextView that is displays the status of the inventory database
     */
    private TextView databaseStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use main_activity.xml as layout.
        setContentView(R.layout.activity_main);

        //Setup empty state view
        databaseStatusView = findViewById(R.id.database_status);



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
            Intent settingsIntent = new Intent(this, EditorActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}