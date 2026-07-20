package com.example.planinagod.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int planId;
    public String content;
    public Date createdAt;
    public boolean isPinned;

    public Note(int planId, String content, Date createdAt, boolean isPinned) {
        this.planId = planId;
        this.content = content;
        this.createdAt = createdAt;
        this.isPinned = isPinned;
    }
}