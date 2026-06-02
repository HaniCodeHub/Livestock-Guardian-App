package com.example.livestockguardian.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livestockguardian.R;
import com.example.livestockguardian.models.Livestock;

import java.util.ArrayList;
import java.util.List;

public class LivestockAdapter extends RecyclerView.Adapter<LivestockAdapter.LivestockViewHolder> {

    private List<Livestock> livestockList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Livestock livestock);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLivestockList(List<Livestock> livestockList) {
        this.livestockList = livestockList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LivestockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livestock, parent, false);
        return new LivestockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LivestockViewHolder holder, int position) {
        Livestock livestock = livestockList.get(position);
        
        String name = livestock.getName() != null ? livestock.getName() : "Unknown";
        holder.tvTagNumber.setText("Tag #" + (name.length() > 4 ? name.substring(0, 4) : name));
        holder.tvBreedAndSpecies.setText(livestock.getBreed() + " • " + livestock.getSpecies());

        String status = livestock.getStatus() != null ? livestock.getStatus() : "active";
        holder.tvStatus.setText(capitalizeStatus(status));

        int statusColor;
        int statusBg;
        int dotColor;
        boolean showTopBorder = false;

        if (isHealthyStatus(status)) {
            statusColor = holder.itemView.getContext().getColor(R.color.brand_green);
            statusBg = holder.itemView.getContext().getColor(R.color.trend_pill_bg);
            dotColor = holder.itemView.getContext().getColor(R.color.system_green);
            holder.ivActionIcon.setImageResource(android.R.drawable.ic_menu_view);
            holder.tvActionText.setText("View Profile");
        } else if (isStolenStatus(status)) {
            statusColor = holder.itemView.getContext().getColor(R.color.alert_red_text);
            statusBg = holder.itemView.getContext().getColor(R.color.alert_red_bg);
            dotColor = holder.itemView.getContext().getColor(R.color.alert_red_text);
            holder.tvStatus.setText("Reported Stolen");
            showTopBorder = true;
            holder.viewStatusIndicator.setBackgroundColor(statusColor);
            holder.ivActionIcon.setImageResource(android.R.drawable.ic_menu_mylocation);
            holder.tvActionText.setText("Track Asset");
        } else {
            // Under Observation
            statusColor = holder.itemView.getContext().getColor(R.color.alert_yellow_text);
            statusBg = holder.itemView.getContext().getColor(R.color.alert_yellow_bg);
            dotColor = holder.itemView.getContext().getColor(R.color.alert_yellow_text);
            showTopBorder = true;
            holder.viewStatusIndicator.setBackgroundColor(statusColor);
            holder.ivActionIcon.setImageResource(android.R.drawable.ic_menu_view);
            holder.tvActionText.setText("View Profile");
        }

        if (holder.layoutStatus.getBackground() != null) {
            holder.layoutStatus.getBackground().setTint(statusBg);
        }
        if (holder.viewStatusDot.getBackground() != null) {
            holder.viewStatusDot.getBackground().setTint(dotColor);
        }
        holder.tvStatus.setTextColor(statusColor);
        
        // Fix: ImageView doesn't have setTint(int). Use setImageTintList instead.
        // Also apply it consistently to handle ViewHolder reuse.
        holder.ivActionIcon.setImageTintList(ColorStateList.valueOf(statusColor));
        holder.tvActionText.setTextColor(statusColor);

        holder.viewStatusIndicator.setVisibility(showTopBorder ? View.VISIBLE : View.GONE);

        holder.btnViewProfile.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(livestock);
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(livestock);
        });
    }

    @Override
    public int getItemCount() {
        return livestockList.size();
    }

    private static boolean isHealthyStatus(String status) {
        return "healthy".equalsIgnoreCase(status);
    }

    private static boolean isStolenStatus(String status) {
        return "stolen".equalsIgnoreCase(status) || "reported stolen".equalsIgnoreCase(status);
    }

    private static String capitalizeStatus(String status) {
        if (status == null || status.isEmpty()) {
            return "Active";
        }
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    static class LivestockViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagNumber, tvBreedAndSpecies, tvStatus, tvActionText;
        View viewStatusDot, layoutStatus, btnViewProfile, viewStatusIndicator;
        ImageView ivAnimal, ivActionIcon;

        public LivestockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagNumber = itemView.findViewById(R.id.tvTagNumber);
            tvBreedAndSpecies = itemView.findViewById(R.id.tvBreedAndSpecies);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            viewStatusDot = itemView.findViewById(R.id.viewStatusDot);
            layoutStatus = itemView.findViewById(R.id.layoutStatus);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfileItem);
            ivAnimal = itemView.findViewById(R.id.ivAnimal);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            ivActionIcon = itemView.findViewById(R.id.ivActionIcon);
            tvActionText = itemView.findViewById(R.id.tvActionText);
        }
    }
}
