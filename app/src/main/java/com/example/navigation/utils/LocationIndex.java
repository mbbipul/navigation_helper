package com.example.navigation.utils;

import android.location.Location;

public class LocationIndex{
    private int index;
    private Location location;

    public LocationIndex(Location location,int index){
        this.location = location;
        this.index = index;
    }

    public void setLocationIndex(Location location,int index){
        this.location = location;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}