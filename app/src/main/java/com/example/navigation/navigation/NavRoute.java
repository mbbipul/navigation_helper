package com.example.navigation.navigation;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

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

    public int getNextPointIndex() {
        return nextPointIndex;
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

    public int getMinimumDistancePointIndex(Location cLocation){

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
}
