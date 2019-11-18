package com.example.simpleweather.Utility;

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
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;
import com.example.simpleweather.MainActivity;
import com.example.simpleweather.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class WeatherRenewService extends Service {
    private static final int NOTIF_ID = 1;
    private static Map<String, Integer> weather_icons_map = new HashMap<>();

    static {
        weather_icons_map.put("overcast clouds", R.drawable.ic_cloud);
        weather_icons_map.put("clear sky", R.drawable.ic_sun);
        weather_icons_map.put("shower rain", R.drawable.ic_rain);
        weather_icons_map.put("rain", R.drawable.ic_rain);
        weather_icons_map.put("light rain", R.drawable.ic_rain_alt_sun);
        weather_icons_map.put("scattered clouds", R.drawable.ic_cloud);
        weather_icons_map.put("few clouds", R.drawable.ic_cloud);
        weather_icons_map.put("broken clouds", R.drawable.ic_cloud_sun);
        weather_icons_map.put("snow", R.drawable.ic_snow_alt);
        weather_icons_map.put("light intensity drizzle", R.drawable.ic_cloud_sun);
        weather_icons_map.put("mist",R.drawable.ic_fog);
        weather_icons_map.put("fog",R.drawable.ic_fog);
        weather_icons_map.put("", R.drawable.ic_umbrella);
    }

    public String PREFERENSES;
    Context context;
    String NOTIF_CHANNEL_ID = "1";
    RemoteViews remoteViews;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    String API = "b542736e613d2382837ad821803eb507";
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    SharedPreferences sPrefs;
    Toast toast;
    public Timer  time = new Timer();


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
        new CurrentWeatherTask().execute();
        toast = Toast.makeText(getApplicationContext(),
                "Пора покормить кота!", Toast.LENGTH_LONG);

        runWeatherRenewTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground("15", "vorkuta", "snow");

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

    private void startForeground(String temp, String city, String s) {

//        getMyActivityNotification(temp,city,s);
        startForeground(NOTIF_ID, getMyActivityNotification("", "", ""));
    }

    private Notification getMyActivityNotification(String temp, String city, String s) {
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class), 0);
        remoteViews.setTextViewText(R.id.forecastView_notificationBar, temp);
        remoteViews.setTextViewText(R.id.location_notificationBar, city);
        remoteViews.setImageViewResource(R.id.weatherImage, weather_type_set_icon(s));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(context)
                    .setSmallIcon(weather_type_set_icon(s))
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

    public void updateNotification(String temp, String city, String s) {
        Notification notification = null;

        notification = getMyActivityNotification(temp, city, s);

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Eggz";
            String description = "EggzNotificationChannel";
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
//        remoteViews.setTextViewText(R.id.forecastView_notificationBar,s);

    }


    public  void runWeatherRenewTask() {

        time.schedule(new DisplayToastTimerTask(), 0, 1000 * 60 * 60);
    }

    public void stopWeatherRenewTask(){
        time.cancel();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
    }

    public int weather_type_set_icon(String weather_model) {
        return weather_icons_map.get(weather_icons_map.containsKey(weather_model) ? weather_model : "");

    }

    private class DisplayToastTimerTask extends TimerTask {
        @Override
        public void run() {
            new CurrentWeatherTask().execute();
            toast.show();
        }
    }



    //    private void startMyOwnForeground(){
//        String NOTIFICATION_CHANNEL_ID = "1";
//        String channelName = NOTIF_CHANNEL_ID;
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ikonka)
//                .setContentTitle("App is running in background")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        startForeground(2, notification);
//    }
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
                String temp = Convert.tempString(main.getString("temp"));

                getMyActivityNotification(temp, weatherType, location);
                updateNotification(temp, location, weatherType);


            } catch (JSONException e) {

            }

        }
    }


}





