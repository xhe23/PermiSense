package com.installedapps.com.installedapps.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.installedapps.com.installedapps.model.Scenario;

import java.util.List;

@Dao
public interface ScenarioDao {
    @Query("SELECT * FROM scenario")
    List<Scenario> getAll();

    @Insert
    void insert(Scenario scenario);

    @Update
    void update(Scenario scenario);

    @Delete
    void delete(Scenario scenario);
}
