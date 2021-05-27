package com.example.android.lifecycleweather.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ForecastHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ForecastHistoryItem fhi);

    @Query("SELECT * FROM forecast_history_item")
    List<ForecastHistoryItem> getAllForecastHistoryItems();

    @Query("SELECT * FROM forecast_history_item")
    LiveData<List<ForecastHistoryItem>> getAllForecastHistoryItemsLive();

    @Query("Select * FROM forecast_history_item WHERE fhiLocation = :location LIMIT 1")
    LiveData<ForecastHistoryItem> getForecastHistoryItemByLocation(String location);

    @Query("DELETE FROM forecast_history_item WHERE fhiLocation = :nonExistentCity")
    void delete(String nonExistentCity);

}
