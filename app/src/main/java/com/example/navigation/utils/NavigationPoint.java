package com.example.navigation.utils;

public class NavigationPoint {
    private int currentPointIndex ;
    private int nextPointIndex;
    private int previousPointIndex;

    public NavigationPoint(int currentPointIndex, int nextPointIndex, int previousPointIndex) {
        this.currentPointIndex = currentPointIndex;
        this.nextPointIndex = nextPointIndex;
        this.previousPointIndex = previousPointIndex;
    }

    private boolean isReachToDest(int lastPointIndex){
        if (currentPointIndex == lastPointIndex )
            return  true;
        else
            return false;
    }

    public int getCurrentPointIndex() {
        return currentPointIndex;
    }

    public void setCurrentPointIndex(int currentPointIndex) {
        this.currentPointIndex = currentPointIndex;
    }

    public int getNextPointIndex() {
        return nextPointIndex;
    }

    public void setNextPointIndex(int nextPointIndex) {
        this.nextPointIndex = nextPointIndex;
    }

    public int getPreviousPointIndex() {
        return previousPointIndex;
    }

    public void setPreviousPointIndex(int previousPointIndex) {
        this.previousPointIndex = previousPointIndex;
    }
}
