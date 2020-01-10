package com.example.navigation.navigation;

import android.content.Context;
import android.location.Location;
import android.speech.tts.TextToSpeech;

import com.example.navigation.utils.NavRoute;
import com.example.navigation.utils.NavigationPoint;
import com.example.navigation.utils.NavigationUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Navigation {

    private NavRoute navRoute;
    private Context context;
    private TextToSpeech textToSpeech;
    private List<LatLng> latLngs;
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
        LatLng currentPoint = new LatLng(location.getLatitude(),location.getLongitude());
        ArrayList<Location> locations = navRoute.getRoutePoints();
        int nearestPointIndex = navRoute.getNearestPointIndex(location);

        NavigationPoint currentNavigationPoint = new NavigationPoint(nearestPointIndex,
                navRoute.getNextPointIndex(nearestPointIndex),
                navRoute.getPrevPointIndex(nearestPointIndex)
        );

        if (isLocationNearToRoute(currentPoint)){
            if(isLocationNearToPath(currentPoint)){
                textToSpeech.speak("You are right track",TextToSpeech.QUEUE_FLUSH,null);
            }else {
                //textToSpeech.speak("You are too far from the path",TextToSpeech.QUEUE_FLUSH,null);

            }
        }
        else {
            textToSpeech.speak("You are too far from the route",TextToSpeech.QUEUE_FLUSH,null);
        }

    }

    private  void navigateToAzimuth(float destAzimuth){
        float currAzimuth = -1;
        textToSpeech.speak(
                "Turn round until you hear stop",TextToSpeech.QUEUE_FLUSH,null);
        while(currAzimuth != 5%(destAzimuth)){

        }
//        currAzimuth = Compass.getAzimuth()
//        wait(2sec.)
//        endwhile
//        TTS.say(“Follow this direction”)
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
