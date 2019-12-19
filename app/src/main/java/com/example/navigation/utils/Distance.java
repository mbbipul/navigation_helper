package com.example.navigation.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Distance {
    double lat1,lon1,lat2,lon2 ;
    public double distance(Location srcLocation, Location destlocation){
        this.lat1 = srcLocation.getLatitude();
        this.lon1 = srcLocation.getLongitude();
        this.lat2 = destlocation.getLatitude();
        this.lon2 = destlocation.getLongitude();
        return  getDistance();
    }
    public double distance(LatLng srcLatLng, LatLng destLatLng){
        this.lat1 = srcLatLng.latitude;
        this.lon1 = srcLatLng.longitude;
        this.lat2 = destLatLng.latitude;
        this.lon2 = destLatLng.longitude;
        return  getDistance();
    }
    private double getDistance() {
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
