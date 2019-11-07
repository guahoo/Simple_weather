package com.example.simpleweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Weather_recycler_adapter extends RecyclerView.Adapter<Weather_recycler_adapter.ViewHolder>{


        private LayoutInflater inflater;
        private List<Weather_model> weather;

        Weather_recycler_adapter(Context context, List<Weather_model> weather) {
            this.weather = weather;
            this.inflater = LayoutInflater.from(context);
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
            holder.weather_type.setText(weather_model.getWeather_type());
            holder.temperature.setText(weather_model.getTemperature());
            holder.pressure.setText(weather_model.getPressure());
            holder.wind.setText(weather_model.getWind());

        }

        @Override
        public int getItemCount() {
            return weather.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView date_time,weather_type,temperature,pressure,humidity,wind;
            ViewHolder(View view){
                super(view);
                date_time=view.findViewById(R.id.stroke_hour);
                weather_type = view.findViewById(R.id.stroke_weather);
                temperature = view.findViewById(R.id.stroke_temp);
                pressure=view.findViewById(R.id.stroke_pressure);
                humidity=view.findViewById(R.id.stroke_hudimity);
                wind=view.findViewById(R.id.stroke_wind);

            }
        }
    }

