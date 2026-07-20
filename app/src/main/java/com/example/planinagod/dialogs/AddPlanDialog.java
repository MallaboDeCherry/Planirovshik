package com.example.planinagod.ui.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.example.planinagod.R;
import com.example.planinagod.data.Plan;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddPlanDialog extends DialogFragment {

    // Основные поля
    private TextInputEditText etTitle, etDescription;
    private TextView tvStartDate, tvEndDate;
    private Spinner spinnerCategory;
    private ChipGroup chipGroupColors;
    private MaterialButton btnSave, btnCancel;

    // 👇 НОВЫЕ ПОЛЯ ДЛЯ ЧИСЛОВОЙ ЦЕЛИ
    private SwitchMaterial switchNumericGoal;
    private com.google.android.material.textfield.TextInputLayout tilTargetValue;
    private TextInputEditText etTargetValue;
    private com.google.android.material.textfield.TextInputLayout tilUnit;
    private TextInputEditText etUnit;

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private int selectedColor = R.color.card_blue;

    private OnPlanAddedListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public interface OnPlanAddedListener {
        void onPlanAdded(Plan plan);
    }

    public void setOnPlanAddedListener(OnPlanAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_plan, container, false);

        initViews(view);
        setupSpinner();
        setupDatePickers();
        setupColorChips();
        setupButtons();
        setupSwitchListener();

        return view;
    }

    private void initViews(View view) {
        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        chipGroupColors = view.findViewById(R.id.chipGroupColors);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        // 👇 НОВЫЕ ПОЛЯ
        switchNumericGoal = view.findViewById(R.id.switchNumericGoal);
        tilTargetValue = view.findViewById(R.id.tilTargetValue);
        etTargetValue = view.findViewById(R.id.etTargetValue);
        tilUnit = view.findViewById(R.id.tilUnit);
        etUnit = view.findViewById(R.id.etUnit);

        tvStartDate.setText(dateFormat.format(startDate.getTime()));
        endDate.add(Calendar.YEAR, 1);
        tvEndDate.setText(dateFormat.format(endDate.getTime()));
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePickers() {
        tvStartDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        startDate.set(year, month, dayOfMonth);
                        tvStartDate.setText(dateFormat.format(startDate.getTime()));
                    },
                    startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        tvEndDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        endDate.set(year, month, dayOfMonth);
                        tvEndDate.setText(dateFormat.format(endDate.getTime()));
                    },
                    endDate.get(Calendar.YEAR),
                    endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });
    }

    private void setupColorChips() {
        final int[] colorIds = {R.color.card_blue, R.color.card_green, R.color.card_orange,
                R.color.card_pink, R.color.card_purple, R.color.card_teal};
        int[] colorNames = {R.string.color_blue, R.string.color_green, R.string.color_orange,
                R.string.color_pink, R.string.color_purple, R.string.color_teal};

        for (int i = 0; i < colorIds.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(colorNames[i]);
            chip.setChipBackgroundColorResource(colorIds[i]);
            chip.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            chip.setCheckable(true);
            chip.setId(i);

            if (i == 0) {
                chip.setChecked(true);
                selectedColor = colorIds[0];
            }

            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedColor = colorIds[buttonView.getId()];
                    }
                }
            });

            chipGroupColors.addView(chip);
        }
    }

    // 👇 ОБРАБОТКА ПЕРЕКЛЮЧАТЕЛЯ
    private void setupSwitchListener() {
        switchNumericGoal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Показываем поля для числовой цели
                tilTargetValue.setVisibility(View.VISIBLE);
                tilUnit.setVisibility(View.VISIBLE);
            } else {
                // Скрываем поля для числовой цели
                tilTargetValue.setVisibility(View.GONE);
                tilUnit.setVisibility(View.GONE);
                etTargetValue.setText("");
                etUnit.setText("");
            }
        });
        // По умолчанию скрываем
        tilTargetValue.setVisibility(View.GONE);
        tilUnit.setVisibility(View.GONE);
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                createAndSavePlan();
            }
        });
    }

    private boolean validateInput() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            etTitle.setError("Введите название плана");
            etTitle.requestFocus();
            return false;
        }

        if (startDate.getTime().after(endDate.getTime())) {
            Toast.makeText(getContext(), "Дата начала не может быть позже даты окончания",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Если включена числовая цель, проверяем что введено значение
        if (switchNumericGoal.isChecked()) {
            String targetStr = etTargetValue.getText().toString().trim();
            if (targetStr.isEmpty()) {
                etTargetValue.setError("Введите максимальное значение");
                etTargetValue.requestFocus();
                return false;
            }
            try {
                double target = Double.parseDouble(targetStr);
                if (target <= 0) {
                    etTargetValue.setError("Значение должно быть больше 0");
                    etTargetValue.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                etTargetValue.setError("Введите корректное число");
                etTargetValue.requestFocus();
                return false;
            }

            String unit = etUnit.getText().toString().trim();
            if (unit.isEmpty()) {
                etUnit.setError("Введите единицу измерения");
                etUnit.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void createAndSavePlan() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        Plan plan;

        if (switchNumericGoal.isChecked()) {
            // С ЧИСЛОВОЙ ЦЕЛЬЮ
            double targetValue = Double.parseDouble(etTargetValue.getText().toString().trim());
            String unit = etUnit.getText().toString().trim();

            plan = new Plan(
                    title,
                    description,
                    category,
                    endDate.getTime(),
                    selectedColor,
                    targetValue,
                    0.0,
                    unit
            );
        } else {
            // БЕЗ ЧИСЛОВОЙ ЦЕЛИ (простая задача)
            plan = new Plan(
                    title,
                    description,
                    category,
                    endDate.getTime(),
                    selectedColor
            );
        }

        if (listener != null) {
            listener.onPlanAdded(plan);
        }

        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}