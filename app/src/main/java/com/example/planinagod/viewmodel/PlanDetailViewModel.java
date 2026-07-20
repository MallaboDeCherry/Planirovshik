package com.example.planinagod.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.planinagod.data.AppDatabase;
import com.example.planinagod.data.Plan;
import com.example.planinagod.data.ProgressRecord;
import com.example.planinagod.data.Subtask;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlanDetailViewModel extends AndroidViewModel {

    private AppDatabase database;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private MutableLiveData<Plan> plan = new MutableLiveData<>();
    private MutableLiveData<List<ProgressRecord>> history = new MutableLiveData<>();
    private MutableLiveData<List<Subtask>> subtasks = new MutableLiveData<>();  // ← НОВОЕ

    public PlanDetailViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<Plan> getPlan() {
        return plan;
    }

    public LiveData<List<ProgressRecord>> getHistory() {
        return history;
    }

    public LiveData<List<Subtask>> getSubtasks() {  // ← НОВЫЙ МЕТОД
        return subtasks;
    }

    public void loadPlan(int planId) {
        executor.execute(() -> {
            Plan p = database.planDao().getPlanById(planId);
            plan.postValue(p);
        });
    }

    public void loadHistory(int planId) {
        executor.execute(() -> {
            List<ProgressRecord> records = database.progressRecordDao().getRecordsForPlan(planId);
            history.postValue(records);
        });
    }

    public void loadSubtasks(int planId) {  // ← НОВЫЙ МЕТОД
        executor.execute(() -> {
            List<Subtask> list = database.subtaskDao().getSubtasksForPlan(planId);
            subtasks.postValue(list);
        });
    }

    public void addProgress(ProgressRecord record) {
        executor.execute(() -> {
            database.progressRecordDao().insert(record);

            Plan currentPlan = plan.getValue();
            if (currentPlan != null) {
                currentPlan.currentValue += record.amount;
                currentPlan.isCompleted = currentPlan.currentValue >= currentPlan.targetValue;
                database.planDao().update(currentPlan);
                plan.postValue(currentPlan);
            }

            loadHistory(record.planId);
        });
    }

    // ===== МЕТОДЫ ДЛЯ ПОДЗАДАЧ =====
    public void addSubtask(Subtask subtask) {
        executor.execute(() -> {
            database.subtaskDao().insert(subtask);
            loadSubtasks(subtask.planId);
        });
    }

    public void updateSubtask(Subtask subtask) {
        executor.execute(() -> {
            database.subtaskDao().update(subtask);
            loadSubtasks(subtask.planId);
        });
    }

    public void deleteSubtask(Subtask subtask) {
        executor.execute(() -> {
            database.subtaskDao().delete(subtask);
            loadSubtasks(subtask.planId);
        });
    }

    public void updatePlan(Plan plan) {
        executor.execute(() -> {
            database.planDao().update(plan);
            loadPlan(plan.id);
        });
    }
}