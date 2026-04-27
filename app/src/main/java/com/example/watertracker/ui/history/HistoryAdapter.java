package com.example.watertracker.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watertracker.IntakeRecord;
import com.example.watertracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(IntakeRecord record);
    }

    private final List<IntakeRecord> records;
    private final OnDeleteClickListener onDeleteClickListener;

    public HistoryAdapter(List<IntakeRecord> records, OnDeleteClickListener onDeleteClickListener) {
        this.records = records;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        IntakeRecord record = records.get(position);

        double consumedLiters = record.getWaterMl() / 1000.0;
        double goalLiters = record.getGoalMl() / 1000.0;
        boolean goalAchieved = record.getWaterMl() >= record.getGoalMl() && record.getGoalMl() > 0;

        holder.dateTextView.setText(formatDisplayDate(record.getDate()));
        holder.amountTextView.setText(String.format(
                Locale.getDefault(),
                "%.2f L / %.2f L",
                consumedLiters,
                goalLiters
        ));

        if (goalAchieved) {
            holder.statusTextView.setText("Goal achieved ✅");
            holder.statusTextView.setTextColor(holder.itemView.getContext().getColor(R.color.aqua_blue));
            holder.statusTextView.setBackgroundColor(android.graphics.Color.parseColor("#EEF7FF"));
        } else {
            holder.statusTextView.setText("Goal not achieved");
            holder.statusTextView.setTextColor(android.graphics.Color.parseColor("#D97706"));
            holder.statusTextView.setBackgroundColor(android.graphics.Color.parseColor("#FFF4E5"));
        }

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    private String formatDisplayDate(String rawDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(rawDate));
        } catch (ParseException | NullPointerException e) {
            return rawDate;
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView amountTextView;
        TextView statusTextView;
        ImageButton deleteButton;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            deleteButton = itemView.findViewById(R.id.deleteHistoryButton);
        }
    }
}