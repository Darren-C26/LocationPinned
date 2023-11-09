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

    public LocationAdapter(Context context, List<LocationModel> locations, DataBaseHelper dataBaseHelper) {
        this.context = context;
        this.locations = locations;
        this.dataBaseHelper = dataBaseHelper;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (context instanceof MainActivity) {
                view = inflater.inflate(R.layout.query_item_layout, null);
            }
            else {
                view = inflater.inflate(R.layout.list_item_layout, null);
            }
        }

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);

        LocationModel location = locations.get(position);

        titleTextView.setText(location.getAddress());
        descriptionTextView.setText("Lat: " + location.getLatitude() + ", Long: " + location.getLongitude());

        if (!(context instanceof MainActivity)){
            Button deleteButton = view.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int locationID = locations.get(position).getId();
                    boolean isDeleted = dataBaseHelper.deleteLocation(locationID);

                    if (isDeleted) {
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