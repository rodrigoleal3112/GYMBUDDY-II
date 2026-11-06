package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.model.Badge;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {

    private List<Badge> badges = new ArrayList<>();

    public void setBadges(List<Badge> badges) {
        this.badges = badges != null ? badges : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Badge b = badges.get(position);
        holder.tvName.setText(b.getName());
        holder.tvDesc.setText(b.getDescription());
        holder.tvXp.setText("+" + b.getXpReward() + " XP");

        if (b.isUnlocked()) {
            holder.tvStatus.setText("CONQUISTADO");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_orange);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.cardIconBg.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_container));
            holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_dark));
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.tvStatus.setText("BLOQUEADO");
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.surface_variant_light));
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary_light));
            holder.cardIconBg.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.surface_variant_light));
            holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary_light));
            holder.itemView.setAlpha(0.6f);
        }
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvXp, tvStatus;
        MaterialCardView cardIconBg;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_badge_name);
            tvDesc = itemView.findViewById(R.id.tv_badge_desc);
            tvXp = itemView.findViewById(R.id.tv_badge_xp);
            tvStatus = itemView.findViewById(R.id.tv_badge_status);
            cardIconBg = itemView.findViewById(R.id.card_badge_icon_bg);
            ivIcon = itemView.findViewById(R.id.iv_badge_icon);
        }
    }
}
