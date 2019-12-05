package com.example.navigation.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.navigation.entity.Route;

import java.util.List;

@Dao
public interface RouteDao {

    @Query("SELECT * FROM routes")
    List<Route> getAll();

    @Query("SELECT COUNT(*) from routes")
    int countUsers();

    @Query("SELECT DISTINCT routename FROM routes")
    List<String> getAllRouteName();

    @Query("SELECT * FROM routes WHERE routename == :routeName ORDER BY time ")
    List<Route> getRoutesByRouteName(String routeName);

    @Insert
    void insertAll(Route... routes);

    @Insert
    void insertOne(Route routes);

    @Delete
    void delete(Route route);

    @Query("DELETE from routes")
    void deleteAll();
}
