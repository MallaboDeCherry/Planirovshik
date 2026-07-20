package com.example.planinagod.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;
import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes WHERE planId = :planId ORDER BY isPinned DESC, createdAt DESC")
    List<Note> getNotesForPlan(int planId);

    @Query("DELETE FROM notes WHERE planId = :planId")
    void deleteNotesForPlan(int planId);
}