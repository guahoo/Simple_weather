package com.example.simpleweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;
import com.example.simpleweather.Utility.Convert;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentWeather extends AsyncTask<String, Void, String> {
    Context context;
    String API="b542736e613d2382837ad821803eb507";
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    String jsonString;


    public String getLocation() {
        return location;
    }

    public String getTempMin() {
        return tempMin;
    }

    public String getTempMax() {
        return tempMax;
    }

    public String getUpdatedAtText() {
        return updatedAtText;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }


    public String getWeatherType() {
        return weatherType;
    }

    String weatherType;
    String location;
    String tempMin;
    String tempMax;
    String updatedAtText;
    String windDirection;
    String windSpeed;

    public String getTemp() {
        return temp;
    }

    String temp;
    long sunrise,sunset,updatedAt;



    public CurrentWeather(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String cityLat=sharedPreferences.getString("cityLat", "55");
    }

    @Override
    protected String doInBackground(String... args) {

        String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?" +"lat="+
                sharedPreferences.getString("cityLat", "55") +"&"+"lon="+ sharedPreferences.getString("cityLon", "55")+"&units=metric&appid=" + API);
        return response;
    }

    @Override
    public void onPostExecute(String result) {

        if (result == null) {
            result = "allbegood";
        }


        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject("main");
            JSONObject sys = jsonObj.getJSONObject("sys");
            JSONObject wind = jsonObj.getJSONObject("wind");



            setWindDirection(Integer.parseInt(wind.getString("deg")));
            windSpeed =wind.getString("speed")+" ";



            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);


            weatherType = weather.getString("description");
            location = jsonObj.getString("name") + ", " + sys.getString("country");
            sunrise = sys.getLong("sunrise");
            sunset = sys.getLong("sunset");
            updatedAt = jsonObj.getLong("dt");
            temp = Convert.tempString(main.getString("temp"));
            tempMin = "Min Temp: " + Convert.tempString(main.getString("temp_min"));
            tempMax = "Max Temp: " + Convert.tempString(main.getString("temp_max"));

            updatedAtText = new SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    .format(new Date(updatedAt * 1000));


            jsonString = jsonObj.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jsonObj", jsonString);
            editor.putString("temp", temp);
            editor.apply();







            ((MainActivity)context).setCurrentWeatherData();
            ((MainActivity)context).set_day_night_background(updatedAt, sunrise, sunset);
            ((MainActivity)context).initChannels(context);
            ((MainActivity)context).startNotifyIntent();


        } catch (JSONException e) {
            ( (MainActivity)context).soWeGotException();

        }

    }


   private void setWindDirection(int wind) {
        if (wind < 10 && wind >= 0) {
            windDirection = "N";
        } else if (wind <= 359 && wind > 350) {
            windDirection = "N";
        } else if (wind > 80 && wind < 100) {
            windDirection = "E";
        } else if (wind > 170 && wind < 190) {
            windDirection = "S";
        } else if (wind > 260 && wind < 280) {
            windDirection = "W";
        } else if (wind >= 10 && wind <= 80) {
            windDirection = "NE";
        } else if (wind >= 100 && wind <= 170) {
            windDirection = "SE";
        } else if (wind >= 190 && wind <= 260) {
            windDirection = "SW";
        } else if (wind >= 280 && wind <= 350) {
            windDirection = "NW";
        }
    }


}