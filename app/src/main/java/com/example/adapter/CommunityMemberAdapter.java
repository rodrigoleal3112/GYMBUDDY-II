package com.example.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.model.CommunityMember;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CommunityMemberAdapter extends RecyclerView.Adapter<CommunityMemberAdapter.ViewHolder> {

    public interface OnMemberActionListener {
        void onCheckInMember(CommunityMember member);
    }

    private List<CommunityMember> members = new ArrayList<>();
    private boolean isAdmin = false;
    private OnMemberActionListener listener;

    public CommunityMemberAdapter() {}

    public CommunityMemberAdapter(boolean isAdmin, OnMemberActionListener listener) {
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
    }

    public void setOnMemberActionListener(OnMemberActionListener listener) {
        this.listener = listener;
    }

    public void setMembers(List<CommunityMember> members) {
        this.members = members != null ? members : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityMember member = members.get(position);
        holder.tvName.setText(member.getUserName());
        holder.tvRole.setText(member.getUserRole() != null ? member.getUserRole() : "Aluno");
        holder.tvLevel.setText("Nível " + member.getLevel());
        holder.tvStreak.setText("🔥 " + member.getStreakDays() + " dias");
        holder.tvYou.setVisibility(member.isCurrentUser() ? View.VISIBLE : View.GONE);

        if (!TextUtils.isEmpty(member.getStudentContact())) {
            holder.tvContact.setVisibility(View.VISIBLE);
            holder.tvContact.setText("✉ " + member.getStudentContact());
        } else {
            holder.tvContact.setVisibility(View.GONE);
        }

        String initial = "?";
        if (member.getUserName() != null && !member.getUserName().trim().isEmpty()) {
            initial = member.getUserName().trim().substring(0, 1).toUpperCase();
        }
        holder.tvInitial.setText(initial);

        // Ações de Administrador (Fazer Check no Aluno)
        if (isAdmin && !member.isCurrentUser()) {
            holder.layoutAdminAction.setVisibility(View.VISIBLE);
            if (member.isCheckedToday()) {
                holder.tvCheckStatus.setText("Presença confirmada ✓");
                holder.tvCheckStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success));
                holder.btnCheckIn.setText("Check Feito ✓");
                holder.btnCheckIn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success_container));
                holder.btnCheckIn.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_success));
            } else {
                holder.tvCheckStatus.setText("Pendente hoje");
                holder.tvCheckStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary_light));
                holder.btnCheckIn.setText("✅ Fazer Check");
                holder.btnCheckIn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_container));
                holder.btnCheckIn.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_orange_dark));
            }

            holder.btnCheckIn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCheckInMember(member);
                }
            });
        } else {
            holder.layoutAdminAction.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvYou, tvRole, tvLevel, tvStreak, tvContact, tvCheckStatus;
        View layoutAdminAction;
        MaterialButton btnCheckIn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tv_comm_member_initial);
            tvName = itemView.findViewById(R.id.tv_comm_member_name);
            tvYou = itemView.findViewById(R.id.tv_comm_member_you);
            tvRole = itemView.findViewById(R.id.tv_comm_member_role);
            tvLevel = itemView.findViewById(R.id.tv_comm_member_level);
            tvStreak = itemView.findViewById(R.id.tv_comm_member_streak);
            tvContact = itemView.findViewById(R.id.tv_comm_member_contact);
            tvCheckStatus = itemView.findViewById(R.id.tv_member_check_status);
            layoutAdminAction = itemView.findViewById(R.id.layout_member_admin_action);
            btnCheckIn = itemView.findViewById(R.id.btn_comm_member_checkin);
        }
    }
}
