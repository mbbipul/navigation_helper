package com.example.navigation.utils;

import com.example.navigation.entity.LocationD;

import java.util.List;

public interface DatabaseListner {
    void fetchAllData(List<LocationD> locations);
}
