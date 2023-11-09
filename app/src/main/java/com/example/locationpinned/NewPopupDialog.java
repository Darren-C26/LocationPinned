package com.example.locationpinned;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.*;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class NewPopupDialog extends Dialog {
    // Interface for handling saved data instance
    public interface OnDataSavedListener {
        void onDataSaved();
    }

    // Add a field for the listener
    private OnDataSavedListener onDataSavedListener;

    public void setOnDataSavedListener(OnDataSavedListener listener) {
        this.onDataSavedListener = listener;
    }

    // Declare UI components
    TextView popupTitle;
    EditText editLatPopup;
    EditText editLongPopup;
    Button saveButtonPopup;
    FloatingActionButton cancelButtonPopup;

    public NewPopupDialog(Context context) {
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
        popupTitle.setText("Add Location");

        // Handle the save button click
        saveButtonPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationModel locationModel;

                // Check if latitude and longitude fields are filled, display message if a field is empty
                if (editLatPopup.getText().toString().equals("") || editLongPopup.getText().toString().equals("")) {
                    Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // Retrieves coordinates from text fields and attempts to find address
                        double latitude = Double.parseDouble(editLatPopup.getText().toString());
                        double longitude = Double.parseDouble(editLongPopup.getText().toString());
                        String address = findAddress(context, latitude, longitude);
                        // If address could not be found for given coordinates, error message is printed
                        if (address.equals("")) {
                            Toast.makeText(context, "No address could be found for the provided coordinates. Please try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            locationModel = new LocationModel(-1, address, latitude, longitude);
                            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);

                            // Attempt to add the location to the database, print appropriate message based on result
                            boolean success = dataBaseHelper.addLocation(locationModel);
                            if (success) {
                                Toast.makeText(context, "Location saved successfully.", Toast.LENGTH_SHORT).show();
                                if (onDataSavedListener != null) {
                                    onDataSavedListener.onDataSaved();
                                }
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

        // Handle the cancel button click
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
            List<Address> locationAddresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (locationAddresses != null && !locationAddresses.isEmpty()) {
                Address primaryAddress = locationAddresses.get(0);
                StringBuilder fullAddress = new StringBuilder();

                // Concatenate address lines to form the full address
                for (int lineIndex = 0; lineIndex <= primaryAddress.getMaxAddressLineIndex(); lineIndex++) {
                    fullAddress.append(primaryAddress.getAddressLine(lineIndex)).append("\n");
                }

                address = fullAddress.toString();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return address;
    }

}
