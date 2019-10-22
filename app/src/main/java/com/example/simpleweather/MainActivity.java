package com.example.simpleweather;

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
import com.example.simpleweather.Utility.Dialog_menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    TextView  updated_atTxt;
    Button addressButton;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    static final int PAGE_COUNT = 10;
    static final String TAG = "myLogs";

    ViewPager pager;
    PagerAdapter pagerAdapter;
    PageFragment pageFragment;

    String CITY = "Moscow";
    String API = "b542736e613d2382837ad821803eb507";
    TextView  statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;
    Context context;
    ProgressBar loader;
    RelativeLayout mainContainer;
    TextView errorText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences (PREFERENCES, MODE_PRIVATE );

        addressButton = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);
        loader = findViewById(R.id.loader);
        mainContainer=findViewById(R.id.mainContainer);
        errorText=findViewById(R.id.errorText);


//        (weather);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        final Dialog_menu d = new Dialog_menu(sharedPreferences,MainActivity.this);




        executeWeatherTask();



        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.showMenuDialog();
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               executeWeatherTask();
                pullToRefresh.setRefreshing(false);
            }
        });


    }


    public void executeWeatherTask() {
        new WeatherTask().execute();
    }

    public  class WeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader.setVisibility(View.VISIBLE);
            mainContainer.setVisibility(View.GONE);
            errorText.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            //String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API);
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/forecast?q=" + sharedPreferences.getString("cityName","Moscow") + "&units=metric&cnt=7&appid=" + API);
            return response;
        }

        @Override
        public void onPostExecute(String result) {


            try {
                JSONObject jsonResult = new JSONObject(result);



                JSONArray jArr = jsonResult.getJSONArray("list"); // Here we have the forecast for every day
                JSONObject jLocation = jsonResult.getJSONObject("city");// Here we have the forecast for every day
                JSONObject jsonObj = jArr.getJSONObject(0);
//
//                // We traverse all the array and parse the data
//                for (int i=0; i < jArr.length(); i++) {
//                    JSONObject jDayForecast = jArr.getJSONObject(i);
//                }

                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");

                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);


                Long updatedAt = jsonObj.getLong("dt");


                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");


                // Long sunrise = sys.getLong("sunrise");
//                Long sunrise = extractSunrize(jsonObj);
                //Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jLocation.getString("name") + ", " + jLocation.getString("country");


                /* Populating extracted data into our views */
                addressButton.setText(address);
//                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
//                temp_minTxt.setText(tempMin);
//                temp_maxTxt.setText(tempMax);
//                // sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
//                // sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
//                windTxt.setText(windSpeed);
//                pressureTxt.setText(pressure);
//                humidityTxt.setText(humidity);

                /* Views populated, Hiding the loader, Showing the main design */
                loader.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);



            } catch (JSONException e) {
                loader.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
            }

        }

        private Long extractSunrize(JSONObject jsonObj) {
            return 0l;
        }


    }



    }


