package com.app.simpleweather.Utility;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.app.simpleweather.MainActivity.LATITUDE;
import static com.app.simpleweather.MainActivity.LONGITUDE;
import static com.app.simpleweather.Utility.Dialog_menu.weHaveGeoPosition;


public class SearchByGeoposition extends AsyncTask<String, Void, String> {
    private static final String STATE = "state";
    private static final String TOWN = "town" ;

    private SharedPreferences.Editor editor;
    private final static String OPENSAGEDATA_API = OftenUsedStrings.OPENSAGEDATA_API.getOftenUsedString();
    private final static String RESULTS = OftenUsedStrings.RESULTS.getOftenUsedString();
    private final static String CITY = OftenUsedStrings.CITY.getOftenUsedString();
    private final static String VILLAGE = OftenUsedStrings.VILLAGE.getOftenUsedString();
    private final static String HAMLET = OftenUsedStrings.HAMLET.getOftenUsedString();
    private final static String FORMATTED = OftenUsedStrings.FORMATTED.getOftenUsedString();
    private final static String COMPONENTS = OftenUsedStrings.COMPONENTS.getOftenUsedString();
    public final static String COMMA = OftenUsedStrings.COMMA.getOftenUsedString();
    public final static String NO_SIGNAL = OftenUsedStrings.NO_SIGNAL.getOftenUsedString();
    public final static String CITY_NAME = OftenUsedStrings.CITY_NAME.getOftenUsedString();
    private final static String COUNTY = OftenUsedStrings.COUNTY.getOftenUsedString();
    private final static String LOCATION = OftenUsedStrings.LOCATION.getOftenUsedString();
    private String latitude;
    private String longitude;

    private JSONObject placeInfo;
    private String placeName;
    private final static String URL_REQUEST_FORECAST = "https://api.opencagedata.com/geocode/v1/json?key="
            + OPENSAGEDATA_API + "&q=%s" + COMMA + "%s&pretty=5" + "&no_annotations=1&language=%s";


    SearchByGeoposition(SharedPreferences sharedPreferences) {
        editor = sharedPreferences.edit();
    }


    @Override
    protected String doInBackground(String... strings) {

        latitude = getLatitude();
        longitude = getLongitude();

//        latitude = "54.611007";
//        longitude = "39.715388";
        String language = Locale.getDefault().getLanguage();

        return HttpRequest.excuteGet(String.format(URL_REQUEST_FORECAST, latitude, longitude, language));
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
        } finally {
            Dialog_menu.getCity.setText(location);
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
        }
            else if (components.has(STATE)) {
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

    void printHelloKitty(boolean except) {
        if (except) {
            throw new NullPointerException("asdf");
        }
        System.out.println("Hello Kitty");
    }

}