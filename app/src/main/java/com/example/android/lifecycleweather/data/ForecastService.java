package com.example.android.lifecycleweather.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastService {

    @GET("data/2.5/forecast")
    Call<FiveDayForecast> getForecastData(@Query("q") String city, @Query("units") String units, @Query("appid") String appID);
}
