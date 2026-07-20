package com.example.planinagod.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "progress_records")
public class ProgressRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int planId;
    public double amount;
    public Date date;
    public String comment;
    public int colorResId;

    public ProgressRecord(int planId, double amount, Date date, String comment, int colorResId) {
        this.planId = planId;
        this.amount = amount;
        this.date = date;
        this.comment = comment;
        this.colorResId = colorResId;
    }
}