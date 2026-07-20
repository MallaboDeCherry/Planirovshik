package com.example.planinagod.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planinagod.R;
import com.example.planinagod.data.Plan;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Типы элементов
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PLAN = 1;
    private static final int TYPE_COMPLETED_HEADER = 2;
    private static final int TYPE_COMPLETED_PLAN = 3;

    private List<Plan> plans;
    private List<Plan> completedPlans = new ArrayList<>();
    private OnPlanClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public interface OnPlanClickListener {
        void onPlanClick(Plan plan);
        void onPlanLongClick(Plan plan);
        void onPlanArchiveClick(Plan plan);
    }

    public PlanAdapter(List<Plan> plans, OnPlanClickListener listener) {
        this.plans = plans;
        this.listener = listener;
        splitCompletedPlans();
    }

    private void splitCompletedPlans() {
        completedPlans.clear();
        for (int i = plans.size() - 1; i >= 0; i--) {
            Plan p = plans.get(i);
            if (p.isCompleted && !p.isArchived) {
                completedPlans.add(p);
                plans.remove(i);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (position <= plans.size()) {
            return TYPE_PLAN;
        }
        if (position == plans.size() + 1 && !completedPlans.isEmpty()) {
            return TYPE_COMPLETED_HEADER;
        }
        return TYPE_COMPLETED_PLAN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_shelf_header, parent, false);
            return new HeaderViewHolder(view);
        }

        if (viewType == TYPE_COMPLETED_HEADER) {
            View view = inflater.inflate(R.layout.item_completed_header, parent, false);
            return new CompletedHeaderViewHolder(view);
        }

        if (viewType == TYPE_COMPLETED_PLAN) {
            View view = inflater.inflate(R.layout.item_plan_completed, parent, false);
            return new CompletedPlanViewHolder(view);
        }

        View view = inflater.inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlanViewHolder) {
            int planIndex = position - 1;
            if (planIndex < plans.size()) {
                bindPlan((PlanViewHolder) holder, plans.get(planIndex));
            }
        } else if (holder instanceof CompletedPlanViewHolder) {
            int completedIndex = position - plans.size() - 2;
            if (completedIndex < completedPlans.size()) {
                bindCompletedPlan((CompletedPlanViewHolder) holder, completedPlans.get(completedIndex));
            }
        }
    }

    private void bindPlan(PlanViewHolder holder, Plan plan) {
        holder.tvCategory.setText(plan.category != null ? plan.category : "Без категории");
        holder.tvTitle.setText(plan.title);

        if (plan.hasNumericGoal) {
            String progressText = String.format(Locale.getDefault(), "%.1f из %.1f %s",
                    plan.currentValue, plan.targetValue, plan.unit);
            holder.tvProgressText.setText(progressText);

            int percent = plan.targetValue > 0 ? (int)(plan.currentValue / plan.targetValue * 100) : 0;
            if (percent > 100) percent = 100;
            holder.tvProgressPercent.setText(percent + "%");
            holder.circularProgress.setProgress(percent);
            holder.circularProgress.setVisibility(View.VISIBLE);
            holder.tvProgressPercent.setVisibility(View.VISIBLE);
        } else {
            if (plan.isCompleted) {
                holder.tvProgressText.setText("✅ Выполнено");
                holder.tvProgressPercent.setText("✅ 100%");
                holder.circularProgress.setProgress(100);
            } else {
                holder.tvProgressText.setText("⏳ Ожидает выполнения");
                holder.tvProgressPercent.setText("0%");
                holder.circularProgress.setProgress(0);
            }
            holder.circularProgress.setVisibility(View.VISIBLE);
            holder.tvProgressPercent.setVisibility(View.VISIBLE);
        }

        String deadlineText = "до " + dateFormat.format(plan.deadline);
        holder.tvDeadline.setText(deadlineText);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlanClick(plan);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onPlanArchiveClick(plan);
            return true;
        });
    }

    private void bindCompletedPlan(CompletedPlanViewHolder holder, Plan plan) {
        holder.tvCategory.setText(plan.category != null ? plan.category : "Без категории");
        holder.tvTitle.setText(plan.title);
        holder.tvProgressText.setText("✅ Выполнено");
        holder.tvDeadline.setText("до " + dateFormat.format(plan.deadline));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlanClick(plan);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onPlanArchiveClick(plan);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        int count = 1; // header
        count += plans.size();
        if (!completedPlans.isEmpty()) {
            count += 1; // completed header
            count += completedPlans.size();
        }
        return count;
    }

    public void updatePlans(List<Plan> newPlans) {
        this.plans = newPlans;
        splitCompletedPlans();
        notifyDataSetChanged();
    }

    // ===== VIEWHOLDERS =====

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class CompletedHeaderViewHolder extends RecyclerView.ViewHolder {
        public CompletedHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvCategory, tvTitle, tvProgressText, tvDeadline, tvProgressPercent;
        CircularProgressIndicator circularProgress;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvProgressText = itemView.findViewById(R.id.tvProgressText);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvProgressPercent = itemView.findViewById(R.id.tvProgressPercent);
            circularProgress = itemView.findViewById(R.id.circularProgress);
        }
    }

    static class CompletedPlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvProgressText, tvDeadline;

        public CompletedPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvProgressText = itemView.findViewById(R.id.tvProgressText);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
        }
    }
}