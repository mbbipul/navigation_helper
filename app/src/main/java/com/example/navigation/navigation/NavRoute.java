package com.example.navigation.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.navigation.utils.Geometry;
import com.example.navigation.utils.LocationIndex;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavRoute {

    private ArrayList<Location> routePoints;

    int N ;
    int firstPointIndex ;
    int lastPointIndex ;
    int currentPointIndex ;
    int nextPointIndex;
    int direction;
    int startPointIndex;

    Context c;
    public int getStartPointIndex() {
        return startPointIndex;
    }

    public void setStartPointIndex(LatLng latLng) {
        this.startPointIndex = startPointIndex;
    }

    public NavRoute(ArrayList<Location> locations, int direction,Context c){
        this.routePoints = locations;
        this.direction = direction;
        this.c = c;
        if (direction==1){
            firstPointIndex = 0;
            lastPointIndex = N-1;
        }else if(direction==-1){
            firstPointIndex = N-1;
            lastPointIndex = 0;
        }
    }


    public int getN() {
        return routePoints.size();
    }

    public int getFirstPointIndex() {
        return firstPointIndex;
    }

    public int getLastPointIndex() {
        return lastPointIndex;
    }


    public int getCurrentPointIndex() {
        return currentPointIndex;
    }

    public void setCurrentPointIndex(int currentPointIndex) {
        this.currentPointIndex = currentPointIndex;
    }

    public int getNextPointIndex(int i) {
        if (i==getRoutePoints().size()-1)
            return -2;
        return i+1;
    }

    public void setNextPointIndex(int nextPointIndex) {
        this.nextPointIndex = nextPointIndex;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public ArrayList<Location> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(ArrayList<Location> routePoints) {
        this.routePoints = routePoints;
    }

    public void addRoutePoint(Location location){ routePoints.add(location); }

    public int getNearestPointIndex(Location cLocation){

        int minIndex = 0;
        int i =0;
        double smallest = distance(cLocation,routePoints.get(0));
        for(Location x : routePoints ){
            double dis = distance(cLocation,x);
            if (dis < smallest) {
                smallest = dis;
                minIndex = i;
            }
            i++;
        }

        return minIndex;
    }


    public Location getPointLocation(int i){
        return routePoints.get(i);
    }

    private double distance(Location srcLocation,Location destlocation) {
        double lat1 = srcLocation.getLatitude(),
                lon1 = srcLocation.getLongitude(),
                lat2 = destlocation.getLatitude(),
                lon2 = destlocation.getLongitude();
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist*1000);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public List<LatLng> getAllpointLatLngs(){
        List<LatLng> latLngs = new ArrayList<>() ;
        for(int i = 0; i<getN();i++){
            LatLng latLng =
                    new LatLng(routePoints.get(i).getLatitude(),routePoints.get(i).getLongitude());
            latLngs.add(latLng);
        }
        return latLngs;
    }
    public  void setAnimation(GoogleMap myMap, final List<LatLng> directionPoint, final Bitmap bitmap) {


        Marker marker = myMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(directionPoint.get(0))
                .flat(true));

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(directionPoint.get(0), 1));

        animateMarker(myMap, marker, directionPoint, false);
    }


    private  void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < directionPoint.size())
                    marker.setPosition(directionPoint.get(i));
                i++;


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 1000);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public int getPrevPointIndex(int i){
        return i-1;
    }

    public LocationIndex getNearestpointLocation(Location currentLocation){
        List<LocationIndex> locationIndices = getNearest3Point(currentLocation);

        LocationIndex smallestLocation = locationIndices.get(0);
        double smallest = distance(currentLocation,smallestLocation.getLocation());

        for (LocationIndex x : locationIndices){
            double dis = distance(currentLocation,x.getLocation());
            if (dis < smallest) {
                smallest = dis;
                smallestLocation = x;
            }
        }
        return smallestLocation;

    }

    //using vertical line
//    public List<LocationIndex> getNearest3Point(Location currentLocation){
//        List<LocationIndex> locationIndices = getNearest3PointLocationIndex(currentLocation);
//        List<LocationIndex> nearestLocationIndices = new ArrayList<>();
//        nearestLocationIndices.add(locationIndices.get(0));
//
//        LocationIndex loc = locationIndices.get(0);
//
//        for (int i = 1;i<locationIndices.size();i++){
//            LocationIndex otherLoc = locationIndices.get(i);
//
//            Geometry geometry = new Geometry(
//                    loc.getLocation(),otherLoc.getLocation(),currentLocation);
//
//            Location location = new Location(LocationManager.GPS_PROVIDER);
//            location.setLatitude(geometry.getX());
//            location.setLongitude(geometry.getY());
//            LocationIndex locationIndex = new LocationIndex(location,i-3);
//            nearestLocationIndices.add(locationIndex);
//        }
//
//
//        return nearestLocationIndices;
//    }

    public List<LocationIndex> getNearest3Point(Location currentLocation){
        List<LocationIndex> locationIndices = getNearest3PointLocationIndex(currentLocation);
        List<LocationIndex> nearestLocationIndices = new ArrayList<>();
        LocationIndex loc = locationIndices.get(0);
        nearestLocationIndices.add(loc);

        LatLng locLatLng = new LatLng(loc.getLocation().getLatitude(),loc.getLocation().getLongitude());
        for (int i = 1;i<locationIndices.size();i++){
            LocationIndex otherLoc = locationIndices.get(i);
            System.out.println(otherLoc.getLocation().getLatitude());

            LatLng midPoint = midPoint(locLatLng.latitude,locLatLng.longitude,otherLoc.getLocation().getLatitude(),otherLoc.getLocation().getLongitude());
            System.out.println(midPoint);
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(midPoint.latitude);
            location.setLongitude(midPoint.longitude);
            LocationIndex locationIndex = new LocationIndex(location,otherLoc.getIndex());
            nearestLocationIndices.add(locationIndex);
        }


        return nearestLocationIndices;
    }

    public List<LocationIndex> getNearest3PointLocationIndex(Location currentLocation){
        List<LocationIndex> locationIndices = getNearest3IndexLocation(currentLocation );
        List<LocationIndex> loc = new ArrayList<>();

        if ( locationIndices.get(1).getIndex() < 0){
            loc.add(locationIndices.get(0));
            loc.add(locationIndices.get(2));
        }else if(locationIndices.get(2).getIndex() < 0){
            loc.add(locationIndices.get(0));
            loc.add(locationIndices.get(2));
        }else {
            return locationIndices;
        }
        return loc;
    }

    public List<LocationIndex> getNearest3IndexLocation(Location currentLocation){

        List<LocationIndex> locationIndices = new ArrayList<>();

        int nearestPointIndex = getNearestPointIndex(currentLocation);
        int prevPointIndex = getPrevPointIndex(nearestPointIndex);
        int nextPointIndex = getNextPointIndex(nearestPointIndex);

        LocationIndex locFix = new LocationIndex(
                getPointLocation(nearestPointIndex),nearestPointIndex);
        locationIndices.add(locFix);

        if (prevPointIndex<0){
            locationIndices.add(new LocationIndex(
                    null,prevPointIndex));
        }
        else {
            locationIndices.add(new LocationIndex(
                    getPointLocation(prevPointIndex),prevPointIndex));
        }

        if (nextPointIndex<0){
            locationIndices.add(new LocationIndex(
                    null,nextPointIndex));
        }
        else {
            locationIndices.add(new LocationIndex(
                    getPointLocation(nextPointIndex),nextPointIndex));
        }

        return locationIndices;

    }

    public LatLng midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }
}
