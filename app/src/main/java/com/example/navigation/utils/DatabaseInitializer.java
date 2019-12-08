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
    public DatabaseInitializer( AppDatabase db){
        this.db = db;
    }


    public void populateAsync(Location location,Route route,String routeName) {
        PopulateDbAsync task = new PopulateDbAsync(location,route,routeName);
        task.execute();
    }

    public List<LocationD> getAllLocation(){
        List<LocationD> locations = db.locationDao().getAll();
        return locations;
    }

    public void drawRoute(String routename){
        databaseListner.fetchRouteInfo(getAllRouteInfo(routename));
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

    public  void deleteRoute(String routename){
    }

    public ArrayList<String> getAllRouteName(){

        ArrayList<String> routesName = new ArrayList<String>();
        List<String> routeNames = db.routeDao().getAllRouteName();

        for(int i=0;i<routeNames.size();i++){
            routesName.add(routeNames.get(i));
        }
        return routesName;
    }

    public static ArrayList<RouteInfo> getAllRouteInfo(String routename){

        ArrayList<RouteInfo> routeInfos = new ArrayList<RouteInfo>();

        List<Route> routes = db.routeDao().getRoutesByRouteName(routename);

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
        private String routeName;

        PopulateDbAsync(Location location,Route route,String routename) {
            this.location = location;
            this.route = route;
            this.routeName = routename;
        }

        @Override
        protected ArrayList<RouteInfo> doInBackground(ArrayList<RouteInfo>... routeinfo) {
            addLocation(db,location,route);
            return getAllRouteInfo(routeName);
        }

        @Override
        protected void onPostExecute(ArrayList<RouteInfo> routeInfos) {
            databaseListner.fetchRouteInfo(routeInfos);
        }

    }
}
