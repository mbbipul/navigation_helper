package com.example.navigation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.navigation.entity.LocationD;
import com.example.navigation.utils.Distance;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import static com.example.navigation.utils.NavigationUtils.getVerticalDistance;
import static com.example.navigation.utils.NavigationUtils.midPoint;

public class NavRoute {

    private ArrayList<Location> routePoints;

    int N ;
    int firstPointIndex ;
    int lastPointIndex ;
    int currentPointIndex ;
    int nextPointIndex;
    int direction;
    int startPointIndex;

    public List<String> getRouteDirection() {
        return routeDirection;
    }

    public ArrayList<RouteInfo> getRouteInfos() {
        return routeInfos;
    }

    private ArrayList<RouteInfo> routeInfos;
    private List<String> routeDirection;

    public int getStartPointIndex() {
        return startPointIndex;
    }

    public void setStartPointIndex(LatLng latLng) {
        this.startPointIndex = startPointIndex;
    }

    public NavRoute(ArrayList<RouteInfo> mRouteInfos, int direction){
        this.routeInfos = mRouteInfos;
        setRoutePoints(mRouteInfos);
        this.direction = direction;
        if (direction==1){
            firstPointIndex = 0;
            lastPointIndex = N-1;
        }else if(direction==-1){
            firstPointIndex = N-1;
            lastPointIndex = 0;
        }
    }

    private void setRoutePoints(ArrayList<RouteInfo> mRouteInfos){
        routePoints = new ArrayList<>();
        routeDirection = new ArrayList<>();
        List<LatLng> points = new ArrayList<LatLng>();
        final MarkerOptions options = new MarkerOptions();

        for (int i=0;i<mRouteInfos.size();i++){
            LocationD locationD = mRouteInfos.get(i).getLocationD();
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(locationD.getLattitude());
            location.setLongitude(locationD.getLongitude());
            location.setAltitude(locationD.getAltitude());

            routeDirection.add(mRouteInfos.get(i).getRoute().getDirection());
            routePoints.add(location);
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
            return getN();
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


    public void addRoutePoint(Location location){ routePoints.add(location); }

    public int getNearestPointIndex(Location cLocation){
        Distance distance = new Distance();
        int minIndex = 0;
        int i =0;
        double smallest = distance.distance(cLocation,routePoints.get(0));
        for(Location x : routePoints ){
            double dis = distance.distance(cLocation,x);
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

    public Location getPointLocationWithRoute(int i){
        return routePoints.get(i);
    }

    public LatLng getPointLatLng(int i){
        LatLng latLng = new LatLng(routePoints.get(i).getLatitude(),
                routePoints.get(i).getLatitude());
        return latLng;
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
        if (i==0)
            return 0;
        return i-1;
    }

    public List<CustomLatLng> getAllPointsWithInfo(){
        List<CustomLatLng> customLatLngs = new ArrayList<>();
        int i = 0;
        for (RouteInfo x : routeInfos){
            LatLng latLng = new LatLng(x.getLocationD().getLattitude(),
                    x.getLocationD().getLongitude());
            CustomLatLng customLatLng = new CustomLatLng(latLng,x.getRoute().getDirection(),i);
            customLatLngs.add(customLatLng);
            i++;
        }
        return  customLatLngs;
    }
    public LocationIndex getNearestpointLocation(Location currentLocation){
        List<LocationIndex> locationIndices = getNearest3Point(currentLocation);
        Distance distance = new Distance();
        LocationIndex smallestLocation = locationIndices.get(0);
        double smallest = distance.distance(currentLocation,smallestLocation.getLocation());

        for (LocationIndex x : locationIndices){
            double dis = distance.distance(currentLocation,x.getLocation());
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


        return locationIndices;
    }


    public List<LocationIndex> getNearest3PointLocationIndex(Location currentLocation){
        List<LocationIndex> locationIndices = getNearest3IndexLocation(currentLocation );
        List<LocationIndex> loc = new ArrayList<>();

        if ( locationIndices.get(1).getIndex() < 0){
            loc.add(locationIndices.get(0));
            loc.add(locationIndices.get(2));
        }else if(locationIndices.get(2).getIndex() < 0){
            loc.add(locationIndices.get(0));
            loc.add(locationIndices.get(1));
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


    /* get vertical distance from point to a line using false position

     */
    public LatLng getMinPoint(int dir,Location currentLoc){
       // getMinPoint
        LatLng currentLat = new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());

        //List<LocationIndex> locationIndices = getNearest3PointLocationIndex(currentLoc);
        List<LocationIndex> locationIndices = getNearest3Point_u(currentLoc);

        LatLng m = new LatLng(locationIndices.get(1).getLocation().getLatitude(),
                locationIndices.get(1).getLocation().getLongitude());
        LatLng p1 = new LatLng(locationIndices.get(0).getLocation().getLatitude(),
                locationIndices.get(0).getLocation().getLongitude());

        if(locationIndices.size()<3) {
            return getVerticalDistance(p1,m,currentLat);
        }
        else {
            LatLng p2 = new LatLng(locationIndices.get(2).getLocation().getLatitude(),
                    locationIndices.get(2).getLocation().getLongitude());

            // List<Double> arrPoint = getDistance(p1,m,p2,currentLoc);

//        if (dir == 1)
//            return getVerticalDistance(m,p2,currentLat);
//        else
            return getVerticalDistance(p1, m, currentLat);
        }

    }

    private List<LocationIndex> getNearest3Point_u(Location cuLoc){
        List<LocationIndex> locationIndices = new ArrayList<>();
        int nIndex = getNearestPointIndex(cuLoc);
        if (nIndex == 0){
            locationIndices.add(new LocationIndex(getPointLocation(nIndex),nIndex));
            locationIndices.add(new LocationIndex(getPointLocation(nIndex+1),nIndex+1));
            locationIndices.add(new LocationIndex(getPointLocation(nIndex+2),nIndex+2));
        }else if (nIndex == getN()-1){
            locationIndices.add(new LocationIndex(getPointLocation(nIndex-2),nIndex-2));
            locationIndices.add(new LocationIndex(getPointLocation(nIndex-1),nIndex-1));
            locationIndices.add(new LocationIndex(getPointLocation(nIndex),nIndex));

        }else {
            locationIndices.add(new LocationIndex(getPointLocation(nIndex-1),nIndex-1));
            locationIndices.add(new LocationIndex(getPointLocation(nIndex),nIndex));
            locationIndices.add(new LocationIndex(getPointLocation(nIndex+1),nIndex+1));
        }

        return locationIndices;
    }

    private List<Double> getDistance (LatLng p1,LatLng m,LatLng p2,LatLng currentLoc) {
        Distance dis = new Distance();
        List<Double> distances = new ArrayList<>();
        distances.add(dis.distance(p1,currentLoc));
        distances.add(dis.distance(m,currentLoc));
        distances.add(dis.distance(p2,currentLoc));
        return distances;
    }


    private  double numberSort (double a,double b) {
        return a - b;
    };

    private LinkedHashMap<LatLng, Double> sortHashMapByValues(HashMap<LatLng, Double> passedMap) {
        List<LatLng> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
     //   Collections.sort(mapKeys);

        LinkedHashMap<LatLng, Double> sortedMap = new LinkedHashMap<>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<LatLng> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                LatLng key = keyIt.next();
                Double comp1 = passedMap.get(key);
                Double comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
