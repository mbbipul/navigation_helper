package com.example.navigation;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location")
public class LocationD {

    @PrimaryKey(autoGenerate = true)
    private  int id;

    @ColumnInfo(name = "lattiude")
    private double lattitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "altitude")
    private double altitude;

    @ColumnInfo(name = "bearing")
    private float bearing;

    @ColumnInfo(name = "speed")
    private float speed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }



}
