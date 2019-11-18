package com.example.simpleweather;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Weather_recycler_adapter extends RecyclerView.Adapter<Weather_recycler_adapter.ViewHolder>{


        private LayoutInflater inflater;
        private List<Weather_model> weather;
    private static Map<String, Integer> weather_icons_map = new HashMap<>();

    static {
        weather_icons_map.put("overcast clouds", R.drawable.ic_cloud);
        weather_icons_map.put("clear sky", R.drawable.ic_sun);
        weather_icons_map.put("shower rain", R.drawable.ic_rain);
        weather_icons_map.put("rain", R.drawable.ic_rain);
        weather_icons_map.put("light rain", R.drawable.ic_rain_alt_sun);
        weather_icons_map.put("scattered clouds", R.drawable.ic_cloud);
        weather_icons_map.put("few clouds", R.drawable.ic_cloud);
        weather_icons_map.put("broken clouds", R.drawable.ic_cloud_sun);
        weather_icons_map.put("snow", R.drawable.ic_snow_alt);
        weather_icons_map.put("", R.drawable.ic_umbrella);
    }

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
            Weather_model weather_model=weather.get(position);
            holder.date_time.setText(weather_model.getDate_time());

            weather_type_set_icon(holder, weather_model.getWeather_type());

            holder.temperature.setText(weather_model.getTemperature());
            holder.pressure.setText(weather_model.getPressure());
            holder.humidity.setText(weather_model.getHudimity());
            holder.wind.setText(weather_model.getWind());

        }

        @Override
        public int getItemCount() {
            return weather.size();
        }

    public void weather_type_set_icon(Weather_recycler_adapter.ViewHolder holder, String weather_model) {
        holder.weather_type.setImageResource(
                weather_icons_map.get(weather_icons_map.containsKey(weather_model) ? weather_model : ""));

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

            ViewHolder(View view){
                super(view);
                sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                date_time=view.findViewById(R.id.stroke_hour);
                weather_type = view.findViewById(R.id.stroke_weather);
                temperature = view.findViewById(R.id.stroke_temp);
                pressure=view.findViewById(R.id.stroke_pressure);
                humidity=view.findViewById(R.id.stroke_hudimity);
                wind=view.findViewById(R.id.stroke_wind);
                textViewsForecast = new TextView[]{date_time, temperature, pressure, humidity, wind};
                if (sharedPreferences.getBoolean("day", false)) {
                    setTextImageViewColor(textViewsForecast, weather_type, R.color.blackTextColor);
                } else {
                    setTextImageViewColor(textViewsForecast, weather_type, R.color.whiteColor);
                }


            }
        }
    }

