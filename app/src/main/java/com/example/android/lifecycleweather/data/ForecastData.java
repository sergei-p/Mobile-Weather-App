package com.example.android.lifecycleweather.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;

public class ForecastData implements Serializable {
    private final static String ICON_URL_FORMAT_STR = "https://openweathermap.org/img/wn/%s@4x.png";

    private int epoch;
    private int highTemp;
    private int lowTemp;
    private int pop;
    private int cloudCoverage;
    private int windSpeed;
    private int windDirDeg;
    private String shortDescription;
    private String iconUrl;

    public ForecastData() {
        this.epoch = 0;
        this.highTemp = 0;
        this.lowTemp = 0;
        this.pop = 0;
        this.cloudCoverage = 0;
        this.windSpeed = 0;
        this.windDirDeg = 0;
        this.shortDescription = null;
        this.iconUrl = null;
    }


    public ForecastData(int epoch, int highTemp, int lowTemp, int pop,
                        int cloudCoverage, int windSpeed, int windDirDeg,
                        String shortDescription, String iconUrl) {
        this.epoch = epoch;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.pop = pop;
        this.cloudCoverage = cloudCoverage;
        this.windSpeed = windSpeed;
        this.windDirDeg = windDirDeg;
        this.shortDescription = shortDescription;
        this.iconUrl = iconUrl;
    }

    public int getEpoch() {
        return epoch;
    }

    public int getHighTemp() {
        return highTemp;
    }

    public int getLowTemp() {
        return lowTemp;
    }

    public int getPop() {
        return pop;
    }

    public int getCloudCoverage() {
        return cloudCoverage;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public int getWindDirDeg() {
        return windDirDeg;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public static class JsonDeserializer implements com.google.gson.JsonDeserializer<ForecastData> {
        @Override
        public ForecastData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject listObj = json.getAsJsonObject();
            JsonObject mainObj = listObj.getAsJsonObject("main");
            JsonArray weatherArr = listObj.getAsJsonArray("weather");
            JsonObject weatherObj = weatherArr.get(0).getAsJsonObject();
            JsonObject cloudsObj = listObj.getAsJsonObject("clouds");
            JsonObject windObj = listObj.getAsJsonObject("wind");

            return new ForecastData(
                    listObj.getAsJsonPrimitive("dt").getAsInt(),
                    (int)Math.round(mainObj.getAsJsonPrimitive("temp_max").getAsDouble()),
                    (int)Math.round(mainObj.getAsJsonPrimitive("temp_min").getAsDouble()),
                    (int)Math.round(listObj.getAsJsonPrimitive("pop").getAsDouble() * 100),
                    cloudsObj.getAsJsonPrimitive("all").getAsInt(),
                    (int)Math.round(windObj.getAsJsonPrimitive("speed").getAsDouble()),
                    windObj.getAsJsonPrimitive("deg").getAsInt(),
                    weatherObj.getAsJsonPrimitive("description").getAsString(),
                    String.format(
                            ICON_URL_FORMAT_STR,
                            weatherObj.getAsJsonPrimitive("icon").getAsString()
                    )
            );
        }
    }
}
