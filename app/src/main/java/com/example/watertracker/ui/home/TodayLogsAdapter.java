package com.example.watertracker.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watertracker.R;
import com.example.watertracker.WaterLog;

import java.util.ArrayList;
import java.util.List;

public class TodayLogsAdapter extends RecyclerView.Adapter<TodayLogsAdapter.LogViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(WaterLog waterLog);
    }

    private final List<WaterLog> waterLogs = new ArrayList<>();
    private final OnDeleteClickListener onDeleteClickListener;

    public TodayLogsAdapter(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void submitList(List<WaterLog> logs) {
        waterLogs.clear();
        if (logs != null) {
            waterLogs.addAll(logs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_today_sip, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        WaterLog waterLog = waterLogs.get(position);
        holder.timeTextView.setText(waterLog.getTime());
        holder.amountTextView.setText(waterLog.getAmountMl() + " ml");

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(waterLog);
            }
        });
    }

    @Override
    public int getItemCount() {
        return waterLogs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView amountTextView;
        ImageButton deleteButton;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            deleteButton = itemView.findViewById(R.id.deleteLogButton);
        }
    }
}