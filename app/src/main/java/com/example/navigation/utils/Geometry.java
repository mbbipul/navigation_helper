package com.example.navigation.utils;

import android.location.Location;

public class Geometry {

    double x1,y1,x2,y2,x_d,y_d;
    public Geometry(double x1,double y1,double x2,double y2,double x_d,double y_d){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x_d =  x_d ;
        this.y_d =  y_d ;
    }

    public Geometry(Location loc1,Location loc2,Location currPoint){
        this.x1 = loc1.getLatitude();
        this.y1 = loc1.getLongitude();
        this.x2 = loc2.getLatitude();
        this.y2 = loc2.getLongitude();
        this.x_d =  currPoint.getLatitude() ;
        this.y_d =  currPoint.getLongitude() ;
    }
    public double getC() {
        return ((getX1()*getA()) + (getY1() * getB()));
    }


    public double getK() {
        return (((getB() * getX_d()) + (getA() * getY_d()))*-1);
    }

    public double getX1() {
        return x1;
    }

    public double getX_d() {
        return x_d;
    }

    public double getY_d() {
        return y_d;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getX() {
        double cby =(getK() - (getA()*getY()));
        return -1*(cby/getB());
    }

    public double getY() {
        double bcak = ((getB()*getC())-(getA()*getK()));
        double ab2 = ((getA()*getA())+(getB()*getB()));
        return (bcak/ab2);
    }

    public double getA() {
        return getY1() - getY2();
    }

    public double getB() {
        return getX1() - getX2();
    }

}
