package com.app.simpleweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import static com.app.simpleweather.CurrentWeather.OPENWEATHERMAP_API_KEY;
import static com.app.simpleweather.Utility.OftenUsedStrings.JSONARRAY;
import static com.app.simpleweather.Utility.OftenUsedStrings.LATITUDE;
import static com.app.simpleweather.Utility.OftenUsedStrings.LONGITUDE;
import static com.app.simpleweather.Utility.OftenUsedStrings.URL_REQUEST_OPEN_WEATHER_MAP_FORECAST_ALERT;
import static com.app.simpleweather.Utility.OftenUsedStrings.URL_REQUEST_OPEN_WEATHER_MAP_FORECAST_CASUAL;


public class ForecastWeather extends AsyncTask<String, Void, String> {
    public static final String LIST = "list";
    private SharedPreferences sharedPreferences;
    private  String PREFERENCES;
    private Context context;
    private String jsonString;



    ForecastWeather(Context context) {
        this.context=context;
    }




    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String cityLat=sharedPreferences.getString(LATITUDE, null);
        String cityLon=sharedPreferences.getString(LONGITUDE, null);
        return HttpRequest.excuteGet(String.format(URL_REQUEST_OPEN_WEATHER_MAP_FORECAST_CASUAL,cityLat,cityLon, OPENWEATHERMAP_API_KEY));
    }

    @Override
    public void onPostExecute(String result) {
        if (result == null) {
            ((MainActivity)context).soWeGotException();
        }

        try {
            assert result != null;
            JSONObject jsonResult = new JSONObject(result);
            JSONArray jArr = jsonResult.getJSONArray(LIST);
            jsonString = jArr.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(JSONARRAY, jsonString);
            editor.apply();

            ((MainActivity)context).setForecastInitialData();
            ((MainActivity)context).setForecastDate(0);


        } catch (Exception e) {
            ((MainActivity)context).soWeGotException();
        }

    }
}
