package com.example.planinagod.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.planinagod.data.AppDatabase;
import com.example.planinagod.data.Plan;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private AppDatabase database;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private MutableLiveData<List<Plan>> allPlans = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        loadAllPlans();
    }

    public LiveData<List<Plan>> getAllPlans() {
        return allPlans;
    }

    // ===== ЗАГРУЗКА ВСЕХ ПЛАНОВ =====
    public void loadAllPlans() {
        executor.execute(() -> {
            List<Plan> plans = database.planDao().getAllPlans();
            allPlans.postValue(plans);
        });
    }

    // ===== ДОБАВЛЕНИЕ =====
    public void insertPlan(Plan plan) {
        executor.execute(() -> {
            database.planDao().insert(plan);
            loadAllPlans();
        });
    }

    // ===== ОБНОВЛЕНИЕ (ВАЖНО ДЛЯ АРХИВАЦИИ!) =====
    public void updatePlan(Plan plan) {
        executor.execute(() -> {
            database.planDao().update(plan);
            loadAllPlans();  // ← ОБЯЗАТЕЛЬНО обновляем список!
        });
    }

    // ===== УДАЛЕНИЕ =====
    public void deletePlan(Plan plan) {
        executor.execute(() -> {
            database.planDao().delete(plan);
            database.progressRecordDao().deleteRecordsForPlan(plan.id);
            loadAllPlans();
        });
    }
}