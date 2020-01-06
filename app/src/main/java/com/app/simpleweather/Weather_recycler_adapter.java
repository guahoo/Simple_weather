package com.app.simpleweather;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import static com.app.simpleweather.Utility.OftenUsedStrings.ISADAY;
import static com.app.simpleweather.Utility.WeatherIconMap.getResourceIdent;

public class Weather_recycler_adapter extends RecyclerView.Adapter<Weather_recycler_adapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<Weather_model> weather;



    public TextView[] textViewsForecast;
    SharedPreferences sharedPreferences;
    Context context;
    String PREFERENCES;


    Weather_recycler_adapter(Context context, List<Weather_model> weather) {
        this.weather = weather;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public Weather_recycler_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.listview_forecast_item, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(Weather_recycler_adapter.ViewHolder holder, int position) {
        Weather_model weather_model = weather.get(position);
        holder.date_time.setText(weather_model.getDate_time());
        weather_type_set_icon(holder, weather_model.getWeather_type());
        holder.temperature.setText(weather_model.getTemperature());
        holder.pressure.setText(weather_model.getPressure());
        holder.humidity.setText(weather_model.getHudimity());
        holder.wind.setText(weather_model.getWind()+weather_model.windDirection);

    }

    @Override
    public int getItemCount() {
        return weather.size();
    }

    public void weather_type_set_icon(Weather_recycler_adapter.ViewHolder holder, String weather_model) {
        holder.weather_type.setImageResource(getResourceIdent(weather_model));

    }

    public void setTextImageViewColor(TextView[] textView, ImageView imageView, Integer i) {
        for (TextView textViewTemp : textView) {
            textViewTemp.setTextColor(context.getResources().getColor(i));
        }
        imageView.setColorFilter(ContextCompat.getColor(context, i));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date_time, temperature, pressure, humidity, wind;
        final ImageView weather_type;

        ViewHolder(View view) {
            super(view);
            sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            date_time = view.findViewById(R.id.stroke_hour);
            weather_type = view.findViewById(R.id.stroke_weather);
            temperature = view.findViewById(R.id.stroke_temp);
            pressure = view.findViewById(R.id.stroke_pressure);
            humidity = view.findViewById(R.id.stroke_hudimity);
            wind = view.findViewById(R.id.stroke_wind);
            textViewsForecast = new TextView[]{date_time, temperature, pressure, humidity, wind};
            if (sharedPreferences.getBoolean(ISADAY, false)) {
                setTextImageViewColor(textViewsForecast, weather_type, R.color.blackTextColor);
            } else {
                setTextImageViewColor(textViewsForecast, weather_type, R.color.whiteColor);
            }


        }
    }
}

