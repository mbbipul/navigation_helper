package com.example.navigation.utils;


import android.location.Location;
import android.os.AsyncTask;
import com.example.navigation.database.AppDatabase;
import com.example.navigation.entity.LocationD;
import com.example.navigation.entity.Route;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer  {

    private static final String TAG = DatabaseInitializer.class.getName();
    private static AppDatabase db;
    private static  DatabaseListner databaseListner;

    public DatabaseInitializer( AppDatabase db,DatabaseListner databaseListner){
        this.db = db;
        this.databaseListner = databaseListner;
    }

    public void populateAsync(Location location,Route route) {
        PopulateDbAsync task = new PopulateDbAsync(location,route);
        task.execute();
    }

    public List<LocationD> getAllLocation(){
        List<LocationD> locations = db.locationDao().getAll();
        return locations;
    }

    private static void addLocation(final AppDatabase db, Location location,Route route) {
        LocationD locationD = new LocationD();

        locationD.setAltitude(location.getAltitude());
        locationD.setLattitude(location.getLatitude());
        locationD.setLongitude(location.getLongitude());
        locationD.setBearing(location.getBearing());
        locationD.setSpeed(location.getSpeed());

        Long id = db.locationDao().insertOne(locationD);

        route.setLocationId(id);
        route.setTime(String.valueOf(System.currentTimeMillis()));

        db.routeDao().insertOne(route);

    }

    public static ArrayList<RouteInfo> getAllRouteInfo(){

        ArrayList<RouteInfo> routeInfos = new ArrayList<RouteInfo>();

        List<Route> routes = db.routeDao().getRoutesByRouteName("home");

        for (int i=0;i<routes.size();i++){

            Long id = routes.get(i).getLocationId();
            LocationD locationD = db.locationDao().getLocationById(id);
            RouteInfo routeInfo = new RouteInfo();
            routeInfo.setRoute(routes.get(i));
            routeInfo.setLocationD(locationD);

            routeInfos.add(routeInfo);
        }
        return routeInfos;
    }


    public void removeAll(){
        db.locationDao().deleteAll();
        db.routeDao().deleteAll();
    }


    private static class PopulateDbAsync extends AsyncTask<ArrayList<RouteInfo>, Void, ArrayList<RouteInfo>> {

        private Location location;
        private Route route;

        PopulateDbAsync(Location location,Route route) {
            this.location = location;
            this.route = route;
        }

        @Override
        protected ArrayList<RouteInfo> doInBackground(ArrayList<RouteInfo>... routeinfo) {
            addLocation(db,location,route);
            return getAllRouteInfo();
        }

        @Override
        protected void onPostExecute(ArrayList<RouteInfo> routeInfos) {
            databaseListner.fetchRouteInfo(routeInfos);
        }

    }
}
