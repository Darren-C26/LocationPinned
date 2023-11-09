package com.example.locationpinned;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ListView listView;
    FloatingActionButton fab;
    TextView noLocations, searchLocations;
    SearchView search;
    List<LocationModel> locationsList;
    List<LocationModel> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        fab = findViewById(R.id.fab);
        imageView = findViewById(R.id.imageView);
        listView = findViewById(R.id.location_view);
        noLocations = findViewById(R.id.noLocations);
        searchLocations = findViewById(R.id.searchLocations);
        search = findViewById(R.id.searchView);

        // Create a database helper to work with location data
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);

        // Get the list of locations from the database
        locationsList = dataBaseHelper.getLocations();

        // Set visibility of noLocations and searchLocations TextViews
        noLocations.setVisibility(View.INVISIBLE);
        searchLocations.setVisibility(View.VISIBLE);

        // Handle click on the FAB to add a new location
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createLocation = new Intent(MainActivity.this, ViewLocations.class);
                startActivity(createLocation);
            }
        });

        // Handle changes in the search query
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query when the user submits it (not needed)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Update the filtered locations list based on the search query
                searchLocations.setVisibility(View.INVISIBLE);
                filterLocations(newText, dataBaseHelper);
                return true;
            }
        });

    }

    // Method to filter locations based on query
    private void filterLocations(String query, DataBaseHelper dataBaseHelper) {
        // Reload the full list of locations from the database
        locationsList = dataBaseHelper.getLocations();
        filteredList.clear();

        if (query.isEmpty()) {
            searchLocations.setVisibility(View.VISIBLE);
            noLocations.setVisibility(View.INVISIBLE);
        } else {
            // Filter the locations based on the query
            for (LocationModel location : locationsList) {
                if (location.getAddress().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(location);
                }
            }

            // Show noLocations view when there are no matching locations
            if (filteredList.isEmpty()) {
                noLocations.setVisibility(View.VISIBLE);
            } else {
                noLocations.setVisibility(View.INVISIBLE);
            }
        }

        // Update the ListView with the filtered locations
        LocationAdapter locationsAdapter = new LocationAdapter(MainActivity.this, filteredList, dataBaseHelper);
        listView.setAdapter(locationsAdapter);
    }
}
