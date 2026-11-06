package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.model.ActivityModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    public interface OnActivityClickListener {
        void onActivitySelected(ActivityModel activity);
        void onActivityDelete(ActivityModel activity);
    }

    private List<ActivityModel> activities = new ArrayList<>();
    private Set<String> completedActivityIds = new HashSet<>();
    private final OnActivityClickListener listener;

    public ActivityAdapter(OnActivityClickListener listener) {
        this.listener = listener;
    }

    public void setActivities(List<ActivityModel> newActivities) {
        this.activities = newActivities != null ? newActivities : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCompletedActivityIds(Set<String> completedIds) {
        this.completedActivityIds = completedIds != null ? completedIds : new HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityModel act = activities.get(position);
        holder.tvName.setText(act.getName());
        holder.tvDesc.setText(act.getDescription());
        holder.tvCategory.setText(act.getCategory());
        holder.tvXp.setText("+" + act.getXpReward() + " XP");
        holder.tvFrequency.setText("Frequência: " + act.getTargetFrequency());
        holder.tvSeriesReps.setText("💪 " + act.getSeriesAndRepsFormatted());

        boolean isCompleted = completedActivityIds.contains(act.getId()) || completedActivityIds.contains(act.getName());
        if (isCompleted) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("✓ Concluído");
            holder.btnCheckin.setText("Dar OK");
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.btnCheckin.setText("Dar OK");
        }

        View.OnClickListener clickListener = v -> {
            if (listener != null) {
                listener.onActivitySelected(act);
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.btnCheckin.setOnClickListener(clickListener);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActivityDelete(act);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvStatus, tvXp, tvName, tvDesc, tvFrequency, tvSeriesReps;
        MaterialButton btnCheckin, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_activity_category);
            tvStatus = itemView.findViewById(R.id.tv_activity_status);
            tvXp = itemView.findViewById(R.id.tv_activity_xp);
            tvName = itemView.findViewById(R.id.tv_activity_name);
            tvDesc = itemView.findViewById(R.id.tv_activity_desc);
            tvFrequency = itemView.findViewById(R.id.tv_activity_frequency);
            tvSeriesReps = itemView.findViewById(R.id.tv_activity_series_reps);
            btnCheckin = itemView.findViewById(R.id.btn_activity_checkin);
            btnDelete = itemView.findViewById(R.id.btn_activity_delete);
        }
    }
}
