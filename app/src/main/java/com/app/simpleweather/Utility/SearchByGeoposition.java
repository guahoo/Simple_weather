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


public class SearchByGeoposition extends AsyncTask<String, Void, String> {

    private SharedPreferences.Editor editor;
    private final static String OPENSAGEDATA_API = "835b77b309444de689cd7c07b675493e";
    private final static String RESULTS = "results";
    private final static String CITY = "city";
    private final static String VILLAGE = "village";
    private final static String HAMLET = "hamlet";
    private final static String FORMATTED = "formatted";
    private final static String COMPONENTS = "components";
    public final static String COMMA = ",";
    private final static String LOCATION= "Ваше местоположение";
    private final static String NO_SIGNAL= "Нет сети";
    public final static String CITY_NAME= "city_name";
    private final static String COUNTY= "county";
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
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... strings) {


        latitude = GeoLocationFinder.getLatitude();
        longitude = GeoLocationFinder.getLongitude();

//        latitude = "60.338935";
//        longitude = "102.296999";
        String language = Locale.getDefault().getLanguage();

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
            saveCityAndCoords(placeName, latitude, longitude);
            Dialog_menu.setLoaderVisibility();
        } catch (JSONException e) {
            prepareJsonException();
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
        } else if(components.has(COUNTY)) {
            placeName = components.getString(COUNTY);
        }else
         {
            placeName = placeInfo.getString(FORMATTED).split(COMMA)[0];
        }
    }

    private void prepareJsonException() {
        Dialog_menu.getCity.setText(LOCATION);
            saveCityAndCoords(LOCATION, latitude, longitude);
            Dialog_menu.setLoaderVisibility();
    }

    private void saveCityAndCoords(String city, String lat, String lon) {
        editor.putString(CITY_NAME, city);
        editor.putString(LATITUDE, lat);
        editor.putString(LONGITUDE, lon);
        editor.apply();
    }

}