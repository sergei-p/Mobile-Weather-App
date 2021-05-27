package com.example.android.lifecycleweather;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.lifecycleweather.data.FiveDayForecast;
import com.example.android.lifecycleweather.data.ForecastData;
import com.example.android.lifecycleweather.utils.OpenWeatherUtils;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastItemViewHolder> {
    private FiveDayForecast fiveDayForecast;
    private OnForecastItemClickListener onForecastItemClickListener;

    private String units = "F"; // default value

    public interface OnForecastItemClickListener {
        void onForecastItemClick(ForecastData forecastData);
    }

    public ForecastAdapter(OnForecastItemClickListener onForecastItemClickListener) {
        this.onForecastItemClickListener = onForecastItemClickListener;
    }

    @NonNull
    @Override
    public ForecastItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.forecast_list_item, parent, false);
        return new ForecastItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastItemViewHolder holder, int position) {
        holder.bind(this.fiveDayForecast.getForecastDataList().get(position));
    }

    public void updateForecastData(FiveDayForecast fiveDayForecast) {
        this.fiveDayForecast = fiveDayForecast;
        notifyDataSetChanged();
    }

    public void updateForecastUnits(String unitsType){
        if(TextUtils.equals(unitsType, "standard")) {
            this.units = "K";
        } else if(TextUtils.equals(unitsType, "metric")) {
            this.units = "C";
        } else if(TextUtils.equals(unitsType, "imperial")) {
            this.units = "F";
        }
    }

    @Override
    public int getItemCount() {
        if (this.fiveDayForecast == null || this.fiveDayForecast.getForecastDataList() == null) {
            return 0;
        } else {
            return this.fiveDayForecast.getForecastDataList().size();
        }
    }

    class ForecastItemViewHolder extends RecyclerView.ViewHolder {
        final private TextView dateTV;
        final private TextView timeTV;
        final private TextView highTempTV;
        final private TextView lowTempTV;
        final private TextView popTV;
        final private ImageView iconIV;

        public ForecastItemViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.tv_date);
            timeTV = itemView.findViewById(R.id.tv_time);
            highTempTV = itemView.findViewById(R.id.tv_high_temp);
            lowTempTV = itemView.findViewById(R.id.tv_low_temp);
            popTV = itemView.findViewById(R.id.tv_pop);
            iconIV = itemView.findViewById(R.id.iv_forecast_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onForecastItemClickListener.onForecastItemClick(
                            fiveDayForecast.getForecastDataList().get(getAdapterPosition())
                    );
                }
            });
        }

        public void bind(ForecastData forecastData) {
            Context ctx = this.itemView.getContext();
            Calendar date = OpenWeatherUtils.dateFromEpochAndTZOffset(
                    forecastData.getEpoch(),
                    fiveDayForecast.getForecastCity().getTimezoneOffsetSeconds()
            );
            dateTV.setText(ctx.getString(R.string.forecast_date, date));
            timeTV.setText(ctx.getString(R.string.forecast_time, date));
            highTempTV.setText(ctx.getString(R.string.forecast_temp, forecastData.getHighTemp(), ForecastAdapter.this.units));
            lowTempTV.setText(ctx.getString(R.string.forecast_temp, forecastData.getLowTemp(), ForecastAdapter.this.units));
            popTV.setText(ctx.getString(R.string.forecast_pop, forecastData.getPop()));

            /*
             * Load forecast icon into ImageView using Glide: https://bumptech.github.io/glide/
             */
            Glide.with(ctx)
                    .load(forecastData.getIconUrl())
                    .into(iconIV);
        }

    }
}
