package com.example.planinagod.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planinagod.R;
import com.example.planinagod.data.Plan;
import com.example.planinagod.data.ProgressRecord;
import com.example.planinagod.data.Subtask;
import com.example.planinagod.ui.adapters.HistoryAdapter;
import com.example.planinagod.ui.dialogs.AddProgressDialog;
import com.example.planinagod.viewmodel.PlanDetailViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlanDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvPercent, tvProgress, tvCompleted, tvRemaining;
    private CircularProgressIndicator circularProgress;
    private MaterialButton btnAddRecord;
    private RecyclerView rvHistory;

    // 👇 НОВЫЕ ЭЛЕМЕНТЫ ДЛЯ ПОДЗАДАЧ
    private LinearLayout subtasksContainer;
    private EditText etSubtaskInput;
    private MaterialButton btnAddSubtask;

    private PlanDetailViewModel viewModel;
    private HistoryAdapter historyAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private int planId;
    private List<Subtask> subtasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        planId = getIntent().getIntExtra("PLAN_ID", -1);
        if (planId == -1) {
            Toast.makeText(this, "Ошибка: план не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupViewModel();
        observeData();
        setupClickListeners();

        viewModel.loadPlan(planId);
        viewModel.loadHistory(planId);
        viewModel.loadSubtasks(planId);
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvPercent = findViewById(R.id.tvDetailPercent);
        tvProgress = findViewById(R.id.tvDetailProgress);
        tvCompleted = findViewById(R.id.tvCompleted);
        tvRemaining = findViewById(R.id.tvRemaining);
        circularProgress = findViewById(R.id.circularDetailProgress);
        btnAddRecord = findViewById(R.id.btnAddRecord);
        rvHistory = findViewById(R.id.rvHistory);

        // 👇 Инициализация подзадач
        subtasksContainer = findViewById(R.id.subtasksContainer);
        etSubtaskInput = findViewById(R.id.etSubtaskInput);
        btnAddSubtask = findViewById(R.id.btnAddSubtask);
    }

    private void setupRecyclerView() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setHasFixedSize(true);
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        rvHistory.setAdapter(historyAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PlanDetailViewModel.class);
    }

    private void observeData() {
        viewModel.getPlan().observe(this, plan -> {
            if (plan != null) {
                updateUI(plan);
            }
        });

        viewModel.getHistory().observe(this, records -> {
            if (records != null) {
                historyAdapter.updateRecords(records);
            }
        });

        // 👇 Наблюдаем за подзадачами
        viewModel.getSubtasks().observe(this, subtaskList -> {
            if (subtaskList != null) {
                subtasks = subtaskList;
                renderSubtasks();
            }
        });
    }

    private void setupClickListeners() {
        btnAddRecord.setOnClickListener(v -> showAddProgressDialog());

        // 👇 Добавление подзадачи
        btnAddSubtask.setOnClickListener(v -> {
            String title = etSubtaskInput.getText().toString().trim();
            if (title.isEmpty()) {
                etSubtaskInput.setError("Введите название подзадачи");
                return;
            }

            Subtask subtask = new Subtask(planId, title, false);
            viewModel.addSubtask(subtask);
            etSubtaskInput.setText("");
            Toast.makeText(this, "Подзадача добавлена", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(Plan plan) {
        tvTitle.setText(plan.title);

        if (plan.hasNumericGoal) {
            // С ЧИСЛОВОЙ ЦЕЛЬЮ
            int percent = plan.targetValue > 0 ? (int)(plan.currentValue / plan.targetValue * 100) : 0;
            if (percent > 100) percent = 100;

            tvPercent.setText(percent + "%");
            tvProgress.setText(plan.currentValue + " из " + plan.targetValue + " " + plan.unit);

            double completed = plan.currentValue;
            double remaining = Math.max(0, plan.targetValue - plan.currentValue);

            tvCompleted.setText(completed + " " + plan.unit + " (" + percent + "%)");
            tvRemaining.setText(remaining + " " + plan.unit + " (" + (100 - percent) + "%)");

            circularProgress.setProgress(percent);
        } else {
            // БЕЗ ЧИСЛОВОЙ ЦЕЛИ
            if (plan.isCompleted) {
                tvPercent.setText("✅ Выполнено");
                tvProgress.setText("Задача выполнена");
                tvCompleted.setText("Выполнено");
                tvRemaining.setText("—");
                circularProgress.setProgress(100);
            } else {
                tvPercent.setText("⏳ Ожидает");
                tvProgress.setText("Задача не выполнена");
                tvCompleted.setText("Не выполнено");
                tvRemaining.setText("—");
                circularProgress.setProgress(0);
            }
        }

        if (plan.isCompleted) {
            circularProgress.setIndicatorColor(getColor(R.color.green));
        } else {
            circularProgress.setIndicatorColor(getColor(R.color.purple_500));
        }
    }

    // 👇 ОТРИСОВКА ПОДЗАДАЧ
    private void renderSubtasks() {
        subtasksContainer.removeAllViews();

        if (subtasks.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Нет подзадач");
            emptyText.setTextColor(getColor(R.color.gray));
            emptyText.setPadding(0, 16, 0, 16);
            subtasksContainer.addView(emptyText);
            return;
        }

        for (Subtask subtask : subtasks) {
            View itemView = getLayoutInflater().inflate(R.layout.item_subtask, null);

            CheckBox checkBox = itemView.findViewById(R.id.checkSubtask);
            TextView tvTitle = itemView.findViewById(R.id.tvSubtaskTitle);

            checkBox.setChecked(subtask.isCompleted);
            tvTitle.setText(subtask.title);

            // Обработка изменения состояния
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                subtask.isCompleted = isChecked;
                viewModel.updateSubtask(subtask);
            });

            // Долгий клик для удаления
            itemView.setOnLongClickListener(v -> {
                viewModel.deleteSubtask(subtask);
                Toast.makeText(this, "Подзадача удалена", Toast.LENGTH_SHORT).show();
                return true;
            });

            subtasksContainer.addView(itemView);
        }
    }

    private void showAddProgressDialog() {
        Plan currentPlan = viewModel.getPlan().getValue();
        if (currentPlan == null) return;

        AddProgressDialog dialog = new AddProgressDialog();
        dialog.setPlan(currentPlan);
        dialog.setOnProgressAddedListener(record -> {
            viewModel.addProgress(record);
            Toast.makeText(PlanDetailActivity.this,
                    "Добавлено: +" + record.amount + " " + currentPlan.unit,
                    Toast.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "add_progress_dialog");
    }
}