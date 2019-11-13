package com.example.navigation;


import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

public class DatabaseInitializer  {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static LocationD addLocation(final AppDatabase db, LocationD locationD) {
        db.locationDao().insertAll(locationD);
        return locationD;
    }

    private static void populateWithTestData(AppDatabase db) {
        LocationD locationD = new LocationD();
        locationD.setLattitude(123.56);
        locationD.setLongitude(56.56);
        locationD.setAltitude(12);
        locationD.setBearing(12);
        locationD.setSpeed(12);

        addLocation(db,locationD);
        List<LocationD> userList = db.locationDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userList.size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }
}
