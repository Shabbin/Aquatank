package com.example.watertracker.ui.settings;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.watertracker.IntakeRecord;
import com.example.watertracker.R;
import com.example.watertracker.WaterIntakeDBHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private EditText editTextWeight;
    private TextView textViewWeight;
    private TextView liveGoalTextView;
    private TextView calculatedGoalTextView;
    private SettingsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        editTextWeight = root.findViewById(R.id.edit_text_weight);
        Button saveButton = root.findViewById(R.id.button_save);
        textViewWeight = root.findViewById(R.id.text_view_weight);
        liveGoalTextView = root.findViewById(R.id.liveGoalTextView);
        calculatedGoalTextView = root.findViewById(R.id.calculatedGoalTextView);
        Button exportButton = root.findViewById(R.id.export_button);

        SharedPreferences preferences =
                requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(SettingsViewModel.class);

        viewModel.setPreferences(preferences);

        viewModel.getStoredWeight().observe(getViewLifecycleOwner(), storedWeight -> {
            textViewWeight.setText("Stored weight: " + storedWeight + " kg");

            double goalLiters = storedWeight * 0.0325;
            calculatedGoalTextView.setText(
                    String.format(
                            Locale.getDefault(),
                            "Your Calculated Daily Goal: %.2f L",
                            goalLiters
                    )
            );
        });

        editTextWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();

                if (input.isEmpty()) {
                    liveGoalTextView.setText("Your Goal: 0.00 L");
                    return;
                }

                try {
                    int weight = Integer.parseInt(input);

                    if (weight <= 0) {
                        liveGoalTextView.setText("Your Goal: 0.00 L");
                        return;
                    }

                    double goalLiters = weight * 0.0325;
                    liveGoalTextView.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "Your Goal: %.2f L",
                                    goalLiters
                            )
                    );
                } catch (NumberFormatException e) {
                    liveGoalTextView.setText("Your Goal: 0.00 L");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        saveButton.setOnClickListener(v -> {
            String userInput = editTextWeight.getText().toString().trim();

            if (TextUtils.isEmpty(userInput)) {
                Toast.makeText(requireContext(), "Please enter your weight", Toast.LENGTH_SHORT).show();
                return;
            }

            int userWeight;
            try {
                userWeight = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid weight", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userWeight <= 0) {
                Toast.makeText(requireContext(), "Weight must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.saveWeight(userWeight);

            int newGoalMl = (int) Math.round(userWeight * 0.0325 * 1000);

            WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(requireContext());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String today = dateFormat.format(new Date());

            IntakeRecord existingTodayRecord = dbHelper.getIntakeRecordByDate(today);
            if (existingTodayRecord != null && existingTodayRecord.getWaterMl() > 0) {
                existingTodayRecord.setGoalMl(newGoalMl);
                dbHelper.updateIntakeRecord(existingTodayRecord);
            }

            dbHelper.close();

            editTextWeight.setText("");
            hideKeyboard();

            Toast.makeText(requireContext(), "Weight saved successfully", Toast.LENGTH_SHORT).show();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportButton.setOnClickListener(v -> exportDatabaseToCsv());
        } else {
            exportButton.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "CSV export requires Android 10 or above", Toast.LENGTH_SHORT).show()
            );
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void exportDatabaseToCsv() {
        WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(requireContext());
        List<IntakeRecord> records = dbHelper.getAllIntakeRecords();
        dbHelper.close();

        String fileName = "water_intake.csv";

        try {
            ContentResolver resolver = requireContext().getContentResolver();
            Uri downloadsDir = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");

            Uri fileUri = resolver.insert(downloadsDir, values);
            if (fileUri == null) {
                Toast.makeText(requireContext(), "Failed to create export file", Toast.LENGTH_SHORT).show();
                return;
            }

            OutputStream os = resolver.openOutputStream(fileUri);
            if (os == null) {
                Toast.makeText(requireContext(), "Failed to open export file", Toast.LENGTH_SHORT).show();
                return;
            }

            String headers = "Date,Consumed (L),Goal (L),Goal Achieved\n";
            os.write(headers.getBytes());

            for (IntakeRecord record : records) {
                double consumedL = record.getWaterMl() / 1000.0;
                double goalL = record.getGoalMl() / 1000.0;
                boolean achieved = record.getWaterMl() >= record.getGoalMl() && record.getGoalMl() > 0;

                String row = record.getDate() + "," +
                        String.format(Locale.getDefault(), "%.2f", consumedL) + "," +
                        String.format(Locale.getDefault(), "%.2f", goalL) + "," +
                        (achieved ? "Yes" : "No") +
                        "\n";

                os.write(row.getBytes());
            }

            os.flush();
            os.close();

            Toast.makeText(requireContext(), "Data exported successfully", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to export data", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        if (getContext() == null || editTextWeight == null) {
            return;
        }

        InputMethodManager imm =
                (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(editTextWeight.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editTextWeight = null;
        textViewWeight = null;
        liveGoalTextView = null;
        calculatedGoalTextView = null;
    }
}