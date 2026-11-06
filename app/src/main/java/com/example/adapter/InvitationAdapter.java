package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.model.Invitation;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {

    public interface OnInvitationActionListener {
        void onAccept(Invitation invitation);
        void onDecline(Invitation invitation);
        void onCancel(Invitation invitation);
    }

    private List<Invitation> invitations = new ArrayList<>();
    private final OnInvitationActionListener listener;

    public InvitationAdapter(OnInvitationActionListener listener) {
        this.listener = listener;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations != null ? invitations : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Invitation inv = invitations.get(position);
        holder.tvCommunityName.setText(inv.getCommunityName());
        holder.tvDetails.setText("De: " + inv.getSenderName() + " • " + inv.getDate());
        holder.tvStatus.setText(inv.getStatus());

        if (Invitation.STATUS_PENDING.equalsIgnoreCase(inv.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_orange);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.layoutActions.setVisibility(View.VISIBLE);

            if (inv.canCancel()) {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDecline.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.VISIBLE);
            } else {
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnDecline.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.GONE);
            }
        } else {
            holder.layoutActions.setVisibility(View.GONE);
            if (Invitation.STATUS_ACCEPTED.equalsIgnoreCase(inv.getStatus())) {
                holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success_container));
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success));
            } else {
                holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_error_container));
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_error));
            }
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(inv);
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (listener != null) listener.onDecline(inv);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(inv);
        });
    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommunityName, tvDetails, tvStatus;
        LinearLayout layoutActions;
        MaterialButton btnAccept, btnDecline, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommunityName = itemView.findViewById(R.id.tv_invite_community_name);
            tvDetails = itemView.findViewById(R.id.tv_invite_details);
            tvStatus = itemView.findViewById(R.id.tv_invite_status);
            layoutActions = itemView.findViewById(R.id.layout_invite_actions);
            btnAccept = itemView.findViewById(R.id.btn_invite_accept);
            btnDecline = itemView.findViewById(R.id.btn_invite_decline);
            btnCancel = itemView.findViewById(R.id.btn_invite_cancel);
        }
    }
}
