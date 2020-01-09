package com.app.simpleweather.Utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;
import com.app.simpleweather.MainActivity;
import com.app.simpleweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static com.app.simpleweather.ForecastWeather.LIST;
import static com.app.simpleweather.Utility.OftenUsedStrings.CITY_NAME;
import static com.app.simpleweather.Utility.OftenUsedStrings.DESCRIPTION;
import static com.app.simpleweather.Utility.OftenUsedStrings.LATITUDE;
import static com.app.simpleweather.Utility.OftenUsedStrings.LONGITUDE;
import static com.app.simpleweather.Utility.OftenUsedStrings.MAIN;
import static com.app.simpleweather.Utility.OftenUsedStrings.NO_SIGNAL;
import static com.app.simpleweather.Utility.OftenUsedStrings.OPEN_WEATHER_MAP_API_KEY;
import static com.app.simpleweather.Utility.OftenUsedStrings.TEMP;
import static com.app.simpleweather.Utility.OftenUsedStrings.URL_REQUEST_OPEN_WEATHER_MAP_CURRENT_WEATHER;
import static com.app.simpleweather.Utility.OftenUsedStrings.URL_REQUEST_OPEN_WEATHER_MAP_FORECAST_ALERT;
import static com.app.simpleweather.Utility.OftenUsedStrings.WEATHER;
import static com.app.simpleweather.Utility.OftenUsedStrings.WIND;
import static com.app.simpleweather.Utility.OftenUsedStrings.WINDSPEED;
import static com.app.simpleweather.Utility.WeatherIconMap.getResourceIdent;

@SuppressWarnings("deprecation")
public class WeatherRenewService extends Service {
    private static final int NOTIF_ID = 1;
    private static final CharSequence CHANNEL_NAME = "weather_service";
    private static final String WEATHER_RENEW_CHANNEL_DESCRIPRION = "Weather_channel_notification";
    private static final String ALERT_SIGNALS = "rain|shower rain|light rain|snow|light snow";
    private static final String CHECK_CONNECTION = "Проверьте подключение к интернету";

    public String PREFERENSES;
    Context context;
    String NOTIF_CHANNEL_ID = "1";
    RemoteViews remoteViews;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    SharedPreferences sPrefs;
    Toast toast;
    public Timer time = new Timer();
    String weatherDescription;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_notification_bar);
        notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);


        sPrefs = getApplicationContext().getSharedPreferences(PREFERENSES, MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        new ForecastWeather(context).execute();
        toast = Toast.makeText(getApplicationContext(),
                "Пора покормить кота!", Toast.LENGTH_LONG);

        runWeatherRenewTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroy() {
    }

    private void startForeground() {
        startForeground(NOTIF_ID, getMyActivityNotification("", "", ""));
    }

    public Notification getMyActivityNotification(String temp, String location, String weatherType) {
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class), 0);
        remoteViews.setTextViewText(R.id.forecastView_notificationBar, temp);
        remoteViews.setTextViewText(R.id.location_notificationBar, location);
        remoteViews.setImageViewResource(R.id.weatherImage, getResourceIdent(weatherType));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(context)

                    .setSmallIcon(getResourceIdent(weatherType))
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setChannelId(NOTIF_CHANNEL_ID)
                    .setContentIntent(contentIntent).getNotification();

        } else {

            return new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_sun)
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setContentIntent(contentIntent).getNotification();
        }


    }

    public void updateNotification(String temp, String location, String weatherType) {
        Notification notification;

        notification = getMyActivityNotification(temp, location, weatherType);

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = WEATHER_RENEW_CHANNEL_DESCRIPRION;
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }


        if (mNotificationManager != null) {
            mNotificationManager.notify(1, notification);
        }


    }


    public void runWeatherRenewTask() {

        time.schedule(new DisplayToastTimerTask(), 0, 1000 * 60 );
    }

    public void stopWeatherRenewTask() {
        time.cancel();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
    }

    private class DisplayToastTimerTask extends TimerTask {
        @Override
        public void run() {
            new ForecastWeather(context).execute();
            //  toast.show();
        }
    }


    public class CurrentWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            String cityLat = sharedPreferences.getString(LATITUDE, null);
            String cityLon = sharedPreferences.getString(LONGITUDE, null);

            return HttpRequest.excuteGet(String.format(URL_REQUEST_OPEN_WEATHER_MAP_CURRENT_WEATHER,
                    cityLat, cityLon, OPEN_WEATHER_MAP_API_KEY));
        }

        @Override
        public void onPostExecute(String result) {

            if (result == null) {
                soWeGotException();
            }

            try {
                assert result != null;
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject(MAIN);
                JSONObject weather = jsonObj.getJSONArray(WEATHER).getJSONObject(0);
                String temp = Convert.tempString(main.getString(TEMP));
                String location = sharedPreferences.getString(CITY_NAME, null);


                String weatherType = weather.getString(DESCRIPTION);
                Pattern pattern = Pattern.compile(getResources().getString(R.string.alertWeatherType));
                Matcher matcher = pattern.matcher(weatherDescription);
                if (matcher.matches()) {
                    getMyActivityNotification(temp, location, matcher.group(0));
                    updateNotification(temp, location, matcher.group(0));
                } else {
                    getMyActivityNotification(temp, location, weatherType);
                    updateNotification(temp, location, weatherType);
                }


            } catch (JSONException e) {
                soWeGotException();


            }


        }
    }

    private void soWeGotException() {
        updateNotification(NO_SIGNAL, CHECK_CONNECTION, null);

    }

    public class ForecastWeather extends AsyncTask<String, Void, String> {
        private static final String ALERT = "_alert";
        JSONArray jArr;
        SharedPreferences sharedPreferences;
        String PREFERENCES;
        Context context;
        String windSpeed;


        ForecastWeather(Context context) {
            this.context = context;
        }




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sharedPreferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String cityLat = sharedPreferences.getString(LATITUDE, null);
            String cityLon = sharedPreferences.getString(LONGITUDE, null);

            return HttpRequest.excuteGet(String.format(URL_REQUEST_OPEN_WEATHER_MAP_FORECAST_ALERT, cityLat, cityLon, OPEN_WEATHER_MAP_API_KEY));
        }

        @Override
        public void onPostExecute(String result) {
            if (result == null) {
                soWeGotException();
            }

            try {
                assert result != null;
                JSONObject jsonResult = new JSONObject(result);
                jArr = jsonResult.getJSONArray(LIST);


            } catch (Exception e) {
                soWeGotException();

            }

            JSONObject jsonObject;
            try {
                jsonObject = jArr.getJSONObject(0);
                JSONObject wind = jsonObject.getJSONObject(WIND);
                JSONObject weather = jsonObject.getJSONArray(WEATHER).getJSONObject(0);


                windSpeed = wind.getString(WINDSPEED);
                weatherDescription = weather.getString(DESCRIPTION);


                if (weatherDescription.matches(ALERT_SIGNALS)) {
                    weatherDescription = weatherDescription + ALERT;
                }

                assyncHandler.sendEmptyMessage(0);

            } catch (Exception e) {
                soWeGotException();
            }


        }
    }

    Handler assyncHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    new CurrentWeatherTask().execute();

                    break;
                default:
                    break;
            }
        }
    };


}






