package com.app.simpleweather.Utility;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


import static com.app.simpleweather.Utility.Dialog_menu.weHaveGeoPosition;
import static com.app.simpleweather.Utility.OftenUsedStrings.CITY;
import static com.app.simpleweather.Utility.OftenUsedStrings.CITY_NAME;
import static com.app.simpleweather.Utility.OftenUsedStrings.COMMA;
import static com.app.simpleweather.Utility.OftenUsedStrings.COMPONENTS;
import static com.app.simpleweather.Utility.OftenUsedStrings.COUNTY;
import static com.app.simpleweather.Utility.OftenUsedStrings.FORMATTED;
import static com.app.simpleweather.Utility.OftenUsedStrings.HAMLET;
import static com.app.simpleweather.Utility.OftenUsedStrings.LATITUDE;
import static com.app.simpleweather.Utility.OftenUsedStrings.LOCATION;
import static com.app.simpleweather.Utility.OftenUsedStrings.LONGITUDE;
import static com.app.simpleweather.Utility.OftenUsedStrings.NO_SIGNAL;
import static com.app.simpleweather.Utility.OftenUsedStrings.OPENSAGEDATA_API;
import static com.app.simpleweather.Utility.OftenUsedStrings.RESULTS;
import static com.app.simpleweather.Utility.OftenUsedStrings.STATE;
import static com.app.simpleweather.Utility.OftenUsedStrings.TOWN;
import static com.app.simpleweather.Utility.OftenUsedStrings.URL_REQUEST_OPENSAGE_GEOPOSITION;
import static com.app.simpleweather.Utility.OftenUsedStrings.VILLAGE;


public class SearchByGeoposition extends AsyncTask<String, Void, String> {

    private SharedPreferences.Editor editor;
    private String latitude;
    private String longitude;

    private JSONObject placeInfo;
    private String placeName;



    SearchByGeoposition(SharedPreferences sharedPreferences) {
        editor = sharedPreferences.edit();
    }


    @Override
    protected String doInBackground(String... strings) {

        latitude = getLatitude();
        longitude = getLongitude();

//        latitude = "55.279280";
//        longitude = "38.776972";
        String language = Locale.getDefault().getLanguage();
        //String language = "ru";

        return HttpRequest.excuteGet(String.format(URL_REQUEST_OPENSAGE_GEOPOSITION, latitude, longitude, language));
    }

    protected String getLatitude() {
        return GeoLocationFinder.getLatitude();
    }

    protected String getLongitude() {
        return GeoLocationFinder.getLongitude();
    }

    @Override
    public void onPostExecute(String result) {

        String location = LOCATION;
        try {
            JSONArray results = new JSONObject(result).getJSONArray(RESULTS);
            placeInfo = results.getJSONObject(0);

            extractPlaceName(placeInfo.getJSONObject(COMPONENTS));

            weHaveGeoPosition = true;
            saveCityAndCoords(placeName, latitude, longitude);
            Dialog_menu.setLoaderVisibility();
            location = placeName;
        } catch (JSONException e) {
            saveCityAndCoords(LOCATION, latitude, longitude);
            Dialog_menu.setLoaderVisibility();

        } catch (NullPointerException nE) {
            location = NO_SIGNAL;
            Dialog_menu.setLoaderVisibility();
            weHaveGeoPosition = false;
        } finally {
            Dialog_menu.setTextCityName(location);
            Dialog_menu.setLoaderVisibility();
            weHaveGeoPosition = false;
        }
    }

    private void extractPlaceName(JSONObject components) throws JSONException {
        if (components.has(CITY)) {
            placeName = components.getString(CITY);
        } else if (components.has(VILLAGE)) {
            placeName = components.getString(VILLAGE);
        } else if (components.has(HAMLET)) {
            placeName = components.getString(HAMLET);
        } else if (components.has(COUNTY)) {
            placeName = components.getString(COUNTY);
        } else if (components.has(TOWN)) {
            placeName = components.getString(TOWN);
        } else if (components.has(STATE)) {
            placeName = components.getString(STATE);
        } else {
            placeName = placeInfo.getString(FORMATTED).split(COMMA)[0];
        }
    }

    private void saveCityAndCoords(String city, String lat, String lon) {
        editor.putString(CITY_NAME, city);
        editor.putString(LATITUDE, lat);
        editor.putString(LONGITUDE, lon);
        editor.apply();
    }


}