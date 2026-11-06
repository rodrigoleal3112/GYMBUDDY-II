package com.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.model.CheckIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckInHistoryAdapter extends RecyclerView.Adapter<CheckInHistoryAdapter.ViewHolder> {

    private List<CheckIn> checkIns = new ArrayList<>();

    public void setCheckIns(List<CheckIn> checkIns) {
        this.checkIns = checkIns != null ? checkIns : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckIn c = checkIns.get(position);
        holder.tvActivity.setText(c.getActivityName());
        holder.tvXp.setText("+" + c.getXpEarned() + " XP");
        holder.tvDateTime.setText(c.getTimestamp());

        String locName = c.getLocationName() != null && !c.getLocationName().isEmpty() 
                ? c.getLocationName() 
                : "Local Adequado de Treino";
        holder.tvLocation.setText(locName);

        String coords = String.format(Locale.getDefault(), "Lat: %.4f • Long: %.4f (±%.0fm)", 
                c.getLatitude(), c.getLongitude(), c.getAccuracyMeters());
        holder.tvCoords.setText(coords);

        holder.tvBadge.setText("VERIFICADO • GPS");
    }

    @Override
    public int getItemCount() {
        return checkIns.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivity, tvXp, tvDateTime, tvLocation, tvCoords, tvBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivity = itemView.findViewById(R.id.tv_checkin_activity);
            tvXp = itemView.findViewById(R.id.tv_checkin_xp);
            tvDateTime = itemView.findViewById(R.id.tv_checkin_datetime);
            tvLocation = itemView.findViewById(R.id.tv_checkin_location);
            tvCoords = itemView.findViewById(R.id.tv_checkin_coords);
            tvBadge = itemView.findViewById(R.id.tv_checkin_verified_badge);
        }
    }
}
