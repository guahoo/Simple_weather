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
import com.app.simpleweather.Utility.WeatherIconMap;
import com.app.simpleweather.Utility.WeatherRenewService;

import org.apache.commons.codec.binary.StringUtils;
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

import static com.app.simpleweather.Utility.SearchByGeoposition.CITY_NAME;

public class MainActivity extends AppCompatActivity {


    List<Weather_model> weather_forecast = new ArrayList<>();
    Button addressButton;
    String PREFERENCES = "";



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
    Dialog_menu dialog_menu;
    TextView[] textViews;
    ImageView[] icons;
    WeatherBar weatherBar;
    NotificationManager notificationManager;
    WeatherRenewService weatherRenewService;
    CurrentWeather currentWeather;
    String CITYNAME;
    SharedPreferences sharedPreferences;




    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("asdfasdf", Context.MODE_PRIVATE);


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
        String jsonString = sharedPreferences.getString("jsonArray", null);
        jsonArray = new JSONArray(jsonString);
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        long updatedAt = jsonObject.getLong("dt");

        String updatedAtText_date = new SimpleDateFormat("dd.MM EEEE",Locale.getDefault())
                .format(new Date(updatedAt * 1000)).toUpperCase();
        forecast_date.setText((updatedAtText_date));
    }


    public void executeWeatherTask() {
        currentWeather = new CurrentWeather(this);
        currentWeather.execute();


        new ForecastWeather(this).execute();


    }

    public void setInitialData() throws JSONException {
        jsonString = sharedPreferences.getString("jsonArray", null);
        jArr = new JSONArray(jsonString);
        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();
        adapter = new Weather_recycler_adapter(this, weather_forecast);
        recyclerView.setAdapter(adapter);
        weather_forecast.clear();

        for (int i = 0; i < jArr.length(); i++) {
            JSONObject jsonObject;
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
                int windSpeedConverting=(int)Double.parseDouble(wind.getString("speed"));
                String windSpeed =String.valueOf(windSpeedConverting)+" ";
                int windDirection =Integer.parseInt(wind.getString("deg"));
                String weatherDescription = weather.getString("description");


                weather_forecast.add(new Weather_model(updatedAtText_hour, weatherDescription, temp, pressure, humidity, windSpeed,windDirection));

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
        currentWeatherStatusView.setImageResource(weather_type_set_icon(currentWeather.getWeatherType()));
        updateTxt.setText(currentWeather.getUpdatedAtText());
      //  CITYNAME=sharedPreferences.getString("city_name","BABRYISK");
        addressButton.setText(sharedPreferences.getString(CITY_NAME,"BABRYISK"));
        setButtonTextSize();


        //addressButton.setTextSize();
        windTextView.setText(currentWeather.windSpeed + currentWeather.windDirection);

        loader.setVisibility(View.GONE);
        mainContainer.setVisibility(View.VISIBLE);
        addressButton.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);


    }

    private void setButtonTextSize() {
        int spaces = CITYNAME == null ? 0 : CITYNAME.replaceAll("[^ ]", "").length();
        if (spaces>=2)addressButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
    }

    protected void set_day_night_background(long updatedAt, long rise, long set) {
        boolean dayNight = updatedAt > rise && updatedAt < set;
        setBackgroundGradientColor(dayNight);

        if (updatedAt > rise && updatedAt < set) {
            setDayNightColor(R.drawable.bg_gradient_day,R.color.blackTextColor,R.color.blackTextColor,true);

        } else {
             setDayNightColor(R.drawable.bg_gradient_night,R.color.whiteColor,R.color.whiteColor,false);
        }
    }

    private void setBackgroundGradientColor(boolean dayNight){
        int backGroundColor = dayNight ? R.drawable.bg_gradient_day : R.drawable.bg_gradient_night;
        mainLayout.setBackgroundResource(backGroundColor);
    }

    private void setDayNightColor(int backGroundColor,int buttonColor,int textViewColor, boolean dayNight) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        mainLayout.setBackgroundResource(backGroundColor);
        addressButton.setTextColor((getResources().getColor(buttonColor)));
        setTextImageViewsColor(textViews, icons, textViewColor);
        editor.putBoolean("day", dayNight);
        editor.apply();
    }

    public void setTextImageViewsColor(TextView[] textView, ImageView[] imageView, Integer i) {
        for (TextView textViewTemp : textView) {
            textViewTemp.setTextColor(getResources().getColor(i));
        }
        for (ImageView imageViewTemp : imageView) {
            imageViewTemp.setColorFilter(ContextCompat.getColor(this, i));
        }

    }

    public void soWeGotException() {
        loader.setVisibility(View.GONE);
        mainContainer.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
        addressButton.setText(getResources().getString(R.string.your_city));
        dialog_menu.showMenuDialog();

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
            channel = new NotificationChannel("1", NOTIF_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription("Weather channel");
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
    public int weather_type_set_icon(String weather_model) {
        return WeatherIconMap.weather_icons_map.get(WeatherIconMap.weather_icons_map.containsKey(weather_model) ? weather_model : "");

    }
}
