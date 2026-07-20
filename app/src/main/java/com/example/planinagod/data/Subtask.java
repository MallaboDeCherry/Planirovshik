package com.example.planinagod.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subtasks")
public class Subtask {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int planId;          // ID родительского плана
    public String title;        // Название подзадачи
    public boolean isCompleted; // Выполнена или нет

    public Subtask(int planId, String title, boolean isCompleted) {
        this.planId = planId;
        this.title = title;
        this.isCompleted = isCompleted;
    }
}