package com.example.android.lifecycleweather;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.lifecycleweather.data.FiveDayForecast;
import com.example.android.lifecycleweather.data.ForecastData;
import com.example.android.lifecycleweather.data.ForecastHistoryItem;
import com.example.android.lifecycleweather.data.ForecastHistoryRepository;
import com.example.android.lifecycleweather.data.ForecastRetrievalRepository;
import com.example.android.lifecycleweather.data.LoadingStatus;

import java.util.List;

public class ForecastRetrievalViewModel extends  ViewModel {
    private static final String TAG = ForecastRetrievalViewModel.class.getSimpleName();
    private LiveData<FiveDayForecast> searchResults;
    private LiveData<LoadingStatus> loadingStatus;
    private LiveData<String> errorMessage;
    private ForecastRetrievalRepository repository;

    public ForecastRetrievalViewModel() {
        this.repository = new ForecastRetrievalRepository();
        this.searchResults = this.repository.getSearchResult();
        this.loadingStatus = this.repository.getLoadingStatus();
        this.errorMessage = this.repository.getErrorMessage();
    }

    public void loadForecastResults(String city, String units) {
        this.repository.loadForecastResults(city, units);
    }

    public LiveData<FiveDayForecast> getSearchResults() { return this.searchResults; }

    public LiveData<LoadingStatus> getLoadingStatus() { return this.loadingStatus; }

    public LiveData<String> getErrorMessage() { return this.errorMessage; }

}
