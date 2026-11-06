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
import com.example.model.Community;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    public interface OnCommunityActionListener {
        void onToggleJoin(Community community);
        void onCommunityClick(Community community);
        void onDeleteCommunity(Community community);
    }

    private List<Community> communities = new ArrayList<>();
    private final OnCommunityActionListener listener;

    public CommunityAdapter(OnCommunityActionListener listener) {
        this.listener = listener;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities != null ? communities : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Community comm = communities.get(position);
        holder.tvName.setText(comm.getName());
        holder.tvDesc.setText(comm.getDescription());
        holder.tvCreator.setText("Criador / Admin: " + comm.getCreatorName());
        holder.tvMembers.setText("👥 " + comm.getMemberCount() + " membros");
        holder.tvStatus.setText(comm.getStatus() != null ? comm.getStatus() : "ATIVO");

        if (comm.isMember()) {
            holder.btnAction.setText("Membro ✓");
            holder.btnAction.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success_container));
            holder.btnAction.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success));
        } else {
            holder.btnAction.setText("Entrar");
            holder.btnAction.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_container));
            holder.btnAction.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_dark));
        }

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleJoin(comm);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteCommunity(comm);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommunityClick(comm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvCreator, tvMembers, tvStatus;
        MaterialButton btnAction;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_community_name);
            tvDesc = itemView.findViewById(R.id.tv_community_desc);
            tvCreator = itemView.findViewById(R.id.tv_community_creator);
            tvMembers = itemView.findViewById(R.id.tv_community_members);
            tvStatus = itemView.findViewById(R.id.tv_community_status);
            btnAction = itemView.findViewById(R.id.btn_community_action);
            btnDelete = itemView.findViewById(R.id.btn_community_delete);
        }
    }
}
