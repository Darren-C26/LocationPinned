package com.example.locationpinned;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ViewLocations extends AppCompatActivity implements NewPopupDialog.OnDataSavedListener {
    // UI elements
    Toolbar toolbar;
    ListView listView;
    List<LocationModel> locationsList;
    TextView noLocations;
    FloatingActionButton cancel_button, add_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_locations);

        // Initialize UI elements
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Locations Database");

        listView = findViewById(R.id.location_view);
        noLocations = findViewById(R.id.noLocation);
        cancel_button = findViewById(R.id.cancelButton);
        add_location = findViewById(R.id.add_location);

        // Create an instance of the database helper
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ViewLocations.this);

        // Retrieve the list of locations from the database
        locationsList = dataBaseHelper.getLocations();

        if (!locationsList.isEmpty()) {
            noLocations.setVisibility(View.INVISIBLE);

            // Set up the adapter to display locations
            LocationAdapter locationsAdapter = new LocationAdapter(ViewLocations.this, locationsList, dataBaseHelper);
            listView.setAdapter(locationsAdapter);
        } else {
            noLocations.setVisibility(View.VISIBLE);
        }

        // Set up click listeners
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the MainActivity
                Intent cancelNote = new Intent(ViewLocations.this, MainActivity.class);
                startActivity(cancelNote);
            }
        });

        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the dialog to add a new location
                NewPopupDialog dialog = new NewPopupDialog(ViewLocations.this);
                dialog.setOnDataSavedListener(ViewLocations.this);
                dialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected location from the filteredList
                LocationModel selectedLocation = locationsList.get(position);

                // Create a dialog to edit the selected location's data
                EditPopupDialog editPopupDialog = new EditPopupDialog(ViewLocations.this, selectedLocation);
                editPopupDialog.show();
            }
        });
    }

    public void onDataSaved() {
        // Update the list of locations after a data update
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ViewLocations.this);
        locationsList = dataBaseHelper.getLocations();

        if (!locationsList.isEmpty()) {
            noLocations.setVisibility(View.INVISIBLE);

            // Set up the adapter to display locations
            LocationAdapter locationsAdapter = new LocationAdapter(ViewLocations.this, locationsList, dataBaseHelper);
            listView.setAdapter(locationsAdapter);
        } else {
            noLocations.setVisibility(View.VISIBLE);
        }
    }
}
