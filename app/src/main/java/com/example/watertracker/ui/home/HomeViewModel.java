package com.example.watertracker.ui.home;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.watertracker.IntakeRecord;
import com.example.watertracker.WaterIntakeDBHelper;
import com.example.watertracker.WaterLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Integer> mWaterIntake;
    private final MutableLiveData<Integer> mRecommendedIntake;
    private final MutableLiveData<List<WaterLog>> todayLogs;

    public HomeViewModel(Context context) {
        super();

        mText = new MutableLiveData<>("");
        mWaterIntake = new MutableLiveData<>(0);
        mRecommendedIntake = new MutableLiveData<>(0);
        todayLogs = new MutableLiveData<>();

        refreshData(context);
    }

    public void refreshData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int weightKg = preferences.getInt("Weight", 0);

        int recommendedIntakeMl = (int) Math.round(weightKg * 0.0325 * 1000);
        mRecommendedIntake.setValue(recommendedIntakeMl);

        String today = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());

        WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(context);

        // ✅ Load ONLY today's logs
        List<WaterLog> logs = dbHelper.getWaterLogsByDate(today);
        todayLogs.setValue(logs);

        int todayIntakeMl = 0;

        if (logs != null) {
            for (WaterLog log : logs) {
                todayIntakeMl += log.getAmountMl();
            }
        }

        // ✅ Only update summary if there is intake
        if (todayIntakeMl > 0) {
            dbHelper.updateDailySummary(today, recommendedIntakeMl);
        }

        mWaterIntake.setValue(todayIntakeMl);
        mText.setValue(buildDisplayText(todayIntakeMl, recommendedIntakeMl));

        dbHelper.close();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Integer> getWaterIntake() {
        return mWaterIntake;
    }

    public LiveData<Integer> getRecommendedIntake() {
        return mRecommendedIntake;
    }

    public LiveData<List<WaterLog>> getTodayLogs() {
        return todayLogs;
    }

    public boolean hasValidGoal() {
        Integer goal = mRecommendedIntake.getValue();
        return goal != null && goal > 0;
    }

    public void addWaterIntake(int mlToAdd, Context context) {
        if (mlToAdd <= 0) {
            return;
        }

        String date = getTodayDate();
        String time = getCurrentTime();

        Integer currentGoalMl = mRecommendedIntake.getValue();
        if (currentGoalMl == null) {
            currentGoalMl = 0;
        }

        WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(context);

        WaterLog waterLog = new WaterLog(date, time, mlToAdd);
        dbHelper.insertWaterLog(waterLog);
        dbHelper.updateDailySummary(date, currentGoalMl);

        IntakeRecord updatedRecord = dbHelper.getIntakeRecordByDate(date);
        int updatedIntakeMl = updatedRecord != null ? updatedRecord.getWaterMl() : 0;

        mWaterIntake.setValue(updatedIntakeMl);
        mText.setValue(buildDisplayText(updatedIntakeMl, currentGoalMl));
        todayLogs.setValue(dbHelper.getWaterLogsByDate(date));

        dbHelper.close();
    }

    public void deleteTodayIntake(Context context) {
        String today = getTodayDate();

        WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(context);
        dbHelper.deleteRecordByDate(today);

        Integer currentGoalMl = mRecommendedIntake.getValue();
        if (currentGoalMl == null) {
            currentGoalMl = 0;
        }

        mWaterIntake.setValue(0);
        mText.setValue(buildDisplayText(0, currentGoalMl));
        todayLogs.setValue(dbHelper.getWaterLogsByDate(today));

        dbHelper.close();
    }

    public void deleteSingleLog(int logId, Context context) {
        String today = getTodayDate();

        Integer currentGoalMl = mRecommendedIntake.getValue();
        if (currentGoalMl == null) {
            currentGoalMl = 0;
        }

        WaterIntakeDBHelper dbHelper = new WaterIntakeDBHelper(context);
        dbHelper.deleteWaterLogById(logId);
        dbHelper.updateDailySummary(today, currentGoalMl);

        IntakeRecord updatedRecord = dbHelper.getIntakeRecordByDate(today);
        int updatedIntakeMl = updatedRecord != null ? updatedRecord.getWaterMl() : 0;

        mWaterIntake.setValue(updatedIntakeMl);
        mText.setValue(buildDisplayText(updatedIntakeMl, currentGoalMl));
        todayLogs.setValue(dbHelper.getWaterLogsByDate(today));

        dbHelper.close();
    }

    private String getTodayDate() {
        return new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    private String buildDisplayText(int intakeMl, int goalMl) {
        double intakeL = intakeMl / 1000.0;
        double goalL = goalMl / 1000.0;

        return String.format(
                Locale.getDefault(),
                "%.2f L / %.2f L",
                intakeL,
                goalL
        );
    }
}