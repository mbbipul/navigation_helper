package com.example.navigation.navigation;

import android.content.Context;
import android.location.Location;
import android.speech.tts.TextToSpeech;

import com.example.navigation.utils.Distance;
import com.example.navigation.utils.NavRoute;
import com.example.navigation.utils.NavigationPoint;
import com.example.navigation.utils.NavigationUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.hoan.dsensor_master.DProcessedSensor;
import com.hoan.dsensor_master.DSensorEvent;
import com.hoan.dsensor_master.DSensorManager;
import com.hoan.dsensor_master.interfaces.DProcessedEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class Navigation {

    private NavRoute navRoute;
    private Context context;
    private TextToSpeech textToSpeech;
    private List<LatLng> latLngs;
    double compass;
    NavigationPoint currentNavigationPoint;
    public Navigation(Context context,NavRoute nav){
        this.context = context;
        this.navRoute = nav;
        textToSpeech=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        DSensorManager.startDProcessedSensor(context, DProcessedSensor.TYPE_3D_COMPASS,
                new DProcessedEventListener() {
                    @Override
                    public void onProcessedValueChanged(DSensorEvent dSensorEvent) {
                        // update UI
                        // dSensorEvent.values[0] is the azimuth.
                        if (Float.isNaN(dSensorEvent.values[0])) {
                            textToSpeech.speak("Compass is not working ," +
                                            "please calibrate ypur phone."
                                    ,TextToSpeech.QUEUE_FLUSH,null);

                        } else {
                            compass = Math.toDegrees(dSensorEvent.values[0]);
                            if (compass < 0) {
                                compass = (compass + 360) % 360;
                            }
                        }
                        //Toast.makeText(RoutesActivity.this, String.valueOf(dSensorEvent.values[0]), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public Navigation(List<LatLng> latLngs){
        this.latLngs = latLngs;
    }

    private boolean isLocationOnPath(LatLng latLng){
        return NavigationUtils.isLocationOnPath(latLng,navRoute,3);
    }

    private boolean isLocationNearToPath(LatLng latLng){
        return isLocationOnPath(latLng);
    }

    private boolean isLocationNearToRoute(LatLng latLng){
        return NavigationUtils.isLocationOnPath(latLng,navRoute,10);
    }


    public void startNavigation(Location location){
        float hdop = location.getAccuracy()/5;
        float MIN_DIST_POINT_TO_POINT = 10*hdop+15;
        double elapsedDistance;
        int ALPHA_MIN ;

        LatLng currentPoint = new LatLng(location.getLatitude(),location.getLongitude());
        ArrayList<Location> locations = navRoute.getRoutePoints();
        int nearestPointIndex = navRoute.getNearestPointIndex(location);
        int lastPointIndex = navRoute.getLastPointIndex();

        currentNavigationPoint = new NavigationPoint(nearestPointIndex,
                navRoute.getNextPointIndex(nearestPointIndex),
                navRoute.getPrevPointIndex(nearestPointIndex)
        );

        if (hdop > 5.0){
            textToSpeech.speak("Gps accurecy is very low",TextToSpeech.QUEUE_FLUSH,null);
            return;
        }
        elapsedDistance = 10;
        if(elapsedDistance>MIN_DIST_POINT_TO_POINT)
            ALPHA_MIN = 15;
        else
            ALPHA_MIN = 35;

        if (isLocationNearToRoute(currentPoint)){
            if(hasDeviation(location)){
                //textToSpeech.speak("You are right track",TextToSpeech.QUEUE_FLUSH,null);

            }else {
                //textToSpeech.speak("You are too far from the path",TextToSpeech.QUEUE_FLUSH,null);

            }
        }
        else {
            textToSpeech.speak("You are too far from the route",TextToSpeech.QUEUE_FLUSH,null);
        }

    }

    private void returnToTrack(Location location){

    }

    private float calcBearing(Location loc1,Location loc2){
        return loc1.bearingTo(loc2);
    }

    private  void navigateToAzimuth(float alpha2){
        double azimuth;
        azimuth = azimuth(alpha2);

    }

    private void turnAround(double azimuth){
        if (Math.abs(azimuth) != 5){
            if (azimuth<0)
                textToSpeech.speak("Please turn left ",TextToSpeech.QUEUE_FLUSH,null);
            else
                textToSpeech.speak("Please turn right ",TextToSpeech.QUEUE_FLUSH,null);
        }else
            textToSpeech.speak("Move ahead",TextToSpeech.QUEUE_FLUSH,null);

    }
    private double azimuth(double alpha2){
        double deviceAzimuth ;
        if (compass > 180)
            deviceAzimuth = 360 - compass;
        else
            deviceAzimuth = -compass;
        return deviceAzimuth + alpha2;
    }

    private float calcDeviationInDegree(Location location){
        Location prevLoc = navRoute.getPointLocation(currentNavigationPoint.getPreviousPointIndex());
        Location nextLoc = navRoute.getPointLocation(currentNavigationPoint.getCurrentPointIndex());
        float alpha1 = calcBearing(prevLoc,location);
        float alpha2 = calcBearing(location,nextLoc);
       // float alpha = alpha2 - alpha1 ;
       // retrun alpha;
        return alpha2;
    }
    private boolean hasDeviation(Location location){
        float hdop = location.getAccuracy()/5;
        double dth = (10*hdop)+5;
        if (getDistanceFromNextPoint(location)>=dth)
            return true;
        return false;
    }

    private double getDistanceFromNextPoint(Location location){
        Distance dis = new Distance();
        Location nextPoint = navRoute.getPointLocation(
                currentNavigationPoint.getNextPointIndex());
        double distance = dis.distance(location,nextPoint);
        return distance;
    }

    public void nav(Location location){

        ArrayList<Location> locations = navRoute.getRoutePoints();
        int nearestPointIndex = navRoute.getNearestPointIndex(location);

        NavigationPoint currentNavigationPoint = new NavigationPoint(nearestPointIndex,
                navRoute.getNextPointIndex(nearestPointIndex),
                navRoute.getPrevPointIndex(nearestPointIndex)
        );

        if(isLocationNearToPath(
                navRoute.getPointLatLng(nearestPointIndex))){
            textToSpeech.speak("You are right track",TextToSpeech.QUEUE_FLUSH,null);
        }else {
            textToSpeech.speak("You are too far from the path",TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    private List<LatLng> getNearestPath(NavigationPoint currentNavigationPoint){
        List<LatLng> nearestPath = new ArrayList<>();
        ///if (currentNavigationPoint.getPreviousPointIndex() >= 0)
            nearestPath.add(navRoute.getPointLatLng(0));
        //if (currentNavigationPoint.getCurrentPointIndex() >= 0)
            nearestPath.add(navRoute.getPointLatLng(1));
       // if (currentNavigationPoint.getNextPointIndex() >= 0)
        //    nearestPath.add(navRoute.getPointLatLng(currentNavigationPoint.getNextPointIndex()));
        return nearestPath;
    }

}
