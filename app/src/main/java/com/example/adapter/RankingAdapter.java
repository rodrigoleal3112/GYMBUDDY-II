package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.model.RankingUser;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<RankingUser> users = new ArrayList<>();

    public void setUsers(List<RankingUser> users) {
        this.users = users != null ? users : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RankingUser u = users.get(position);
        holder.tvPos.setText(String.valueOf(u.getPosition()));
        holder.tvName.setText(u.getName());
        holder.tvDetails.setText("Nível " + u.getLevel() + " • 🔥 " + u.getStreakDays() + " dias de sequência");
        holder.tvXp.setText(u.getXp() + " XP");

        // Destaque para as primeiras posições e para o próprio usuário
        if (u.getPosition() == 1) {
            holder.tvPos.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gold));
            holder.tvPos.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary_light));
        } else if (u.getPosition() == 2) {
            holder.tvPos.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.silver));
            holder.tvPos.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary_light));
        } else if (u.getPosition() == 3) {
            holder.tvPos.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.bronze));
            holder.tvPos.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.tvPos.setBackgroundResource(R.drawable.bg_pill_orange);
            holder.tvPos.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }

        if (u.isCurrentUser()) {
            holder.tvYouBadge.setVisibility(View.VISIBLE);
            holder.cardContainer.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange));
            holder.cardContainer.setStrokeWidth(3);
            holder.cardContainer.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_container));
        } else {
            holder.tvYouBadge.setVisibility(View.GONE);
            holder.cardContainer.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.divider_light));
            holder.cardContainer.setStrokeWidth(1);
            holder.cardContainer.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.surface_light));
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(10, users.size()); // Até o décimo colocado conforme solicitado
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPos, tvName, tvYouBadge, tvDetails, tvXp;
        MaterialCardView cardContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPos = itemView.findViewById(R.id.tv_ranking_pos);
            tvName = itemView.findViewById(R.id.tv_ranking_name);
            tvYouBadge = itemView.findViewById(R.id.tv_ranking_you_badge);
            tvDetails = itemView.findViewById(R.id.tv_ranking_details);
            tvXp = itemView.findViewById(R.id.tv_ranking_xp);
            cardContainer = itemView.findViewById(R.id.card_ranking_item);
        }
    }
}
