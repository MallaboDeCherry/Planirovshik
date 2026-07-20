package com.example.planinagod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planinagod.R;
import com.example.planinagod.data.Plan;
import com.example.planinagod.ui.adapters.PlanAdapter;
import com.example.planinagod.ui.dialogs.AddPlanDialog;
import com.example.planinagod.dialogs.ArchiveConfirmDialog;
import com.example.planinagod.viewmodel.MainViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlanAdapter.OnPlanClickListener {

    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private MainViewModel viewModel;
    private FloatingActionButton fabAddPlan;
    private ChipGroup chipGroupFilters;

    private String currentCategory = "Все";
    private boolean showArchived = false;
    private List<Plan> allPlansList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        setupRecyclerView();
        setupChipFilters();
        observeData();
    }

    // ===== МЕНЮ =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_archive) {
            // Переключаем режим показа
            showArchived = !showArchived;
            item.setTitle(showArchived ? "Активные" : "Архив");

            // Применяем фильтр
            applyFilter(currentCategory);
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===== ИНИЦИАЛИЗАЦИЯ =====
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAddPlan = findViewById(R.id.fabAddPlan);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabAddPlan.setOnClickListener(v -> showAddPlanDialog());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new PlanAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void setupChipFilters() {
        chipGroupFilters.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (checkedIds.isEmpty()) return;

                int checkedId = checkedIds.get(0);
                Chip chip = findViewById(checkedId);

                if (chip != null) {
                    currentCategory = chip.getText().toString();
                    applyFilter(currentCategory);
                }
            }
        });
    }

    // ===== ФИЛЬТРАЦИЯ =====
    private void applyFilter(String category) {
        List<Plan> filteredPlans = new ArrayList<>();

        // Лог для отладки
        android.util.Log.d("MAIN_ACTIVITY", "=== ПРИМЕНЕНИЕ ФИЛЬТРА ===");
        android.util.Log.d("MAIN_ACTIVITY", "Всего планов: " + allPlansList.size());
        android.util.Log.d("MAIN_ACTIVITY", "showArchived: " + showArchived);
        android.util.Log.d("MAIN_ACTIVITY", "category: " + category);

        for (Plan plan : allPlansList) {
            // Лог каждого плана
            android.util.Log.d("MAIN_ACTIVITY", "План: " + plan.title +
                    ", isArchived: " + plan.isArchived);

            // Проверка архива
            if (showArchived) {
                // Показываем ТОЛЬКО архивированные
                if (!plan.isArchived) continue;
            } else {
                // Показываем ТОЛЬКО НЕ архивированные
                if (plan.isArchived) continue;
            }

            // Проверка категории
            if (category.equals("Все") ||
                    (plan.category != null && plan.category.equals(category))) {
                filteredPlans.add(plan);
            }
        }

        android.util.Log.d("MAIN_ACTIVITY", "Отфильтровано: " + filteredPlans.size());
        adapter.updatePlans(filteredPlans);
    }

    private void observeData() {
        viewModel.getAllPlans().observe(this, plans -> {
            if (plans != null) {
                allPlansList = plans;
                applyFilter(currentCategory);
            }
        });
    }

    private void showAddPlanDialog() {
        AddPlanDialog dialog = new AddPlanDialog();
        dialog.setOnPlanAddedListener(plan -> {
            viewModel.insertPlan(plan);
            Snackbar.make(recyclerView, "План \"" + plan.title + "\" добавлен!",
                    Snackbar.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "add_plan_dialog");
    }

    // ===== ОБРАБОТЧИКИ =====
    @Override
    public void onPlanClick(Plan plan) {
        Intent intent = new Intent(this, PlanDetailActivity.class);
        intent.putExtra("PLAN_ID", plan.id);
        startActivity(intent);
    }

    @Override
    public void onPlanLongClick(Plan plan) {
        Snackbar.make(recyclerView, "Удалить план \"" + plan.title + "\"?", Snackbar.LENGTH_LONG)
                .setAction("Удалить", v -> {
                    viewModel.deletePlan(plan);
                    Toast.makeText(this, "План удален", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onPlanArchiveClick(Plan plan) {
        ArchiveConfirmDialog dialog = new ArchiveConfirmDialog();
        dialog.setPlan(plan);
        dialog.setOnArchiveConfirmListener(new ArchiveConfirmDialog.OnArchiveConfirmListener() {
            @Override
            public void onConfirm(Plan p) {
                // Лог до изменения
                android.util.Log.d("MAIN_ACTIVITY", "ДО: " + p.title + ", isArchived: " + p.isArchived);

                // Переключаем статус
                p.isArchived = !p.isArchived;

                // Лог после изменения
                android.util.Log.d("MAIN_ACTIVITY", "ПОСЛЕ: " + p.title + ", isArchived: " + p.isArchived);

                // Сохраняем в базу
                viewModel.updatePlan(p);

                String message = p.isArchived ?
                        "📁 План \"" + p.title + "\" архивирован" :
                        "📋 План \"" + p.title + "\" восстановлен";

                Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // Ничего не делаем
            }
        });
        dialog.show(getSupportFragmentManager(), "archive_confirm_dialog");
    }
}