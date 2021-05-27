package com.example.android.lifecycleweather;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.lifecycleweather.data.ForecastHistoryItem;
import com.example.android.lifecycleweather.data.ForecastHistoryRepository;

import java.util.List;

public class ForecastHistoryViewModel extends AndroidViewModel {
    private static final String TAG = ForecastHistoryViewModel.class.getSimpleName();
    private ForecastHistoryRepository repository;

    public ForecastHistoryViewModel(Application application) {
        super(application);
        this.repository = new ForecastHistoryRepository(application);
    }

    public void insertForecastHistoryItem(ForecastHistoryItem fhi) {
        this.repository.insertForecastHistoryItem(fhi);
    }

    public void deleteForecastHistoryItem(String nonExistentCity) {
        this.repository.deleteForecastHistoryItem(nonExistentCity);
    }

    public List<ForecastHistoryItem> getAllForecastHistoryItems() {
        return this.repository.getAllForecastHistoryItems();
    }

    public LiveData<List<ForecastHistoryItem>> getAllForecastHistoryItemsLive() {
        return this.repository.getAllForecastHistoryItemsLive();
    }

    public LiveData<ForecastHistoryItem> getForecastHistoryItemByLocation(String location) {
        return this.repository.getForecastHistoryItemByLocation(location);
    }
}
