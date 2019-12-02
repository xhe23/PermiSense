package com.installedapps.com.installedapps.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.installedapps.com.installedapps.model.Rule;

import java.util.List;

@Dao
public interface RuleDao {
    @Query("SELECT * FROM rule")
    List<Rule> getAll();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Rule rule);

    @Update()
    void update(Rule rule);
}
