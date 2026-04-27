package com.example.watertracker.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watertracker.CustomViewModelFactory;
import com.example.watertracker.WaterLog;
import com.example.watertracker.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

public class HomeFragment extends Fragment {

    private boolean goalPopupShown = false;

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private TodayLogsAdapter todayLogsAdapter;

    private TextView waterIntakeTextView;
    private TextView progressPercentTextView;
    private TextView progressSubTextView;
    private TextView speechBubbleTextView;
    private CircularProgressIndicator waterProgressBar;

    private MaterialButton addButton;
    private MaterialButton btn250;
    private MaterialButton btn500;
    private MaterialButton btn750;
    private MaterialButton deleteTodayButton;

    private TextView emptyLogsTextView;
    private RecyclerView todayLogsRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        initViews();
        initRecyclerView();
        initViewModel();
        observeViewModel();
        setupClickListeners();
        refreshHomeUi();

        return rootView;
    }

    private void initViews() {
        waterIntakeTextView = binding.waterIntakeTextView;
        progressPercentTextView = binding.progressPercentTextView;
        progressSubTextView = binding.progressSubTextView;
        speechBubbleTextView = binding.speechBubbleTextView;
        waterProgressBar = binding.waterProgressBar;

        addButton = binding.addButton;
        btn250 = binding.btn250;
        btn500 = binding.btn500;
        btn750 = binding.btn750;
        deleteTodayButton = binding.deleteTodayButton;

        emptyLogsTextView = binding.emptyLogsTextView;
        todayLogsRecyclerView = binding.todayLogsRecyclerView;
    }

    private void initRecyclerView() {
        todayLogsAdapter = new TodayLogsAdapter(this::showDeleteSingleLogConfirmation);
        todayLogsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        todayLogsRecyclerView.setAdapter(todayLogsAdapter);
    }

    private void initViewModel() {
        homeViewModel = new ViewModelProvider(
                this,
                new CustomViewModelFactory(getActivity())
        ).get(HomeViewModel.class);
    }

    private void observeViewModel() {
        homeViewModel.getRecommendedIntake().observe(getViewLifecycleOwner(), recommendedIntake -> refreshHomeUi());

        homeViewModel.getWaterIntake().observe(getViewLifecycleOwner(), waterIntake -> refreshHomeUi());

        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                waterIntakeTextView.setText(text);
            }
            updateProgressSafely();
            checkGoalAchievement();
        });

        homeViewModel.getTodayLogs().observe(getViewLifecycleOwner(), this::updateTodayLogsUi);
    }

    private void setupClickListeners() {
        addButton.setOnClickListener(view -> {
            if (!homeViewModel.hasValidGoal()) {
                Toast.makeText(getContext(), "Please set your weight first in Settings", Toast.LENGTH_SHORT).show();
                return;
            }

            String input = binding.mlToAddEditText.getText().toString().trim();

            if (TextUtils.isEmpty(input)) {
                Toast.makeText(getContext(), "Please enter water amount", Toast.LENGTH_SHORT).show();
                return;
            }

            int mlToAdd;
            try {
                mlToAdd = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mlToAdd <= 0) {
                Toast.makeText(getContext(), "Enter a value greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            addWaterAndRefresh(mlToAdd, view);
        });

        btn250.setOnClickListener(view -> {
            if (!homeViewModel.hasValidGoal()) {
                Toast.makeText(getContext(), "Please set your weight first in Settings", Toast.LENGTH_SHORT).show();
                return;
            }
            addWaterAndRefresh(250, view);
        });

        btn500.setOnClickListener(view -> {
            if (!homeViewModel.hasValidGoal()) {
                Toast.makeText(getContext(), "Please set your weight first in Settings", Toast.LENGTH_SHORT).show();
                return;
            }
            addWaterAndRefresh(500, view);
        });

        btn750.setOnClickListener(view -> {
            if (!homeViewModel.hasValidGoal()) {
                Toast.makeText(getContext(), "Please set your weight first in Settings", Toast.LENGTH_SHORT).show();
                return;
            }
            addWaterAndRefresh(750, view);
        });

        deleteTodayButton.setOnClickListener(view -> showDeleteTodayConfirmation());
    }

    private void addWaterAndRefresh(int mlToAdd, View sourceView) {
        if (homeViewModel == null) {
            return;
        }

        homeViewModel.addWaterIntake(mlToAdd, requireContext());
        binding.mlToAddEditText.getText().clear();
        hideKeyboard(sourceView);

        Integer waterIntake = homeViewModel.getWaterIntake().getValue();
        Integer goal = homeViewModel.getRecommendedIntake().getValue();

        if (waterIntake != null && goal != null && goal > 0) {
            int progress = (waterIntake * 100) / goal;

            if (progress >= 100) {
                speechBubbleTextView.setText("You're already hydrated! 🎉");
            } else {
                speechBubbleTextView.setText("Nice! Keep going 💧");
            }
        } else {
            speechBubbleTextView.setText("Nice! Keep going 💧");
        }

        binding.speechBubbleCard.animate().alpha(1f).setDuration(180).start();
    }

    private void refreshHomeUi() {
        if (homeViewModel == null) {
            return;
        }

        String displayText = homeViewModel.getText().getValue();
        if (displayText != null) {
            waterIntakeTextView.setText(displayText);
        }

        updateProgressSafely();
        checkGoalAchievement();
    }

    private void updateProgressSafely() {
        if (homeViewModel == null || waterProgressBar == null) {
            return;
        }

        Integer waterIntake = homeViewModel.getWaterIntake().getValue();
        Integer recommendedIntake = homeViewModel.getRecommendedIntake().getValue();

        if (waterIntake == null) {
            waterIntake = 0;
        }

        if (recommendedIntake == null || recommendedIntake <= 0) {
            waterProgressBar.setProgress(0);
            progressPercentTextView.setText("0%");
            progressSubTextView.setText("completed");
            speechBubbleTextView.setText("Set your weight in Settings first");
            return;
        }

        int progress = Math.min((waterIntake * 100) / recommendedIntake, 100);
        waterProgressBar.setProgress(progress);
        progressPercentTextView.setText(progress + "%");
        progressSubTextView.setText("completed");
        updateMotivationBubble(progress, waterIntake, recommendedIntake);
    }

    private void updateMotivationBubble(int progress, int waterIntake, int recommendedIntake) {
        if (recommendedIntake <= 0) {
            speechBubbleTextView.setText("Set your weight in Settings first");
            return;
        }

        if (waterIntake <= 0) {
            speechBubbleTextView.setText("Let's drink some water!");
        } else if (progress < 25) {
            speechBubbleTextView.setText("Good start. Keep going!");
        } else if (progress < 60) {
            speechBubbleTextView.setText("Nice progress. Stay steady!");
        } else if (progress < 100) {
            speechBubbleTextView.setText("Almost there. You got this!");
        } else {
            speechBubbleTextView.setText("You're already hydrated! 🎉");
        }
    }

    private void checkGoalAchievement() {
        if (homeViewModel == null) {
            return;
        }

        Integer waterIntake = homeViewModel.getWaterIntake().getValue();
        Integer recommendedIntake = homeViewModel.getRecommendedIntake().getValue();

        if (waterIntake == null || recommendedIntake == null || recommendedIntake <= 0) {
            return;
        }

        if (waterIntake >= recommendedIntake && !goalPopupShown) {
            goalPopupShown = true;

            new AlertDialog.Builder(requireContext())
                    .setTitle("Goal Achieved")
                    .setMessage("Congratulations! You have reached your daily water goal.")
                    .setPositiveButton("OK", null)
                    .show();
        }

        if (waterIntake < recommendedIntake) {
            goalPopupShown = false;
        }
    }

    private void updateTodayLogsUi(List<WaterLog> logs) {
        if (logs != null && !logs.isEmpty()) {
            emptyLogsTextView.setVisibility(View.GONE);
            todayLogsRecyclerView.setVisibility(View.VISIBLE);
            todayLogsAdapter.submitList(logs);
        } else {
            emptyLogsTextView.setVisibility(View.VISIBLE);
            todayLogsRecyclerView.setVisibility(View.GONE);
            todayLogsAdapter.submitList(null);
        }
    }

    private void showDeleteTodayConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Today's Logs")
                .setMessage("Are you sure you want to delete all of today's water logs?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    homeViewModel.deleteTodayIntake(requireContext());
                    Toast.makeText(requireContext(), "Today's logs deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showDeleteSingleLogConfirmation(WaterLog waterLog) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete This Sip")
                .setMessage("Delete " + waterLog.getAmountMl() + " ml log at " + waterLog.getTime() + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    homeViewModel.deleteSingleLog(waterLog.getId(), requireContext());
                    Toast.makeText(requireContext(), "Sip deleted.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void hideKeyboard(View sourceView) {
        InputMethodManager imm =
                (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && sourceView != null) {
            imm.hideSoftInputFromWindow(sourceView.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (homeViewModel != null) {
            homeViewModel.refreshData(requireContext());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}