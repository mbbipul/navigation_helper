package com.example.navigation.navigation;

import android.content.Context;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView details;
    public Navigation(Context context,NavRoute nav,TextView details){
        this.context = context;
        this.navRoute = nav;
        this.details = details;
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
                            compass = Distance.round(Math.toDegrees(dSensorEvent.values[0]),1);
                            if (compass < 0) {
                                compass = Distance.round((compass + 360) % 360,1);
                            }
                        }
                        //Toast.makeText(RoutesActivity.this, String.valueOf(dSensorEvent.values[0]), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public Navigation(List<LatLng> latLngs){
        this.latLngs = latLngs;
    }

    public void check(){
        Toast.makeText(context, "initialize", Toast.LENGTH_SHORT).show();

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
        textToSpeech.speak("Navigation start",TextToSpeech.QUEUE_FLUSH,null);

        details.setText("");
        float hdop = location.getAccuracy()/5;
        float MIN_DIST_POINT_TO_POINT = 10*hdop+15;
//        double elapsedDistance;
//        int ALPHA_MIN ;

        LatLng currentPoint = new LatLng(location.getLatitude(),location.getLongitude());
        ArrayList<Location> locations = navRoute.getRoutePoints();
        int nearestPointIndex = navRoute.getNearestPointIndex(location);
        int lastPointIndex = navRoute.getLastPointIndex();

        details.setText(details.getText()+String.valueOf("current index"+nearestPointIndex+"\n"));
        currentNavigationPoint = new NavigationPoint(nearestPointIndex,
                navRoute.getNextPointIndex(nearestPointIndex),
                navRoute.getPrevPointIndex(nearestPointIndex)
        );
        details.setText(details.getText()+"Accurecy "+String.valueOf(hdop)+"\n");

//        if (hdop > 5.0){
//            Toast.makeText(context, "low gps", Toast.LENGTH_SHORT).show();
//            textToSpeech.speak("Gps accurecy is very low",TextToSpeech.QUEUE_FLUSH,null);
//            return;
//        }

//        elapsedDistance = 10;
//        if(elapsedDistance>MIN_DIST_POINT_TO_POINT)
//            ALPHA_MIN = 15;
//        else
//            ALPHA_MIN = 35;

        if (isLocationNearToRoute(currentPoint)){
            if (nearestPointIndex == navRoute.getN()-1){
                Toast.makeText(context, "reached", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("You are reached ",TextToSpeech.QUEUE_FLUSH,null);

            }else {
                if(hasDeviation(location)){
                    //textToSpeech.speak("You are right track",TextToSpeech.QUEUE_FLUSH,null);
                    details.setText("\n"+details.getText()+"has deviation\n");
                    Toast.makeText(context, "has deviation", Toast.LENGTH_SHORT).show();
                    returnToTrack(location);
                }else {
                    details.setText(details.getText()+"\n");
                    Toast.makeText(context, "Move forward", Toast.LENGTH_SHORT).show();
                    textToSpeech.speak("Move forward",TextToSpeech.QUEUE_FLUSH,null);
                }
            }

        }
        else {
            Toast.makeText(context, "too far", Toast.LENGTH_SHORT).show();

            textToSpeech.speak("You are too far from the route",TextToSpeech.QUEUE_FLUSH,null);
        }

    }

    private void returnToTrack(Location location){
        navigateToAzimuth(location);
    }

    private double calcBearing(Location loc1,Location loc2){
        double bearing = loc1.bearingTo(loc2);
        details.setText(details.getText()+"bearing " + String.valueOf(bearing));
        return bearing;
        //return NavigationUtils.calcBearing(loc1,loc2);
    }

    private  void navigateToAzimuth(Location location){
        double alpha2 = calcDeviationInDegree(location);
        turnAround(azimuth(alpha2),location);
    }

    private void turnAround(double azimuth,Location location){
        details.setText(details.getText()+"\n"+"Azimuth :"+String.valueOf(azimuth));
        double nextPoinDistance = getDistanceFromNextPoint(location);

        if (nextPoinDistance < 1){
            String command = navRoute.getRouteDirection().get(currentNavigationPoint.getCurrentPointIndex());
            textToSpeech.speak(command,TextToSpeech.QUEUE_FLUSH,null);

        }
        else {
            if (Math.abs(azimuth) != 5){
                if (azimuth>0){
                    if (azimuth<=180){
                        speak("left");
                    }else{
                        speak("right");
                    }

                }
                else {
                    if (Math.abs(azimuth)<=180){
                        speak("right");
                    }else{
                        speak("left");
                    }
                    Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();
                    textToSpeech.speak("Please turn right ", TextToSpeech.QUEUE_FLUSH, null);
                }
            }else {
                Toast.makeText(context, "move", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("Move ahead", TextToSpeech.QUEUE_FLUSH, null);
            }
        }


    }

    private void speak(String speak){
        Toast.makeText(context, speak, Toast.LENGTH_SHORT).show();
        textToSpeech.speak("Please turn "+speak,TextToSpeech.QUEUE_FLUSH,null);
    }
    private double azimuth(double alpha2){
//        double deviceAzimuth ;
//        if (compass > 180)
//            deviceAzimuth = 360 - compass;
//        else
//            deviceAzimuth = -compass;
        details.setText(details.getText()+"compass " + String.valueOf(compass));

        return Distance.round(compass + alpha2,6);
    }

    private double calcDeviationInDegree(Location location){
        Location prevLoc = navRoute.getPointLocation(currentNavigationPoint.getPreviousPointIndex());
        Location nextLoc = navRoute.getPointLocation(currentNavigationPoint.getCurrentPointIndex());
        double alpha1 = calcBearing(prevLoc,location);
        double alpha2 = calcBearing(location,nextLoc);
       // float alpha = alpha2 - alpha1 ;
       // retrun alpha;
        return  Distance.round(alpha2,6);
    }
    private boolean hasDeviation(Location location){
//        float hdop = location.getAccuracy()/5;
//
//        double dth = (10*hdop)+5;

        double dth = 2;
        double d = getDistanceFromNextPoint(location);
        details.setText(details.getText()+"\n"+"Min Dis :"+String.valueOf(dth));
        details.setText(details.getText()+"\n"+"Min_m Dist :"+String.valueOf(d));

        if (d >= dth)
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

}
