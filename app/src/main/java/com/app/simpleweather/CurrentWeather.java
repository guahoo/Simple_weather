package com.app.simpleweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;
import com.app.simpleweather.Utility.Convert;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.app.simpleweather.MainActivity.COUNTRY_NAME;
import static com.app.simpleweather.MainActivity.DEG;
import static com.app.simpleweather.MainActivity.DESCRIPTION;
import static com.app.simpleweather.MainActivity.DT;
import static com.app.simpleweather.MainActivity.JSONOBJECT;
import static com.app.simpleweather.MainActivity.LATITUDE;
import static com.app.simpleweather.MainActivity.LOCATION_NAME;
import static com.app.simpleweather.MainActivity.LONGITUDE;
import static com.app.simpleweather.MainActivity.MAIN;
import static com.app.simpleweather.MainActivity.SUNRISE;
import static com.app.simpleweather.MainActivity.SUNSET;
import static com.app.simpleweather.MainActivity.SYS;
import static com.app.simpleweather.MainActivity.TEMP;
import static com.app.simpleweather.MainActivity.TEMP_MAX;
import static com.app.simpleweather.MainActivity.TEMP_MIN;
import static com.app.simpleweather.MainActivity.WEATHER;
import static com.app.simpleweather.MainActivity.WINDSPEED;
import static com.app.simpleweather.MainActivity.WIND;
import static com.app.simpleweather.Utility.SearchByGeoposition.COMMA;

public class CurrentWeather extends AsyncTask<String, Void, String> {


    Context context;
    public static final String OPENWEATHERMAP_API_KEY ="b542736e613d2382837ad821803eb507";
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
    private final static String URL_REQUEST_FORECAST=
            "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s";





    public CurrentWeather(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... args) {
        String latitude=sharedPreferences.getString(LATITUDE, null);
        String longitude=sharedPreferences.getString(LONGITUDE, null);

        return HttpRequest.excuteGet(String.format(URL_REQUEST_FORECAST,latitude,longitude, OPENWEATHERMAP_API_KEY));
    }

    @Override
    public void onPostExecute(String result) {

        if (result == null) {
            ((MainActivity)context).soWeGotException();

        }


        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject(MAIN);
            JSONObject sys = jsonObj.getJSONObject(SYS);
            JSONObject wind = jsonObj.getJSONObject(WIND);



            setWindDirection(Integer.parseInt(wind.getString(DEG)));
            int windSpeedConverting=(int)Double.parseDouble(wind.getString(WINDSPEED));
            windSpeed =(windSpeedConverting)+" ";



            JSONObject weather = jsonObj.getJSONArray(WEATHER).getJSONObject(0);


            weatherType = weather.getString(DESCRIPTION);
            location = jsonObj.getString(LOCATION_NAME) + COMMA + sys.getString(COUNTRY_NAME);
            sunrise = sys.getLong(SUNRISE);
            sunset = sys.getLong(SUNSET);
            updatedAt = jsonObj.getLong(DT);
            temp = Convert.tempString(main.getString(TEMP));



            //TODO formatted

            updatedAtText = new SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    .format(new Date(updatedAt * 1000));


            jsonString = jsonObj.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(JSONOBJECT, jsonString);
            editor.putString(TEMP, temp);
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