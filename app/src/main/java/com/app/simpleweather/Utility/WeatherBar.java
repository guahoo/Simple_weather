package com.app.simpleweather.Utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.RemoteViews;

import com.app.simpleweather.MainActivity;
import com.app.simpleweather.R;

import java.util.HashMap;
import java.util.Map;


public class WeatherBar {

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
        weather_icons_map.put("", R.drawable.ic_umbrella);
    }

    Context context;
    String NOTIF_CHANNEL_ID = "1";
    RemoteViews remoteViews;
    SharedPreferences sharedPreferences;
    String PREFERENCES;
    NotificationManager notificationManager;

    public WeatherBar(Context context) {
        this.context = context;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_notification_bar);
        notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);


    }


    public Notification getMyActivityNotification(String s) {
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class), 0);

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


    public void updateNotification(String s, String weatherType) {
        Notification notification = null;

        notification = getMyActivityNotification(weatherType);

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
        remoteViews.setTextViewText(R.id.forecastView_notificationBar, s);

    }

    public void createNotificationChannel(String s, String weathertype, String location) {
        // Create the NotificationChannel, but only on OPEN_WEATHER_MAP_API_KEY 26+ because
        // the NotificationChannel class is new and not in the support library
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

        remoteViews.setTextViewText(R.id.forecastView_notificationBar, s);
        remoteViews.setImageViewResource(R.id.weatherImage, weather_type_set_icon(weathertype));
        remoteViews.setTextViewText(R.id.location_notificationBar, location);


    }

    public int weather_type_set_icon(String weather_model) {

        return weather_icons_map.get(weather_icons_map.containsKey(weather_model) ? weather_model : "");

//                return (weather_icons_map.get(weather_model));

    }


}
