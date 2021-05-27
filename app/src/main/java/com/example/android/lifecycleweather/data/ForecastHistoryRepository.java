package com.example.android.lifecycleweather.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ForecastHistoryRepository {
    private ForecastHistoryDao dao;

    public ForecastHistoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.dao = db.forecastHistoryDao();
    }

    public void insertForecastHistoryItem(ForecastHistoryItem fhi) {
        AppDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insert(fhi);
            }
        });
    }

    public void deleteForecastHistoryItem(String nonExistentCity) {
        AppDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(nonExistentCity);
            }
        });
    }

    public LiveData<List<ForecastHistoryItem>> getAllForecastHistoryItemsLive() {
        return this.dao.getAllForecastHistoryItemsLive();
    }

    public List<ForecastHistoryItem> getAllForecastHistoryItems() {
        return this.dao.getAllForecastHistoryItems();
    }

    public LiveData<ForecastHistoryItem> getForecastHistoryItemByLocation(String location) {
        return this.dao.getForecastHistoryItemByLocation(location);
    }
}
