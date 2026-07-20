package com.example.planinagod.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.planinagod.R;
import com.example.planinagod.data.Plan;
import com.google.android.material.button.MaterialButton;

public class ArchiveConfirmDialog extends DialogFragment {

    private Plan plan;
    private OnArchiveConfirmListener listener;

    public interface OnArchiveConfirmListener {
        void onConfirm(Plan plan);
        void onCancel();
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void setOnArchiveConfirmListener(OnArchiveConfirmListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_archive_confirm, container, false);

        TextView tvMessage = view.findViewById(R.id.tvArchiveMessage);
        MaterialButton btnConfirm = view.findViewById(R.id.btnArchiveConfirm);
        MaterialButton btnCancel = view.findViewById(R.id.btnArchiveCancel);

        if (plan != null) {
            tvMessage.setText("Вы уверены, что хотите архивировать план \"" + plan.title + "\"?");
        }

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirm(plan);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });

        return view;
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