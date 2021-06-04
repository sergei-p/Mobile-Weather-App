package com.example.android.lifecycleweather;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lifecycleweather.data.FiveDayForecast;
import com.example.android.lifecycleweather.data.ForecastCity;
import com.example.android.lifecycleweather.data.ForecastData;
import com.example.android.lifecycleweather.data.ForecastHistoryItem;
import com.example.android.lifecycleweather.data.LoadingStatus;
import com.google.android.material.navigation.NavigationView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.OnForecastItemClickListener,
            ForecastHistoryAdapter.OnForecastHistoryItemClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    //private static final String OPENWEATHER_APPID = " ";

    private ForecastAdapter forecastAdapter;
    private ForecastHistoryAdapter forecastHistoryAdapter;
    private ForecastRetrievalViewModel forecastRetrievalViewModel;
    private ForecastHistoryViewModel forecastHistoryViewModel;

    private ForecastCity forecastCity;

    private List<ForecastHistoryItem> viewedCities;

    private RecyclerView forecastListRV;
    private RecyclerView forecastHistoryListRv;
    private DrawerLayout drawerLayout;
    private Button button;

    private ProgressBar loadingIndicatorPB;
    private SharedPreferences sharedPreferences;

    private TextView errorMessageTV;
    private Toast errorToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.loadingIndicatorPB = findViewById(R.id.pb_loading_indicator);
        this.errorMessageTV = findViewById(R.id.tv_error_message);
        // Recycler Views
        this.forecastListRV = findViewById(R.id.rv_forecast_list);
        this.forecastListRV.setLayoutManager(new LinearLayoutManager(this));
        this.forecastListRV.setHasFixedSize(true);

        this.forecastHistoryListRv = findViewById(R.id.rv_forecast_history_list);
        this.forecastHistoryListRv.setLayoutManager(new LinearLayoutManager((this))); // possibly change
        this.forecastHistoryListRv.setHasFixedSize(true);

        this.drawerLayout = findViewById(R.id.drawer_layout);

        this.button = findViewById(R.id.nav_settings_option_button);

        // Adapters
        this.forecastAdapter = new ForecastAdapter(this);
        this.forecastListRV.setAdapter(this.forecastAdapter);

        this.forecastHistoryAdapter = new ForecastHistoryAdapter(this);
        this.forecastHistoryListRv.setAdapter(this.forecastHistoryAdapter);

        this.viewedCities = new ArrayList<>();

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String cityValue = sharedPreferences.getString("pref_city", "Corvallis,OR,US");
        String unitsValue = sharedPreferences.getString("pref_units", "@string/pref_units_value_fahrenheit");

        // might have to modify
        this.forecastRetrievalViewModel = new ViewModelProvider(this).get(ForecastRetrievalViewModel.class);

        this.forecastHistoryViewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(ForecastHistoryViewModel.class);

        this.forecastRetrievalViewModel.loadForecastResults(cityValue, unitsValue);

        this.forecastRetrievalViewModel.getSearchResults().observe(
                this,
                new Observer<FiveDayForecast>() {
                    @Override
                    public void onChanged(FiveDayForecast fiveDayForecast) {
                        forecastAdapter.updateForecastUnits(sharedPreferences.getString("pref_units", "@string/pref_units_value_fahrenheit"));
                        forecastAdapter.updateForecastData(fiveDayForecast);

                        if(fiveDayForecast != null){
                            forecastCity = fiveDayForecast.getForecastCity();
                            ActionBar actionBar = getSupportActionBar();
                            actionBar.setTitle(forecastCity.getName());
                        }

                    }
                }
        );

        this.forecastHistoryViewModel.getAllForecastHistoryItemsLive().observe(
                this,
                new Observer<List<ForecastHistoryItem>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChanged(List<ForecastHistoryItem> forecastHistoryItems) {
                        forecastHistoryAdapter.updateForecastHistoryData(forecastHistoryItems);
                        forecastHistoryAdapter.notifyDataSetChanged();
                        forecastHistoryListRv.setAdapter(forecastHistoryAdapter);
                    }
                }
        );

        this.forecastRetrievalViewModel.getLoadingStatus().observe(
                this,
                new Observer<LoadingStatus>() {
                    @Override
                    public void onChanged(LoadingStatus loadingStatus) {
                        if(loadingStatus == LoadingStatus.LOADING) {
                            Log.d(TAG, "Forecast data is in the process of being retrieved...");
                            loadingIndicatorPB.setVisibility(View.VISIBLE);
                        } else if(loadingStatus == LoadingStatus.SUCCESS) {
                            Log.d(TAG, "Forecast data has been successfully retrieved");
                            loadingIndicatorPB.setVisibility(View.INVISIBLE);
                            forecastListRV.setVisibility(View.VISIBLE);
                            errorMessageTV.setVisibility(View.INVISIBLE);
                        } else {
                            Log.d(TAG, "An error occurred while retrieving forecast data");
                            loadingIndicatorPB.setVisibility(View.INVISIBLE);
                            forecastListRV.setVisibility(View.INVISIBLE);
                            errorMessageTV.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        this.forecastRetrievalViewModel.getErrorMessage().observe(
                this,
                new Observer<String>() {
                    @Override
                    public void onChanged(String errorMessage) {
                        // remove non-existent city from db
                        if(errorMessage != null) {
                            if (errorMessage.indexOf("404") == 0) {
                                int city_start = errorMessage.indexOf("(") + 1;
                                int city_end = errorMessage.indexOf(")");
                                String nonExistentCity = errorMessage.substring(city_start, city_end);
                                forecastHistoryViewModel.deleteForecastHistoryItem(nonExistentCity);
                            }
                        }
                        errorMessageTV.setText(errorMessage);
                    }
                }
        );

        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // menu button in top right corner
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    public void openSettingsActivity(){
        this.drawerLayout.closeDrawers();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onForecastHistoryItemClick(ForecastHistoryItem forecastHistoryItem) {
        this.drawerLayout.closeDrawers();
        if(forecastHistoryItem != null){
            this.forecastRetrievalViewModel.loadForecastResults(
                    forecastHistoryItem.fhiLocation,
                    forecastHistoryItem.fhiUnits);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy method was called");
        this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "shared preference changed, key: " + key + ", value: " + sharedPreferences.getString(key, ""));

        String theCity = sharedPreferences.getString("pref_city", "Corvallis,OR,US");
        String theUnits = sharedPreferences.getString("pref_units", "@string/pref_units_value_fahrenheit");

        if(TextUtils.equals(key, "pref_units")) {
            this.forecastAdapter.updateForecastUnits(theUnits);
        }
        this.forecastRetrievalViewModel.loadForecastResults(theCity, theUnits);

        // add new info to db
        ForecastHistoryItem fhiInstance = new ForecastHistoryItem();
        fhiInstance.fhiLocation = theCity;
        fhiInstance.fhiUnits = theUnits;
        fhiInstance.fhiTimeStamp = getTimeStamp().toString();

        this.forecastHistoryViewModel.insertForecastHistoryItem(fhiInstance);
        //freeing memory
        fhiInstance = null;
    }

    @Override
    public void onForecastItemClick(ForecastData forecastData) {
        Intent intent = new Intent(this, ForecastDetailActivity.class);
        intent.putExtra(ForecastDetailActivity.EXTRA_FORECAST_DATA, forecastData);
        intent.putExtra(ForecastDetailActivity.EXTRA_FORECAST_CITY, this.forecastCity);
        intent.putExtra("EXTRA_UNITS_TYPE", sharedPreferences.getString("pref_units", "@string/pref_units_value_fahrenheit"));
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                viewForecastCityInMap();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Timestamp getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();

        Timestamp timestamp = new Timestamp(time);
        return timestamp;
    }

    private void viewForecastCityInMap() {
        if (this.forecastCity != null) {
            Uri forecastCityGeoUri = Uri.parse(getString(
                    R.string.geo_uri,
                    this.forecastCity.getLatitude(),
                    this.forecastCity.getLongitude(),
                    12
            ));
            Intent intent = new Intent(Intent.ACTION_VIEW, forecastCityGeoUri);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                if (this.errorToast != null) {
                    this.errorToast.cancel();
                }
                this.errorToast = Toast.makeText(
                        this,
                        getString(R.string.action_map_error),
                        Toast.LENGTH_LONG
                );
                this.errorToast.show();
            }
        }
    }


}