package com.example.watertracker.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watertracker.IntakeRecord;
import com.example.watertracker.R;
import com.example.watertracker.WaterIntakeDBHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView historyRecyclerView;
    private LinearLayout emptyStateLayout;
    private HistoryAdapter historyAdapter;
    private final List<IntakeRecord> recordList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = root.findViewById(R.id.historyRecyclerView);
        emptyStateLayout = root.findViewById(R.id.emptyStateLayout);

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyAdapter = new HistoryAdapter(recordList, this::showDeleteConfirmation);
        historyRecyclerView.setAdapter(historyAdapter);

        loadHistory();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(requireContext());
        List<IntakeRecord> records = dbHelper.getAllIntakeRecords();
        dbHelper.close();

        recordList.clear();
        if (records != null) {
            recordList.addAll(records);
        }

        historyAdapter.notifyDataSetChanged();

        if (recordList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmation(IntakeRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete History")
                .setMessage("Delete all water history for " + record.getDate() + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(requireContext());
                    dbHelper.deleteRecordByDate(record.getDate());
                    dbHelper.close();

                    loadHistory();
                })
                .show();
    }
}