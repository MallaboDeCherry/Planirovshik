package com.example.planinagod.ui.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.example.planinagod.R;
import com.example.planinagod.data.Plan;
import com.example.planinagod.data.ProgressRecord;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddProgressDialog extends DialogFragment {

    // 👇 НОВОЕ ПОЛЕ: Название записи
    private TextInputEditText etTitle;
    private TextInputEditText etAmount;
    private TextView tvDate;
    private ChipGroup chipGroupColors;
    private TextInputEditText etComment;
    private MaterialButton btnSave, btnCancel;

    private Calendar selectedDate = Calendar.getInstance();
    private int selectedColor = R.color.purple_500;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private Plan plan;
    private OnProgressAddedListener listener;

    public interface OnProgressAddedListener {
        void onProgressAdded(ProgressRecord record);
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void setOnProgressAddedListener(OnProgressAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_progress, container, false);

        initViews(view);
        setupDatePicker();
        setupColorChips();
        setupButtons();

        return view;
    }

    private void initViews(View view) {
        // 👇 Инициализируем все поля
        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        tvDate = view.findViewById(R.id.tvProgressDate);
        chipGroupColors = view.findViewById(R.id.chipGroupRecordColors);
        etComment = view.findViewById(R.id.etComment);
        btnSave = view.findViewById(R.id.btnSaveProgress);
        btnCancel = view.findViewById(R.id.btnCancelProgress);

        tvDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void setupDatePicker() {
        tvDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        tvDate.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
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

    private void setupButtons() {
        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                createAndSaveRecord();
            }
        });
    }

    // 👇 ВАЛИДАЦИЯ: проверяем только название
    private boolean validateInput() {
        // Название обязательно
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            etTitle.setError("Введите название записи");
            etTitle.requestFocus();
            return false;
        }

        // Количество проверяем только если есть числовая цель
        if (plan.hasNumericGoal) {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                etAmount.setError("Введите количество");
                etAmount.requestFocus();
                return false;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    etAmount.setError("Количество должно быть больше 0");
                    etAmount.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                etAmount.setError("Введите корректное число");
                etAmount.requestFocus();
                return false;
            }
        } else {
            // Если нет числовой цели, количество не требуется
            // Можно просто отметить как выполненное
            etAmount.setVisibility(View.GONE);
        }

        return true;
    }

    // 👇 СОЗДАНИЕ ЗАПИСИ
    private void createAndSaveRecord() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        // Количество: если не введено, ставим 0
        double amount = 0.0;
        if (!amountStr.isEmpty()) {
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                amount = 0.0;
            }
        }

        ProgressRecord record = new ProgressRecord(
                plan.id,
                amount,
                selectedDate.getTime(),
                title,  // 👈 Название записи сохраняем в comment
                selectedColor
        );

        // Если есть дополнительный комментарий, добавляем его к названию
        if (!comment.isEmpty()) {
            // Можно сохранить как-то иначе, или объединить
        }

        if (listener != null) {
            listener.onProgressAdded(record);
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