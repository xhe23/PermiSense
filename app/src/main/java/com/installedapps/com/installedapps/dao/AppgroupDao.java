package com.installedapps.com.installedapps.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.installedapps.com.installedapps.model.AppGroup;
import com.installedapps.com.installedapps.model.Scenario;

import java.util.List;

@Dao
public interface AppgroupDao {
    @Query("SELECT * FROM appgroup")
    List<AppGroup> getAll();

    @Insert
    void insert(AppGroup appgroup);

    @Update
    void update(AppGroup appgroup);

    @Delete
    void delete(AppGroup appgroup);
}
