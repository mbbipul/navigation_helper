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

    @Query("SELECT * FROM location WHERE id == :id")
    LocationD getLocationById(Long id);

    @Query("SELECT COUNT(*) from location")
    int countUsers();

    @Query("DELETE from location")
    void deleteAll();

    @Insert
    List<Long> insertAll(LocationD... locationDS);

    @Insert
    Long insertOne(LocationD locationD);

    @Delete
    void delete(LocationD locationD);
}
