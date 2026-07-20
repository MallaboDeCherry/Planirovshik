package com.example.planinagod.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface ProgressRecordDao {
    @Insert
    void insert(ProgressRecord record);

    @Delete
    void delete(ProgressRecord record);

    @Query("SELECT * FROM progress_records WHERE planId = :planId ORDER BY date DESC")
    List<ProgressRecord> getRecordsForPlan(int planId);

    @Query("DELETE FROM progress_records WHERE planId = :planId")
    void deleteRecordsForPlan(int planId);

    @Query("SELECT SUM(amount) FROM progress_records WHERE planId = :planId")
    double getTotalProgress(int planId);
}