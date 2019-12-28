package com.app.simpleweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static com.app.simpleweather.CurrentWeather.OPENWEATHERMAP_API_KEY;
import static com.app.simpleweather.MainActivity.JSONARRAY;
import static com.app.simpleweather.MainActivity.LATITUDE;
import static com.app.simpleweather.MainActivity.LONGITUDE;

public class ForecastWeather extends AsyncTask<String, Void, String> {
    public static final String LIST = "list";
    private SharedPreferences sharedPreferences;
    private  String PREFERENCES;
    private Context context;
    private String jsonString;



    ForecastWeather(Context context) {
        this.context=context;
    }
    private final static String URL_REQUEST_FORECAST= "https://api.openweathermap.org/data/2.5/forecast?" +
            "lat=%s&lon=%s&units=metric&cnt=30&appid=%s";



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String cityLat=sharedPreferences.getString(LATITUDE, null);
        String cityLon=sharedPreferences.getString(LONGITUDE, null);
        return HttpRequest.excuteGet(String.format(URL_REQUEST_FORECAST,cityLat,cityLon, OPENWEATHERMAP_API_KEY));
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


        } catch (JSONException e) {
            ((MainActivity)context).soWeGotException();
        }

    }
}
