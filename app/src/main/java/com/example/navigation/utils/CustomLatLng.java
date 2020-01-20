package com.example.navigation.utils;

import com.google.android.gms.maps.model.LatLng;

public class CustomLatLng {
    LatLng latLng;
    String direction;
    int index;

    public LatLng getLatLng() {
        return latLng;
    }

    public String getDirection() {
        return direction;
    }

    public int getIndex() {
        return index;
    }

    public CustomLatLng(LatLng latLng, String direction, int index) {
        this.latLng = latLng;
        this.direction = direction;
        this.index = index;
    }
}
