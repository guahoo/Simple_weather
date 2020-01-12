package com.app.simpleweather;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.simpleweather.Utility.Convert;
import com.app.simpleweather.Utility.Dialog_menu;
import com.app.simpleweather.Utility.WeatherBar;
import com.app.simpleweather.Utility.WeatherRenewService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.app.simpleweather.Utility.OftenUsedStrings.CHANNEL_ID;
import static com.app.simpleweather.Utility.OftenUsedStrings.CHANNEL_NAME;
import static com.app.simpleweather.Utility.OftenUsedStrings.CITY_NAME;
import static com.app.simpleweather.Utility.OftenUsedStrings.DEG;
import static com.app.simpleweather.Utility.OftenUsedStrings.DESCRIPTION;
import static com.app.simpleweather.Utility.OftenUsedStrings.DT;
import static com.app.simpleweather.Utility.OftenUsedStrings.HUDIMITY;
import static com.app.simpleweather.Utility.OftenUsedStrings.ISADAY;
import static com.app.simpleweather.Utility.OftenUsedStrings.JSONARRAY;
import static com.app.simpleweather.Utility.OftenUsedStrings.MAIN;
import static com.app.simpleweather.Utility.OftenUsedStrings.PRESSURE;
import static com.app.simpleweather.Utility.OftenUsedStrings.TEMP;
import static com.app.simpleweather.Utility.OftenUsedStrings.WEATHER;
import static com.app.simpleweather.Utility.OftenUsedStrings.WIND;
import static com.app.simpleweather.Utility.OftenUsedStrings.WINDSPEED;

import static com.app.simpleweather.Utility.WeatherIconMap.getResourceIdent;

public class MainActivity extends AppCompatActivity {




    List<Weather_model> weather_forecast = new ArrayList<>();
    Button addressButton;
    String PREFERENCES;



    JSONArray jArr;
    private static final String NOTIF_CHANNEL_ID = "1";
    private static int firstVisibleInListview;
    TextView tempTxt,  sunriseTxt,
            sunsetTxt, updateTxt, forecast_date, errorText, windTextView;
    ImageView sun_rise_icon, currentWeatherStatusView, sun_set_icon, hour_View, weather_View, temp_View, pressure_View, humidity_View, wind_View,current_Wind_View;


    ProgressBar loader;
    String jsonString;
    JSONArray jsonArray;
    LinearLayoutManager layoutManager;
    Weather_recycler_adapter adapter;
    RecyclerView recyclerView;
    RelativeLayout mainLayout, mainContainer;
    SwipeRefreshLayout pullToRefresh;
    volatile Dialog_menu dialog_menu;
    TextView[] textViews;
    ImageView[] icons;
    WeatherBar weatherBar;
    NotificationManager notificationManager;
    WeatherRenewService weatherRenewService;
    CurrentWeather currentWeather;

    SharedPreferences sharedPreferences;
    boolean dialog_showing;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);


        init();
        currentWeather = new CurrentWeather(this);




        notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);








        addressButton.setOnClickListener(v -> dialog_menu.showMenuDialog());

        pullToRefresh.setOnRefreshListener(() -> {
            currentWeather=new CurrentWeather(this);
            currentWeather.execute();

            new ForecastWeather(this).execute();

            pullToRefresh.setRefreshing(false);

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int currentFirstVisible = layoutManager.findFirstVisibleItemPosition();
                if (currentFirstVisible > firstVisibleInListview) {
                    try {
                        setForecastDate(currentFirstVisible);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else {
                    try {
                        setForecastDate(currentFirstVisible);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
                firstVisibleInListview = currentFirstVisible;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (sharedPreferences.getAll().isEmpty()) {
            dialog_menu.showMenuDialog();
        }else {
            executeWeatherTask();
        }
    }

    public void setForecastDate(int i) throws JSONException {
        String jsonString = sharedPreferences.getString(JSONARRAY, null);
        jsonArray = new JSONArray(jsonString);
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        long updatedAt = jsonObject.getLong(DT);
        //TODO formatter

        String updatedAtText_date = new SimpleDateFormat("dd.MM EEEE",Locale.getDefault())
                .format(new Date(updatedAt * 1000)).toUpperCase();
        forecast_date.setText((updatedAtText_date));
    }


    public void executeWeatherTask() {
        currentWeather = new CurrentWeather(this);
        currentWeather.execute();


        new ForecastWeather(this).execute();


    }

    public void setForecastInitialData() throws JSONException {
        jsonString = sharedPreferences.getString(JSONARRAY, null);
        jArr = new JSONArray(jsonString);
        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();
        adapter = new Weather_recycler_adapter(this, weather_forecast);
        recyclerView.setAdapter(adapter);
        weather_forecast.clear();

        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jArr.getJSONObject(i);

                JSONObject main = jsonObject.getJSONObject(MAIN);
                long updatedAt = jsonObject.getLong(DT);
                JSONObject wind = jsonObject.getJSONObject(WIND);
                JSONObject weather = jsonObject.getJSONArray(WEATHER).getJSONObject(0);


                String temp = Convert.tempString(main.getString(TEMP));
                //TODO formatter

                String updatedAtText_hour = new SimpleDateFormat("HH", Locale.ENGLISH)
                        .format(new Date(updatedAt * 1000));

                String pressure = main.getString(PRESSURE);
                String humidity = main.getString(HUDIMITY);
                int windSpeedConverting=(int)Double.parseDouble(wind.getString(WINDSPEED));
                String windSpeed =String.valueOf(windSpeedConverting)+" ";
                int windDirection =Integer.parseInt(wind.getString(DEG));
                String weatherDescription = weather.getString(DESCRIPTION);


                weather_forecast.add(new Weather_model(updatedAtText_hour, weatherDescription,
                        temp, pressure, humidity, windSpeed,windDirection));

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }


        }
    }

    public void setCurrentWeatherData() {
        tempTxt.setText(currentWeather.getTemp());
        //TODO сделать через форматер
        sunriseTxt.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(currentWeather.getSunrise() * 1000)));
        sunsetTxt.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(currentWeather.getSunset() * 1000)));
        currentWeatherStatusView.setImageResource(getResourceIdent(currentWeather.getWeatherType()));
        updateTxt.setText(currentWeather.getUpdatedAtText());
        addressButton.setText(sharedPreferences.getString(CITY_NAME,null));
        setButtonTextSize();
        windTextView.setText(String.format("%s%s", currentWeather.windSpeed, currentWeather.windDirection));
        loader.setVisibility(View.GONE);
        mainContainer.setVisibility(View.VISIBLE);
        addressButton.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    private void setButtonTextSize() {
        String displayedCityName=sharedPreferences.getString(CITY_NAME,null);
        int spaces = displayedCityName == null ? 0 : displayedCityName.length();
        if (spaces>=20)addressButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
    }

    protected void set_day_night_background(long updatedAt, long rise, long set) {
        boolean dayNight = updatedAt > rise && updatedAt < set;
        setBackgroundGradientColor(dayNight);
        setImageViewsColor(dayNight);
        setTextViewsColor(dayNight);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ISADAY, dayNight);
        editor.apply();
    }

    private void setTextViewsColor(boolean dayNight) {
        int textViewsColor = dayNight ? R.color.blackTextColor : R.color.whiteColor;
        for (TextView textViewTemp : textViews) {
            textViewTemp.setTextColor(getResources().getColor(textViewsColor));
        }
        addressButton.setTextColor((getResources().getColor(textViewsColor)));

    }

    private void setImageViewsColor(boolean dayNight) {
        int imagesColor = dayNight ? R.color.blackTextColor : R.color.whiteColor;
        for (ImageView imageViewTemp : icons) {
            imageViewTemp.setColorFilter(ContextCompat.getColor(this,imagesColor));
        }
    }

    private void setBackgroundGradientColor(boolean dayNight){
        int backGroundColor = dayNight ? R.drawable.bg_gradient_day : R.drawable.bg_gradient_night;
        mainLayout.setBackgroundResource(backGroundColor);
    }





    protected void init() {

        pullToRefresh = findViewById(R.id.pullToRefresh);
        dialog_menu = new Dialog_menu(sharedPreferences, MainActivity.this);
        mainLayout = findViewById(R.id.mainRelativeLayout);
        mainContainer = findViewById(R.id.mainContainer);
        addressButton = findViewById(R.id.address);
        tempTxt = findViewById(R.id.temp);


        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        updateTxt = findViewById(R.id.updated_at);
        loader = findViewById(R.id.loader);
        errorText = findViewById(R.id.errorText);
        forecast_date = findViewById(R.id.forecast_date);
        currentWeatherStatusView = findViewById(R.id.weather_Status);

        sun_rise_icon = findViewById(R.id.sun_rise_icon);
        sun_set_icon = findViewById(R.id.sun_set_icon);
        current_Wind_View=findViewById(R.id.currentWindView);
        hour_View = findViewById(R.id.hour_view);
        weather_View = findViewById(R.id.weather_view);
        temp_View = findViewById(R.id.temp_view);
        windTextView =findViewById(R.id.windDirectionTextView);
        pressure_View = findViewById(R.id.pressure_view);
        humidity_View = findViewById(R.id.hudimity_view);
        wind_View = findViewById(R.id.wind_view);
        recyclerView = findViewById(R.id.recycler_forecast_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        weatherRenewService = new WeatherRenewService();



        loader.setVisibility(View.VISIBLE);
        mainContainer.setVisibility(View.INVISIBLE);

        textViews = new TextView[]{
                tempTxt, sunriseTxt,
                sunsetTxt, updateTxt, forecast_date, errorText,
                windTextView
        };
        addressButton.setText(getResources().getString(R.string.please_wait));


        icons = new ImageView[]{sun_set_icon, sun_rise_icon, currentWeatherStatusView,hour_View,
                weather_View, temp_View, pressure_View, humidity_View, wind_View,current_Wind_View};
        weatherBar = new WeatherBar(getApplicationContext());

    }

    public void initChannels(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, NOTIF_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(CHANNEL_NAME);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WeatherRenewService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startNotifyIntent() {
        if (isMyServiceRunning()) {
            stopService(new Intent(this, WeatherRenewService.class));
            weatherRenewService.stopWeatherRenewTask();
        }
        startService(new Intent(this, WeatherRenewService.class));
    }

    public void soWeGotException() {
        loader.setVisibility(View.GONE);
        mainContainer.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
        addressButton.setText(getResources().getString(R.string.your_city));



        if (dialog_showing){
            dialog_menu.hideMenuDialog();
            show_Hide_menu_Dialog_Window(false);
        }
        dialog_menu.showMenuDialog();
        show_Hide_menu_Dialog_Window(true);

    }

    void show_Hide_menu_Dialog_Window(boolean bool){
        dialog_showing =bool;

    }


}
