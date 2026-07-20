package com.example.planinagod.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface SubtaskDao {
    @Insert
    void insert(Subtask subtask);

    @Update
    void update(Subtask subtask);

    @Delete
    void delete(Subtask subtask);

    @Query("SELECT * FROM subtasks WHERE planId = :planId ORDER BY id ASC")
    List<Subtask> getSubtasksForPlan(int planId);

    @Query("DELETE FROM subtasks WHERE planId = :planId")
    void deleteSubtasksForPlan(int planId);

    @Query("SELECT COUNT(*) FROM subtasks WHERE planId = :planId AND isCompleted = 0")
    int getIncompleteCount(int planId);

    @Query("SELECT COUNT(*) FROM subtasks WHERE planId = :planId")
    int getTotalCount(int planId);
}