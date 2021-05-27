package com.example.android.lifecycleweather.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "forecast_history_item")
public class ForecastHistoryItem {
    @NonNull
    @PrimaryKey
    public String fhiLocation;

    @ColumnInfo(name = "time_stamp")
    public String fhiTimeStamp;

    @ColumnInfo(name = "units")
    public String fhiUnits;
}
