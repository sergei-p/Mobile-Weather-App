package com.example.android.lifecycleweather.data;


import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.lifecycleweather.utils.OpenWeatherUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastRetrievalRepository {
    private static final String TAG = ForecastRetrievalRepository.class.getSimpleName();
    private static final String BASE_URL = "https://api.openweathermap.org";
    private static final String OPENWEATHER_APPID = "575a8a4d2f391e11befd2c3d7b305c9f";

    private MutableLiveData<FiveDayForecast> searchResults;

    private MutableLiveData<LoadingStatus> loadingStatus;

    private MutableLiveData<String> errorMessage;

    private String currentCity;
    private String currentUnits;

    private ForecastService forecastService;

    public ForecastRetrievalRepository() {
        this.searchResults = new MutableLiveData<>();
        this.searchResults.setValue(null);

        this.loadingStatus = new MutableLiveData<>();
        this.loadingStatus.setValue(LoadingStatus.SUCCESS);

        this.errorMessage = new MutableLiveData<>();
        this.errorMessage.setValue(null);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ForecastData.class, new ForecastData.JsonDeserializer())
                .registerTypeAdapter(ForecastCity.class, new ForecastCity.JsonDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.forecastService = retrofit.create(ForecastService.class);
    }

    public  LiveData<FiveDayForecast> getSearchResult() { return this.searchResults; }

    public LiveData<LoadingStatus> getLoadingStatus() {
        return this.loadingStatus;
    }

    public LiveData<String> getErrorMessage() { return this.errorMessage; }

    public boolean shouldExecuteSearch(String city, String units) {

        return !TextUtils.equals(city, this.currentCity)
                || !TextUtils.equals(units, this.currentUnits)
                || this.getLoadingStatus().getValue() == LoadingStatus.ERROR;

    }

    public void loadForecastResults(String city, String units) {

        if(this.shouldExecuteSearch(city, units)) {
            Log.d(TAG, "Retrieving new forecast data for city: " + city + " with temperature units of: " + units);
            this.currentCity = city;
            this.currentUnits = units;
            this.executeForecastRetrieval(city, units, OPENWEATHER_APPID);
        } else {
            Log.d(TAG, "using cached data for current weather forecast");
        }
    }

    private void executeForecastRetrieval(String city, String units, String apiID) {
        Call<FiveDayForecast> results;

        results = forecastService.getForecastData(city, units, apiID);


        this.searchResults.setValue(null);
        this.loadingStatus.setValue(LoadingStatus.LOADING);
        results.enqueue(new Callback<FiveDayForecast>() {
            @Override
            public void onResponse(Call<FiveDayForecast> call, Response<FiveDayForecast> response) {
                if(response.code() == 200){
                    searchResults.setValue(response.body());
                    loadingStatus.setValue(LoadingStatus.SUCCESS);
                } else if(response.code() == 404){
                    Log.d(TAG, response.code() + " error occurred while executing HTTP GET request");
                    // String structure is important
                    // String is parsed and values are used error type identification and db access
                    errorMessage.setValue("404 Error " + "(" + city + ")" + " City Not Found. Please try a different location.");
                    loadingStatus.setValue(LoadingStatus.ERROR);
                } else {
                    Log.d(TAG, response.code() + " error occurred while executing HTTP GET request");
                    errorMessage.setValue("Server responded with " + response.code() + " error");
                    loadingStatus.setValue(LoadingStatus.ERROR);
                }
            }

            @Override
            public void onFailure(Call<FiveDayForecast> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "HTTP Request Failed: " + t.getCause());

                String noNetworkConnection = "android_getaddrinfo failed: EAI_NODATA (No address associated with hostname)";

                if(TextUtils.equals(noNetworkConnection, t.getCause().getMessage())){
                    String errorMsg = "Unable to fetch forecast data.  Please check your internet connection.";
                    errorMessage.setValue(errorMsg);
                } else {
                    errorMessage.setValue(t.getCause().getMessage());
                }
                loadingStatus.setValue(LoadingStatus.ERROR);
            }
        });
    }
}
