package com.example.planinagod.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planinagod.R;
import com.example.planinagod.data.ProgressRecord;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ProgressRecord> records;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public HistoryAdapter(List<ProgressRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ProgressRecord record = records.get(position);

        holder.tvDate.setText(dateFormat.format(record.date));
        holder.tvAmount.setText("+" + record.amount);

        if (record.comment != null && !record.comment.isEmpty()) {
            holder.tvComment.setText(record.comment);
            holder.tvComment.setVisibility(View.VISIBLE);
        } else {
            holder.tvComment.setVisibility(View.GONE);
        }

        if (record.colorResId != 0) {
            holder.viewColor.setBackgroundColor(
                    holder.itemView.getContext().getColor(record.colorResId)
            );
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateRecords(List<ProgressRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAmount, tvComment;
        View viewColor;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvAmount = itemView.findViewById(R.id.tvHistoryAmount);
            tvComment = itemView.findViewById(R.id.tvHistoryComment);
            viewColor = itemView.findViewById(R.id.viewHistoryColor);
        }
    }
}