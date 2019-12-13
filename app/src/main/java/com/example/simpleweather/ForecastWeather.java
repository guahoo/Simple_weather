package com.example.simpleweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastWeather extends AsyncTask<String, Void, String> {
    JSONArray jArr;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    String API = "b542736e613d2382837ad821803eb507";
    Context context;
    String jsonString;



    public ForecastWeather( Context context) {
        this.context=context;
    }
    final static String URL_REQUEST_FORECAST= "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&cnt=30&appid=%s";



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            sharedPreferences = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String cityLat=sharedPreferences.getString("cityLat", "55");
        String cityLon=sharedPreferences.getString("cityLon", "55");

        return HttpRequest.excuteGet(String.format(URL_REQUEST_FORECAST,cityLat,cityLon,API));
    }

    @Override
    public void onPostExecute(String result) {
        if (result == null) {
            result = "allbegood";
        }

        try {
            JSONObject jsonResult = new JSONObject(result);
            jArr = jsonResult.getJSONArray("list");
            jsonString = jArr.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jsonArray", jsonString);
            editor.apply();

            ((MainActivity)context).setInitialData();
            ((MainActivity)context).setForecastDate(0);


        } catch (JSONException e) {

        }

    }
}
