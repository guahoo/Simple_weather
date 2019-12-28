package com.app.simpleweather.Utility;

import com.app.simpleweather.R;

import java.util.HashMap;
import java.util.Map;

public class WeatherIconMap {
    public static Map<String, Integer> weather_icons_map = new HashMap<>();


    private static final String OVERCAST_CLOUDS = "overcast clouds";
    private static final String CLEAR_SKY = "clear sky";
    private static final String SHOWER_RAIN = "shower rain";
    private static final String SHOWER_RAIN_ALERT = "shower rain_alert";
    private static final String LIGHT_RAIN_ALERT = "light rain_alert";
    private static final String RAIN_ALERT = "rain_alert";
    private static final String RAIN = "rain" ;
    private static final String LIGHT_RAIN = "light rain";
    private static final String SCATTERED_CLOUD = "scattered clouds" ;
    private static final String FEW_CLOUDS = "few clouds";
    private static final String BROKEN_CLOUDS = "broken clouds";
    private static final String SNOW ="snow" ;
    private static final String SNOW_ALERT = "snow_alert";
    private static final String LIGHT_SNOW = "light snow";
    private static final String LIGHT_INTENSITY_DRIZZLE = "light intensity drizzle";
    private static final String MIST = "mist";
    private static final String DRIZZLE = "drizzle";
    private static final String FOG = "fog";

    static {
        weather_icons_map.put(OVERCAST_CLOUDS, R.drawable.ic_cloud);
        weather_icons_map.put(CLEAR_SKY, R.drawable.ic_sun);
        weather_icons_map.put(SHOWER_RAIN, R.drawable.ic_rain);
        weather_icons_map.put(SHOWER_RAIN_ALERT,R.drawable.ic_rain_alert);
        weather_icons_map.put(LIGHT_RAIN_ALERT,R.drawable.ic_rain_alert);
        weather_icons_map.put(RAIN_ALERT,R.drawable.ic_rain_alert);
        weather_icons_map.put(RAIN, R.drawable.ic_rain);
        weather_icons_map.put(LIGHT_RAIN, R.drawable.ic_rain_alt_sun);
        weather_icons_map.put(SCATTERED_CLOUD, R.drawable.ic_cloud);
        weather_icons_map.put(FEW_CLOUDS, R.drawable.ic_cloud);
        weather_icons_map.put(BROKEN_CLOUDS, R.drawable.ic_cloud_sun);
        weather_icons_map.put(SNOW, R.drawable.ic_snow_alt);
        weather_icons_map.put(SNOW_ALERT,R.drawable.ic_snow_alert_01);
        weather_icons_map.put(LIGHT_SNOW, R.drawable.ic_snow_alt);
        weather_icons_map.put(LIGHT_INTENSITY_DRIZZLE, R.drawable.ic_cloud_sun);
        weather_icons_map.put(MIST,R.drawable.ic_fog);
        weather_icons_map.put(DRIZZLE,R.drawable.ic_fog);
        weather_icons_map.put(FOG,R.drawable.ic_fog);
        weather_icons_map.put(null, R.drawable.ic_umbrella);
    }

    public WeatherIconMap() {
    }

    public static Integer getWeather_icons_map(String s) {
        return weather_icons_map.get(s);
    }
}
