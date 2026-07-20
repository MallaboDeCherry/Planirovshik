package com.example.planinagod.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface PlanDao {
    @Insert
    void insert(Plan plan);

    @Update
    void update(Plan plan);

    @Delete
    void delete(Plan plan);

    // Активные планы (не архивированные)
    @Query("SELECT * FROM plans WHERE isArchived = 0 ORDER BY deadline ASC")
    List<Plan> getActivePlans();  // ← НОВЫЙ ЗАПРОС

    // Архивные планы
    @Query("SELECT * FROM plans WHERE isArchived = 1 ORDER BY deadline DESC")
    List<Plan> getArchivedPlans();  // ← НОВЫЙ ЗАПРОС

    // Все планы (только активные)
    @Query("SELECT * FROM plans WHERE isArchived = 0 ORDER BY deadline ASC")
    List<Plan> getAllPlans();

    @Query("SELECT * FROM plans WHERE id = :planId")
    Plan getPlanById(int planId);

    @Query("SELECT * FROM plans WHERE category = :category AND isArchived = 0")
    List<Plan> getPlansByCategory(String category);

}