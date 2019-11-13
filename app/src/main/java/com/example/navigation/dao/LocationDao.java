package com.example.navigation.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.navigation.entity.LocationD;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location")
    List<LocationD> getAll();

    @Query("SELECT COUNT(*) from location")
    int countUsers();

    @Insert
    void insertAll(LocationD... users);

    @Delete
    void delete(LocationD locationD);
}
