package com.example.android.lifecycleweather;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.lifecycleweather.data.ForecastHistoryItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ForecastHistoryAdapter extends RecyclerView.Adapter<ForecastHistoryAdapter.ForecastHistoryItemViewHolder> {
    private static final String TAG = ForecastHistoryAdapter.class.getSimpleName();
    private List<ForecastHistoryItem> mForecastHistoryList;
    private OnForecastHistoryItemClickListener onForecastHistoryItemClickListener;

    public interface OnForecastHistoryItemClickListener {
        void onForecastHistoryItemClick(ForecastHistoryItem fhi);
    }

    public ForecastHistoryAdapter(OnForecastHistoryItemClickListener onForecastHistoryItemClickListener){
        this.onForecastHistoryItemClickListener = onForecastHistoryItemClickListener;
    }

    @NonNull
    @Override
    public ForecastHistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.forecast_history_list_item, parent, false);
        return new ForecastHistoryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastHistoryItemViewHolder holder, int position) {
        holder.bind(this.mForecastHistoryList.get(position));
    }

    @RequiresApi(api = Build.VERSION_CODES.N) // Fore reversed() at "compareByTime.reversed()"
    public void updateForecastHistoryData(List<ForecastHistoryItem> forecastHistoryList) {
        if(forecastHistoryList != null) {
            // sort recent cities, with most recently viewed to appear first
            Comparator<ForecastHistoryItem> compareByTime =
                    (ForecastHistoryItem l1, ForecastHistoryItem l2) -> l1.fhiTimeStamp.compareTo(l2.fhiTimeStamp);
            Collections.sort(forecastHistoryList, compareByTime.reversed());

            this.mForecastHistoryList = forecastHistoryList;
        }
    }

    @Override
    public int getItemCount() {
        if(this.mForecastHistoryList == null) {
            return 0;
        } else {
            return this.mForecastHistoryList.size();
        }
    }

    class ForecastHistoryItemViewHolder extends RecyclerView.ViewHolder {
        final private TextView foreCastHistoryItemTV;

        public ForecastHistoryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foreCastHistoryItemTV = itemView.findViewById(R.id.forecast_history_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onForecastHistoryItemClickListener.onForecastHistoryItemClick(
                      mForecastHistoryList.get(getAdapterPosition())
                    );
                }
            });
        }

        public void bind(ForecastHistoryItem forecastHistoryItem) {
            foreCastHistoryItemTV.setText(forecastHistoryItem.fhiLocation);
        }
    }
}
