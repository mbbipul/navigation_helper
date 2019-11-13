package com.example.navigation.utils;


import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.navigation.database.AppDatabase;
import com.example.navigation.entity.LocationD;

import java.util.List;

public class DatabaseInitializer  {

    private static final String TAG = DatabaseInitializer.class.getName();
    final AppDatabase db;

    public DatabaseInitializer( AppDatabase db){
        this.db = db;
    }
    public void populateAsync(LocationD locationD) {
        PopulateDbAsync task = new PopulateDbAsync(db,locationD);
        task.execute();
    }

    public List<LocationD> getAll(){
        List<LocationD> locations = db.locationDao().getAll();
        return locations;
    }

    private static LocationD addLocation(final AppDatabase db, LocationD locationD) {
        db.locationDao().insertAll(locationD);
        return locationD;
    }

    private static void populateWithData(AppDatabase db,LocationD locationD) {
        addLocation(db,locationD);
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private LocationD locationD;
        PopulateDbAsync(AppDatabase db,LocationD locationD) {
            mDb = db;
            this.locationD = locationD;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithData(mDb,locationD);
            return null;
        }

    }
}
