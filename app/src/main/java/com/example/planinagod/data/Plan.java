package com.example.planinagod.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.room.Ignore;
import java.util.Date;

@Entity(tableName = "plans")
public class Plan {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public String category;
    public Date deadline;
    public int colorResId;

    public boolean hasNumericGoal;
    public double targetValue;
    public double currentValue;
    public String unit;

    public boolean isCompleted;
    public boolean isArchived;  // ← ЭТО ПОЛЕ ДОЛЖНО БЫТЬ!

    // ===== ОСНОВНОЙ КОНСТРУКТОР (для Room) =====
    public Plan(String title, String description, String category, Date deadline,
                int colorResId, boolean hasNumericGoal, double targetValue,
                double currentValue, String unit, boolean isCompleted, boolean isArchived) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.deadline = deadline;
        this.colorResId = colorResId;
        this.hasNumericGoal = hasNumericGoal;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.unit = unit;
        this.isCompleted = isCompleted;
        this.isArchived = isArchived;
    }

    // ===== КОНСТРУКТОР ДЛЯ ЗАДАЧ С ЧИСЛОВОЙ ЦЕЛЬЮ =====
    @Ignore
    public Plan(String title, String description, String category, Date deadline,
                int colorResId, double targetValue, double currentValue, String unit) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.deadline = deadline;
        this.colorResId = colorResId;
        this.hasNumericGoal = true;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.unit = unit;
        this.isCompleted = currentValue >= targetValue;
        this.isArchived = false;
    }

    // ===== КОНСТРУКТОР ДЛЯ ЗАДАЧ БЕЗ ЧИСЛОВОЙ ЦЕЛИ =====
    @Ignore
    public Plan(String title, String description, String category, Date deadline,
                int colorResId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.deadline = deadline;
        this.colorResId = colorResId;
        this.hasNumericGoal = false;
        this.targetValue = 0;
        this.currentValue = 0;
        this.unit = "";
        this.isCompleted = false;
        this.isArchived = false;
    }
}