package com.example.simpleweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class ForecastWeather extends AsyncTask<String, Void, String> {
    JSONArray jArr;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    String API = "b542736e613d2382837ad821803eb507";
    ProgressBar loader;

    View v;
    Context context;
    String jsonString;
    TextView errorText;

    public ForecastWeather( Context context) {
        this.v=v;
        this.context=context;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//        errorText = v.findViewById(R.id.errorText);
//
//        loader.setVisibility(View.VISIBLE);
//        errorText.setVisibility(View.GONE);
        sharedPreferences = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... strings) {

        return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/forecast?q=" + sharedPreferences.
                getString("cityName", "Moscow") + "&units=metric&cnt=30&appid=" + API);
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
//           loader.setVisibility(View.GONE);
//           errorText.setVisibility(View.VISIBLE);
        }

    }
}
