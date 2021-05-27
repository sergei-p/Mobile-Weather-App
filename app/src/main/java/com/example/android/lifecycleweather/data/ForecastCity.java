package com.example.android.lifecycleweather.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;

public class ForecastCity implements Serializable {
    private String name;
    private double latitude;
    private double longitude;
    private int timezoneOffsetSeconds;

    public ForecastCity() {
        this.name = null;
        this.latitude = 0;
        this.longitude = 0;
        this.timezoneOffsetSeconds = 0;
    }

    public ForecastCity(String name, double latitude, double longitude, int timezoneOffsetSeconds) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezoneOffsetSeconds = timezoneOffsetSeconds;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getTimezoneOffsetSeconds() {
        return timezoneOffsetSeconds;
    }

    public static class JsonDeserializer implements com.google.gson.JsonDeserializer<ForecastCity> {
        @Override
        public ForecastCity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject cityObj = json.getAsJsonObject();
            JsonObject coordObj = cityObj.getAsJsonObject("coord");
            return new ForecastCity(
                    cityObj.getAsJsonPrimitive("name").getAsString(),
                    coordObj.getAsJsonPrimitive("lat").getAsDouble(),
                    coordObj.getAsJsonPrimitive("lon").getAsDouble(),
                    cityObj.getAsJsonPrimitive("timezone").getAsInt()
            );
        }
    }
}
