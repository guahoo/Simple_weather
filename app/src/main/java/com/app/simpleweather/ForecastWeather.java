package com.app.simpleweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastWeather extends AsyncTask<String, Void, String> {
    private SharedPreferences sharedPreferences;
    private  String PREFERENCES;
    private Context context;
    private String jsonString;



    ForecastWeather(Context context) {
        this.context=context;
    }
    private final static String URL_REQUEST_FORECAST= "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&cnt=30&appid=%s";



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String cityLat=sharedPreferences.getString("cityLat", null);
        String cityLon=sharedPreferences.getString("cityLon", null);

        String API = "b542736e613d2382837ad821803eb507";
        return HttpRequest.excuteGet(String.format(URL_REQUEST_FORECAST,cityLat,cityLon, API));
    }

    @Override
    public void onPostExecute(String result) {
        if (result == null) {
            ((MainActivity)context).soWeGotException();
        }

        try {
            assert result != null;
            JSONObject jsonResult = new JSONObject(result);
            JSONArray jArr = jsonResult.getJSONArray("list");
            jsonString = jArr.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jsonArray", jsonString);
            editor.apply();

            ((MainActivity)context).setForecastInitialData();
            ((MainActivity)context).setForecastDate(0);


        } catch (JSONException e) {
            ((MainActivity)context).soWeGotException();
        }

    }
}
