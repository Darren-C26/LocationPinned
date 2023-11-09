package com.example.locationpinned;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    // Initialize constants for database attribute names
    public static final String DATABASE_NAME = "locations.db";
    public static final String LOCATION_TABLE = "location";
    public static final String COLUMN_LOCATION_ID = "location_id";
    public static final String COLUMN_LOCATION_ADDRESS = "location_address";
    public static final String COLUMN_LOCATION_LATITUDE = "location_latitude";
    public static final String COLUMN_LOCATION_LONGITUDE = "location_longitude";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;

        if (!locationsExistInDatabase()){
            addLocationsFromFile(context);
        }
    }

    public void onCreate(SQLiteDatabase db) {
        // Create the SQLite table to store location data
        db.execSQL(
                "CREATE TABLE " + LOCATION_TABLE + " (" +
                        COLUMN_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_LOCATION_ADDRESS + " TEXT, " +
                        COLUMN_LOCATION_LATITUDE + " REAL, " +
                        COLUMN_LOCATION_LONGITUDE + " REAL)"
        );

    }

    // Checks if any locations exist in the database
    private boolean locationsExistInDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + LOCATION_TABLE;
        Cursor cursor = db.rawQuery(queryString, null);

        boolean locationsExist = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return locationsExist;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    // Add a new location to the database
    public boolean addLocation(LocationModel locationModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_ADDRESS, locationModel.getAddress());
        cv.put(COLUMN_LOCATION_LATITUDE, locationModel.getLatitude());
        cv.put(COLUMN_LOCATION_LONGITUDE, locationModel.getLongitude());
        long insert = db.insert(LOCATION_TABLE, null, cv);
        db.close();
        return insert != -1;
    }

    //Retrieve a list of locations from the database
    public List<LocationModel> getLocations() {
        List<LocationModel> locationsList = new ArrayList<>();
        String queryString = "SELECT * FROM " + LOCATION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                int locationID = cursor.getInt(0);
                String locationAddress = cursor.getString(1);
                double locationLatitude = cursor.getDouble(2);
                double locationLongitude = cursor.getDouble(3);

                LocationModel location = new LocationModel(locationID, locationAddress, locationLatitude, locationLongitude);
                locationsList.add(location);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locationsList;
    }

    // Delete a location from the database
    public boolean deleteLocation(int locationID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_LOCATION_ID + " = ?";
        String[] whereArgs = {String.valueOf(locationID)};
        int deletedRows = db.delete(LOCATION_TABLE, whereClause, whereArgs);
        db.close();
        return deletedRows > 0;
    }

    // Update an existing location in the database
    public boolean updateLocation(int locationID, LocationModel updatedLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_ADDRESS, updatedLocation.getAddress());
        cv.put(COLUMN_LOCATION_LATITUDE, updatedLocation.getLatitude());
        cv.put(COLUMN_LOCATION_LONGITUDE, updatedLocation.getLongitude());
        String whereClause = COLUMN_LOCATION_ID + " = ?";
        String[] whereArgs = {String.valueOf(locationID)};
        int updatedRows = db.update(LOCATION_TABLE, cv, whereClause, whereArgs);
        db.close();
        return updatedRows > 0;
    }

    // Read locations from a text file and add them to the database
    public void addLocationsFromFile(Context context) {
        try {
            // Open the file from the assets directory
            InputStream inputStream = context.getAssets().open("coordinates.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] coordinates = line.split(" ");

                if (coordinates.length == 2) {
                    String latitudeStr = coordinates[0].replaceAll(",", "");
                    String longitudeStr = coordinates[1].replaceAll(",", "");
                    double latitude = Double.parseDouble(latitudeStr);
                    double longitude = Double.parseDouble(longitudeStr);

                    String address = "";
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address returnedAddress = addresses.get(0);
                            StringBuilder strToReturn = new StringBuilder("");

                            for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                                strToReturn.append(returnedAddress.getAddressLine(i)).append("\n");
                            }
                            address = strToReturn.toString();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Create a LocationModel object and add it to the database
                    LocationModel locationModel = new LocationModel(-1, address, latitude, longitude);
                    addLocation(locationModel);
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
