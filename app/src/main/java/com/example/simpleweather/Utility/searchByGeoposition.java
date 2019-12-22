package com.example.simpleweather.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;
import com.example.simpleweather.NominativeConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class searchByGeoposition extends AsyncTask<String, Void, String> {

    protected NominativeConnect nominativeConnect;
    SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Context context;
    final static String API = "835b77b309444de689cd7c07b675493e";
    final static String URL_REQUEST_FORECAST = "https://api.opencagedata.com/geocode/v1/json?key=" + API + "&q=%s" + "," + "%s&pretty=5" +
            "&no_annotations=1&language=%s";
    final static String RESULTS = "results";
    final static String CITY = "city";
    final static String VILLAGE = "village";
    final static String HAMLET = "hamlet";
    final static String FORMATTED = "formatted";
    final static String COMPONENTS = "components";
    final static String COMMA = ",";
    final static String LOCATION= "Ваше местоположение";
    final static String NO_SIGNAL= "Нет сети";
    final static String CITY_LAT= "cityLat";
    final static String CITY_lON= "cityLon";
    final static String CITY_NAME= "city_name";

    private JSONObject placeInfo;
    private String placeName;



    public searchByGeoposition(Context context, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        nominativeConnect = new NominativeConnect();
        editor = sharedPreferences.edit();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... strings) {


//        String latitude = GeoLocationFinder.getLatitude();
//        String longitude =GeoLocationFinder.getLongitude();

        String latitude = GeoLocationFinder.getLatitude();
        String longitude = GeoLocationFinder.getLongitude();
        String language = Locale.getDefault().getDisplayLanguage();


        return HttpRequest.excuteGet(String.format(URL_REQUEST_FORECAST, latitude, longitude, language));

    }

    @Override
    public void onPostExecute(String result) {

        try {
            JSONArray results = new JSONObject(result).getJSONArray(RESULTS);
            placeInfo = results.getJSONObject(0);

            extractPlaceName(placeInfo.getJSONObject(COMPONENTS));

            Dialog_menu.weHaveGeoPosition = true;
            Dialog_menu.getCity.setText(placeName);
            saveCityAndCoords(placeName, GeoLocationFinder.getLatitude(), GeoLocationFinder.getLongitude());
            Dialog_menu.setLoaderVisibility();
        } catch (JSONException e) {
            prepareJsonExeption();
        } catch (NullPointerException nE) {
            Dialog_menu.getCity.setText(NO_SIGNAL);
        } finally {
            Dialog_menu.weHaveGeoPosition = false;
        }
    }

    private void extractPlaceName(JSONObject components) throws JSONException {
        if (components.has(CITY)) {
            placeName = components.getString(CITY);
        } else if (components.has(VILLAGE)) {
            placeName = components.getString(VILLAGE);
        } else if (components.has(HAMLET)) {
            placeName = components.getString(HAMLET);
        } else {
            placeName = placeInfo.getString(FORMATTED).split(COMMA)[0];
        }
    }

    private void prepareJsonExeption() {
        Dialog_menu.getCity.setText(LOCATION);
        if (placeInfo != null) {
            saveCityAndCoords(LOCATION, GeoLocationFinder.getLatitude(), GeoLocationFinder.getLongitude());
            Dialog_menu.setLoaderVisibility();
        }
    }

    protected void saveCityAndCoords(String city, String lat, String lon) {
        editor.putString(CITY_NAME, city);
        editor.putString(CITY_LAT, lat);
        editor.putString(CITY_lON, lon);
        editor.apply();
    }

}