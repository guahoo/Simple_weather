package com.example.simpleweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;
import com.example.simpleweather.Utility.Convert;
import com.example.simpleweather.Utility.Dialog_menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    Button addressButton;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    static JSONArray jArr;





    String API = "b542736e613d2382837ad821803eb507";
    TextView   tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt,updateTxt;
    ProgressBar loader;
    TextView errorText;
    String jsonString;
    JSONArray jsonArray;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences (PREFERENCES, MODE_PRIVATE );
        String jSonArray = this.getResources().getString(R.string.jsonArray);

        if (sharedPreferences.getAll().isEmpty()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jsonArray",jSonArray);
            editor.putString("cityName","Moscow");
            editor.apply();
        }

        addressButton = findViewById(R.id.address);


        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        updateTxt=findViewById(R.id.updated_at);
        loader = findViewById(R.id.loader);
        errorText=findViewById(R.id.errorText);



        String jsonString =sharedPreferences.getString("jsonArray",null);
        try {
           jsonArray= new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeWeatherTask();

        setViewPager();



        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        final Dialog_menu d = new Dialog_menu(sharedPreferences,MainActivity.this);


        addressButton.setOnClickListener(v -> d.showMenuDialog());

        pullToRefresh.setOnRefreshListener(() -> {
            executeWeatherTask();
            pullToRefresh.setRefreshing(false);
        });


    }


    public void executeWeatherTask() {
        new WeatherTask().execute();
        new CurrentWeatherTask().execute();
    }



    public class CurrentWeatherTask extends WeatherTask {

        @Override
        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" +
                    sharedPreferences.getString("cityName","Moscow") + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        public void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");

                long sunrise = sys.getLong("sunrise");
                long sunset = sys.getLong("sunset");
                long updatedAt = jsonObj.getLong("dt");

                String temp=Convert.tempString(main.getString("temp"));




                String tempMin = "Min Temp: " + Convert.tempString(main.getString("temp_min"));
                String tempMax = "Max Temp: " + Convert.tempString(main.getString("temp_max"));

                String updatedAtText = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
                        .format(new Date(updatedAt * 1000));

               tempTxt.setText(temp);
               temp_minTxt.setText(tempMin);
               temp_maxTxt.setText(tempMax);
               sunriseTxt.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(sunrise * 1000)));
               sunsetTxt.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(sunset * 1000)));


               updateTxt.setText(updatedAtText);

                /* Views populated, Hiding the loader, Showing the main design */
                loader.setVisibility(View.GONE);

            } catch (JSONException e) {
                loader.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
            }

        }



    }





    public  class WeatherTask extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            //String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API);
            return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/forecast?q=" + sharedPreferences.
                    getString("cityName","Moscow") + "&units=metric&cnt=30&appid=" + API);
        }

        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);

                jArr = jsonResult.getJSONArray("list"); // Here we have the forecast for every day
                JSONObject jLocation = jsonResult.getJSONObject("city");// Here we have the forecast for every day
                JSONObject jsonObj = jArr.getJSONObject(0);
                jsonString = jArr.toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("jsonArray",jsonString);
                editor.apply();

                String jsonString =sharedPreferences.getString("jsonArray",null);
                try {
                    jsonArray= new JSONArray(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setViewPager();



                String address = jLocation.getString("name") + ", " + jLocation.getString("country");


//                /* Populating extracted data into our views */
                addressButton.setText(address);

                /* Views populated, Hiding the loader, Showing the main design */
                loader.setVisibility(View.GONE);
              //  pager.setVisibility(View.VISIBLE);



            } catch (JSONException e) {
                loader.setVisibility(View.GONE);
            }
        }
    }
    public void setViewPager(){
        ViewPager viewPager = findViewById(R.id.pager);
        PagerAdapter adapter = new ViewPagerAdapter(MainActivity.this, jsonArray);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

    }



    }


