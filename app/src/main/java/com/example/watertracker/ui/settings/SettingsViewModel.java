package com.example.watertracker.ui.settings;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private SharedPreferences preferences;
    private final MutableLiveData<Integer> weightLiveData;

    public SettingsViewModel() {
        weightLiveData = new MutableLiveData<>();
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;

        int storedWeight = preferences.getInt("Weight", 0);
        weightLiveData.setValue(storedWeight);
    }

    public void saveWeight(int weight) {
        if (preferences == null) {
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("Weight", weight);
        editor.apply();

        weightLiveData.setValue(weight);
    }

    public LiveData<Integer> getStoredWeight() {
        return weightLiveData;
    }
}