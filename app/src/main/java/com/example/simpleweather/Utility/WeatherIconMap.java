package com.example.simpleweather.Utility;

import com.example.simpleweather.R;

import java.util.HashMap;
import java.util.Map;

public class WeatherIconMap {
    public static Map<String, Integer> weather_icons_map = new HashMap<>();

    static {
        weather_icons_map.put("overcast clouds", R.drawable.ic_cloud);
        weather_icons_map.put("clear sky", R.drawable.ic_sun);
        weather_icons_map.put("shower rain", R.drawable.ic_rain);
        weather_icons_map.put("shower rain_alert",R.drawable.ic_rain_alert);
        weather_icons_map.put("light rain_alert",R.drawable.ic_rain_alert);
        weather_icons_map.put("rain_alert",R.drawable.ic_rain_alert);
        weather_icons_map.put("rain", R.drawable.ic_rain);
        weather_icons_map.put("light rain", R.drawable.ic_rain_alt_sun);
        weather_icons_map.put("scattered clouds", R.drawable.ic_cloud);
        weather_icons_map.put("few clouds", R.drawable.ic_cloud);
        weather_icons_map.put("broken clouds", R.drawable.ic_cloud_sun);
        weather_icons_map.put("snow", R.drawable.ic_snow_alt);
        weather_icons_map.put("snow_alert",R.drawable.ic_snow_alert_01);
        weather_icons_map.put("light snow", R.drawable.ic_snow_alt);
        weather_icons_map.put("light intensity drizzle", R.drawable.ic_cloud_sun);
        weather_icons_map.put("mist",R.drawable.ic_fog);
        weather_icons_map.put("fog",R.drawable.ic_fog);
        weather_icons_map.put("", R.drawable.ic_umbrella);
    }

    public WeatherIconMap() {
    }

    public static Integer getWeather_icons_map(String s) {
        return weather_icons_map.get(s);
    }
}
