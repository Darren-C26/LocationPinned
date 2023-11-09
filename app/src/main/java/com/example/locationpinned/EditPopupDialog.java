package com.example.locationpinned;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class EditPopupDialog extends Dialog {
    
    // Declare UI components
    TextView popupTitle;
    EditText editLatPopup;
    EditText editLongPopup;
    Button saveButtonPopup;
    FloatingActionButton cancelButtonPopup;

    public EditPopupDialog(Context context, LocationModel locationModel) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_layout);

        // Set dialog window properties
        Window window = getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Initialize UI components
        popupTitle = findViewById(R.id.popupTitle);
        editLatPopup = findViewById(R.id.editLatPopup);
        editLongPopup = findViewById(R.id.editLongPopup);
        saveButtonPopup = findViewById(R.id.saveButtonPopup);
        cancelButtonPopup = findViewById(R.id.cancelButtonPopup);

        // Set the title for the dialog
        popupTitle.setText("Edit Location");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);

        // Populate the latitude and longitude fields with the location model data
        editLatPopup.setText(String.valueOf(locationModel.getLatitude()));
        editLongPopup.setText(String.valueOf(locationModel.getLongitude()));

        // Handle save button click
        saveButtonPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editLatPopup.getText().toString().equals("") || editLongPopup.getText().toString().equals("")) {
                    Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // Extract the latitude and longitude from the input fields
                        double latitude = Double.parseDouble(editLatPopup.getText().toString());
                        double longitude = Double.parseDouble(editLongPopup.getText().toString());

                        // Attempt to find the address based on the latitude and longitude
                        String address = findAddress(context, latitude, longitude);

                        if (address.equals("")) {
                            Toast.makeText(context, "No address could be found for the provided coordinates. Please try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Update the location model with the new data
                            locationModel.setLatitude(latitude);
                            locationModel.setLongitude(longitude);
                            locationModel.setAddress(address);

                            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);

                            // Attempt to update the location in the database
                            boolean success = dataBaseHelper.updateLocation(locationModel.getId(), locationModel);

                            // Print appropriate message based on result
                            if (success) {
                                Toast.makeText(context, "Location saved successfully.", Toast.LENGTH_SHORT).show();
                                dismiss(); // Close the dialog
                            } else {
                                Toast.makeText(context, "Location could not be saved.", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Error saving location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Handle cancel button click
        cancelButtonPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the dialog
            }
        });
    }

    // Find the address based on latitude and longitude
    private String findAddress(Context context, double latitude, double longitude) {
        String address = "";

        // Create a Geocoder instance with the given context and locale
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strToReturn = new StringBuilder("");

                // Concatenate the address lines to form the full address
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strToReturn.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strToReturn.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }
}
