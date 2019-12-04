package com.example.navigation.utils;

import com.example.navigation.entity.LocationD;

import java.util.ArrayList;
import java.util.List;

public interface DatabaseListner {
    void fetchRouteInfo(ArrayList<RouteInfo> routeInfos);
}
