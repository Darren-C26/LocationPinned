package com.example.locationpinned;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class LocationAdapter extends BaseAdapter {
    private Context context;
    private List<LocationModel> locations;
    private DataBaseHelper dataBaseHelper;

    // Constructor for the LocationAdapter class
    public LocationAdapter(Context context, List<LocationModel> locations, DataBaseHelper dataBaseHelper) {
        this.context = context;
        this.locations = locations;
        this.dataBaseHelper = dataBaseHelper;
    }

    // Get the number of items in the adapter
    @Override
    public int getCount() {
        return locations.size();
    }

    // Get the item at the specified position
    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    // Get the item's ID at the specified position
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Get a view for each item in the adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            // Inflate a new view for the item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Check if the context is an instance of MainActivity
            if (context instanceof MainActivity) {
                view = inflater.inflate(R.layout.query_item_layout, null);
            } else {
                view = inflater.inflate(R.layout.list_item_layout, null);
            }
        }

        // Find and set references to UI elements
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);

        // Get the location model for the current position
        LocationModel location = locations.get(position);

        // Set the title and description text views
        titleTextView.setText(location.getAddress());
        descriptionTextView.setText("Lat: " + location.getLatitude() + ", Long: " + location.getLongitude());

        // Check if the context is not an instance of MainActivity
        if (!(context instanceof MainActivity)) {
            Button deleteButton = view.findViewById(R.id.deleteButton);

            // Set a click listener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the location ID to be deleted
                    int locationID = locations.get(position).getId();

                    // Delete the location from the database and check if it was successful
                    boolean isDeleted = dataBaseHelper.deleteLocation(locationID);

                    if (isDeleted) {
                        // Remove the location from the list and refresh the adapter
                        locations.remove(position);
                        notifyDataSetChanged();
                    } else {
                        // Handle the case where the location couldn't be deleted
                    }
                }
            });
        }

        return view;
    }
}
