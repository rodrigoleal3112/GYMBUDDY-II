package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.model.CommunityActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivityAdapter extends RecyclerView.Adapter<CommunityActivityAdapter.ViewHolder> {

    public interface OnCommunityActivityListener {
        void onToggleParticipate(CommunityActivity activity);
        void onCheckInClick(CommunityActivity activity);
    }

    private List<CommunityActivity> activities = new ArrayList<>();
    private final OnCommunityActivityListener listener;

    public CommunityActivityAdapter(OnCommunityActivityListener listener) {
        this.listener = listener;
    }

    public void setActivities(List<CommunityActivity> activities) {
        this.activities = activities != null ? activities : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityActivity act = activities.get(position);
        holder.tvTitle.setText(act.getTitle());
        holder.tvDesc.setText(act.getDescription());
        holder.tvCategory.setText(act.getCategory() != null ? act.getCategory() : "CARDIO");
        holder.tvXp.setText("+" + act.getXpReward() + " XP");
        holder.tvSchedule.setText("🕒 " + (act.getSchedule() != null ? act.getSchedule() : "Diário"));
        holder.tvParticipants.setText("👥 " + act.getParticipantsCount() + " participantes");

        if (act.isParticipating()) {
            holder.btnJoin.setText("Participando ✓");
            holder.btnJoin.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success_container));
            holder.btnJoin.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success));
        } else {
            holder.btnJoin.setText("Participar");
            holder.btnJoin.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_container));
            holder.btnJoin.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_dark));
        }

        holder.btnJoin.setOnClickListener(v -> {
            if (listener != null) listener.onToggleParticipate(act);
        });

        holder.btnCheckin.setOnClickListener(v -> {
            if (listener != null) listener.onCheckInClick(act);
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvCategory, tvXp, tvSchedule, tvParticipants;
        MaterialButton btnJoin, btnCheckin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_comm_act_title);
            tvDesc = itemView.findViewById(R.id.tv_comm_act_desc);
            tvCategory = itemView.findViewById(R.id.tv_comm_act_category);
            tvXp = itemView.findViewById(R.id.tv_comm_act_xp);
            tvSchedule = itemView.findViewById(R.id.tv_comm_act_schedule);
            tvParticipants = itemView.findViewById(R.id.tv_comm_act_participants);
            btnJoin = itemView.findViewById(R.id.btn_comm_act_join);
            btnCheckin = itemView.findViewById(R.id.btn_comm_act_checkin);
        }
    }
}
