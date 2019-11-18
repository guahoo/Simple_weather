package com.example.simpleweather;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;
import com.example.simpleweather.Utility.Convert;
import com.example.simpleweather.Utility.Dialog_menu;
import com.example.simpleweather.Utility.WeatherBar;
import com.example.simpleweather.Utility.WeatherRenewService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {


    List<Weather_model> weather_forecast = new ArrayList<>();
    Button addressButton;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    static JSONArray jArr;
    private static final String NOTIF_CHANNEL_ID = "1";
    String API = "b542736e613d2382837ad821803eb507";
    private static int firstVisibleInListview;
    TextView tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, updateTxt, forecast_date, errorText, weatherView,
            sunsetView, sunRiseView;
    ImageView sun_rise_icon, sun_set_icon, hour_View, weather_View, temp_View, pressure_View, humidity_View, wind_View;





    ProgressBar loader;
    String jsonString;
    JSONArray jsonArray;
    LinearLayoutManager layoutManager;
    Weather_recycler_adapter adapter;
    RecyclerView recyclerView;
    RelativeLayout mainLayout, mainContainer;
    SwipeRefreshLayout pullToRefresh;
    Dialog_menu dialog_menu;

    String updatedAtText_simplify;
    TextView[] textViews;
    ImageView[] icons;
    WeatherBar weatherBar;
    NotificationManager notificationManager;





    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        weatherBar = new WeatherBar(getApplicationContext());
        notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        String jSonArray = this.getResources().getString(R.string.jsonArray);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getAll().isEmpty()) {

            editor.putString("jsonArray", jSonArray);
            editor.putString("cityName", "Moscow");
            editor.apply();
        }

        String jsonString = sharedPreferences.getString("jsonArray", null);
        textViews = new TextView[]{
                tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
                sunsetTxt, updateTxt, forecast_date, errorText, weatherView,
                sunRiseView, sunsetView
        };


        icons = new ImageView[]{sun_set_icon, sun_rise_icon, hour_View, weather_View, temp_View, pressure_View, humidity_View, wind_View};

        try {
            jsonArray = new JSONArray(jsonString);

        } catch (JSONException e) {
            loader.setVisibility(View.GONE);
            errorText.setVisibility(View.VISIBLE);
        }
        executeWeatherTask();


        addressButton.setOnClickListener(v -> dialog_menu.showMenuDialog());

        pullToRefresh.setOnRefreshListener(() -> {

            executeWeatherTask();
            pullToRefresh.setRefreshing(false);

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int currentFirstVisible = layoutManager.findFirstVisibleItemPosition();


                if (currentFirstVisible > firstVisibleInListview) {
                    try {
                        setForecastDate(currentFirstVisible);
                    } catch (JSONException e) {

                    }
                } else {
                    try {
                        setForecastDate(currentFirstVisible);
                    } catch (JSONException e) {

                    }
                }

                firstVisibleInListview = currentFirstVisible;
            }
        });
    }

    public void setForecastDate(int i) throws JSONException {
        String jsonString = sharedPreferences.getString("jsonArray", null);
        jsonArray = new JSONArray(jsonString);
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        long updatedAt = jsonObject.getLong("dt");
        String updatedAtText_date = new SimpleDateFormat("dd.MM EEEE", Locale.ENGLISH)
                .format(new Date(updatedAt * 1000));
        forecast_date.setText("Forecast for " + updatedAtText_date);
    }


    public void executeWeatherTask() {
        new CurrentWeatherTask().execute();
        new ForecastWeatherTask().execute();



    }

    public void setInitialData() throws JSONException {
        jsonString = sharedPreferences.getString("jsonArray", null);
        jArr = new JSONArray(jsonString);
        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();
        adapter = new Weather_recycler_adapter(this, weather_forecast);
        recyclerView.setAdapter(adapter);
        weather_forecast.clear();
        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jArr.getJSONObject(i);

                JSONObject main = jsonObject.getJSONObject("main");
                long updatedAt = jsonObject.getLong("dt");
                JSONObject wind = jsonObject.getJSONObject("wind");
                JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);


                String temp = Convert.tempString(main.getString("temp"));
                String updatedAtText_hour = new SimpleDateFormat("HH", Locale.ENGLISH)
                        .format(new Date(updatedAt * 1000));

                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");


                weather_forecast.add(new Weather_model(updatedAtText_hour, weatherDescription, temp, pressure, humidity, windSpeed));

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }


        }
    }

    void set_day_night_background(long updatedAt, long rise, long set) {
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (updatedAt > rise && updatedAt < set) {

            mainLayout.setBackgroundResource(R.drawable.bg_gradient_day);
            addressButton.setTextColor((getResources().getColor(R.color.blackTextColor)));
            setTextColor(textViews, icons, R.color.blackTextColor);
            editor.putBoolean("day", true);
            editor.apply();
        } else {
            mainLayout.setBackgroundResource(R.drawable.bg_gradient_night);
            addressButton.setTextColor((getResources().getColor(R.color.whiteColor)));
            setTextColor(textViews, icons, R.color.whiteColor);
            editor.putBoolean("day", false);
            editor.apply();
        }

    }

    public void setTextColor(TextView[] textView, ImageView[] imageView, Integer i) {
        for (TextView textViewTemp : textView) {
            textViewTemp.setTextColor(getResources().getColor(i));
        }
        for (ImageView imageViewTemp : imageView) {
            imageViewTemp.setColorFilter(ContextCompat.getColor(this, i));
        }

    }

    protected void onStart() {
        super.onStart();

    }

    protected void init() {
        sharedPreferences = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        dialog_menu = new Dialog_menu(sharedPreferences, MainActivity.this);


        mainLayout = findViewById(R.id.mainRelativeLayout);
        mainContainer = findViewById(R.id.mainContainer);
        addressButton = findViewById(R.id.address);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        updateTxt = findViewById(R.id.updated_at);
        loader = findViewById(R.id.loader);
        errorText = findViewById(R.id.errorText);
        forecast_date = findViewById(R.id.forecast_date);
        weatherView = findViewById(R.id.status);
        sunRiseView = findViewById(R.id.sunRise_Title);
        sunsetView = findViewById(R.id.sunset_Title);
        sun_rise_icon = findViewById(R.id.sun_rise_icon);
        sun_set_icon = findViewById(R.id.sun_set_icon);
        hour_View = findViewById(R.id.hour_view);
        weather_View = findViewById(R.id.weather_view);
        temp_View = findViewById(R.id.temp_view);
        pressure_View = findViewById(R.id.pressure_view);
        humidity_View = findViewById(R.id.hudimity_view);
        wind_View = findViewById(R.id.wind_view);
        recyclerView = findViewById(R.id.recycler_forecast_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void initChannels(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("1", NOTIF_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription("Channel description");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    protected void startNotifyIntent() {
        if (isMyServiceRunning(WeatherRenewService.class)) {
            stopService(new Intent(this, WeatherRenewService.class));
        }

        startService(new Intent(this, WeatherRenewService.class));

    }

    public class CurrentWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" +
                    sharedPreferences.getString("cityName", "Moscow") + "&units=metric&appid=" + API);
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


                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                String weatherType = weather.getString("description");

                String location = jsonObj.getString("name") + ", " + sys.getString("country");

                jsonString = jsonObj.toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("jsonObj", jsonString);
                editor.apply();


                long sunrise = sys.getLong("sunrise");
                long sunset = sys.getLong("sunset");
                long updatedAt = jsonObj.getLong("dt");

                String temp = Convert.tempString(main.getString("temp"));
                editor.putString("temp", temp);
                editor.apply();


                String tempMin = "Min Temp: " + Convert.tempString(main.getString("temp_min"));
                String tempMax = "Max Temp: " + Convert.tempString(main.getString("temp_max"));

                String updatedAtText = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
                        .format(new Date(updatedAt * 1000));

                updatedAtText_simplify = new SimpleDateFormat("HH:mm", Locale.ENGLISH)
                        .format(new Date(updatedAt * 1000));

                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(sunset * 1000)));
                weatherView.setText(weatherType.toUpperCase());
                updateTxt.setText(updatedAtText);


//                weatherBar.createNotificationChannel(temp,weatherType,location);
//                weatherBar.updateNotification(temp,weatherType);


                loader.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);

                set_day_night_background(updatedAt, sunrise, sunset);

                initChannels(MainActivity.this);
                startNotifyIntent();

            } catch (JSONException e) {
                loader.setVisibility(View.GONE);
                errorText.setVisibility(View.VISIBLE);
                mainContainer.setVisibility(View.GONE);
            }

        }


    }

    public class ForecastWeatherTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
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
                JSONObject jLocation = jsonResult.getJSONObject("city");// Here we have the forecast for every day
                // Here we have the forecast for every day
                JSONObject jsonObj = jArr.getJSONObject(0);
                jsonString = jArr.toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("jsonArray", jsonString);
                editor.apply();

                String jsonString = sharedPreferences.getString("jsonArray", null);
                String address = jLocation.getString("name") + ", " + jLocation.getString("country");
                addressButton.setText(address);

                try {
                    jsonArray = new JSONArray(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                setInitialData();
                setForecastDate(0);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }

}


