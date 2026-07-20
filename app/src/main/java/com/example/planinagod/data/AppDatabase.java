package com.example.planinagod.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Plan.class, ProgressRecord.class, Subtask.class}, version = 5)  // ← Увеличили с 4 до 5
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlanDao planDao();
    public abstract ProgressRecordDao progressRecordDao();
    public abstract SubtaskDao subtaskDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "planner_database")
                            .fallbackToDestructiveMigration()  // ← Это позволяет пересоздать БД при изменении схемы
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}