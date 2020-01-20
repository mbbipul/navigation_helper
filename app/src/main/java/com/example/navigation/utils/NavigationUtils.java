package com.example.navigation.utils;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NavigationUtils {
    public static LatLng getVerticalDistance(LatLng p1, LatLng p2, LatLng currentLoc){
        Distance dis = new Distance();

        double distance = dis.distance(p1,p2);

        if(distance < 0.1)
            return p2;
        LatLng m = midPoint(p1.latitude,p1.longitude,p2.latitude,p2.longitude);

        List<LatLng> points = Arrays.asList(p1,m,p2);
        List<Double> arrPoint = getDistances(p1,m,p2,currentLoc);

        List<Double> arrPointSort = new ArrayList<>(arrPoint);
        Collections.sort(arrPointSort);

        int [] twoMinPoint = new int[2];
        twoMinPoint[0] = arrPoint.indexOf(arrPointSort.get(0));
        twoMinPoint[1] = arrPoint.indexOf(arrPointSort.get(1));
        System.gc();
        return getVerticalDistance(points.get(twoMinPoint[0]),points.get(twoMinPoint[1]),currentLoc);
    }

    private static List<Double> getDistances(LatLng p1, LatLng m, LatLng p2, LatLng currentLoc) {
        Distance dis = new Distance();
        List<Double> distances = new ArrayList<>();
        distances.add(dis.distance(p1,currentLoc));
        distances.add(dis.distance(m,currentLoc));
        distances.add(dis.distance(p2,currentLoc));
        return distances;
    }
    public static LatLng midPoint(double lat1, double lon1, double lat2, double lon2){

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

    public static boolean isLocationOnPath(LatLng latlng, NavRoute navRoute, double tolerence){
        Distance dis = new Distance();
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latlng.latitude);
        location.setLongitude(latlng.longitude);
        double distance = dis.distance(latlng,navRoute.getMinPoint(1,location));
        if (distance <= tolerence)
            return true;
        return false;
    }

    private static List<LatLng> getNearestTwoPooint(LatLng latLng, List<LatLng> points){
        List<LatLng> twoPoints = new ArrayList<>();
        Distance distance = new Distance();
        int minIndex = 0;
        int i =0;
        double smallest = distance.distance(latLng,points.get(0));
        for(LatLng x : points ){
            double dis = distance.distance(latLng,x);
            if (dis < smallest) {
                smallest = dis;
                minIndex = i;
            }
            i++;
        }

        twoPoints.add(points.get(minIndex));
        twoPoints.add(points.get(minIndex+1));
        return twoPoints;
    }

    public static double calcBearing(Location loc1,Location loc2){
        double lat1 = loc1.getLatitude();
        double lng1 = loc1.getLongitude();
        double lat2 = loc2.getLatitude();
        double lng2 = loc2.getLongitude();

        double dLon = (lng2-lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double brng = Math.toDegrees((Math.atan2(y, x)));
        brng = (360 - ((brng + 360) % 360));
        return Distance.round(brng,2);
    }


}
