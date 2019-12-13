package com.example.simpleweather.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.webkit.WebSettings;

import com.androdocs.httprequest.HttpRequest;
import com.example.simpleweather.NominativeConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class SearchByGeoposition extends AsyncTask<String, Void, String> {

    final static String URL_CITY_NAME_REQUEST = "https://nominatim.openstreetmap.org/reverse?lat=%s&lon=%s&format=json&zoom=18&addressdetails=10";
    protected NominativeConnect nominativeConnect;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    final static String API="835b77b309444de689cd7c07b675493e";
    final static String URL_REQUEST_FORECAST= "https://api.opencagedata.com/geocode/v1/json?key="+API+"&q=%s&,&%s&pretty=1";


    public SearchByGeoposition(Context context, SharedPreferences sharedPreferences) {
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

//        String latitude = "38.685898";
//        String longitude ="55.378686";
        String latitude = GeoLocationFinder.getLatitude();
        String longitude =GeoLocationFinder.getLongitude();


//        return nominativeConnect.excuteGet(String.format(URL_CITY_NAME_REQUEST, latitude, longitude));
        return HttpRequest.excuteGet("https://api.opencagedata.com/geocode/v1/json?key=835b77b309444de689cd7c07b675493e&q="+latitude+","+longitude+"&pretty=5&no_annotations=1&language="+Locale.getDefault().getDisplayLanguage());

    }

    @Override
    public void onPostExecute(String result) {
        JSONObject geoData= null;
        JSONObject placeInfo=null;
        JSONObject components=null;
       // String shortPlaceName=null;
        String placeName=null;
        try {
            geoData = new JSONObject(result);
            JSONArray results=geoData.getJSONArray("results");


            placeInfo=results.getJSONObject(0);
            try {
                components=results.getJSONObject(0).getJSONObject("components");
                placeName = components.getString("city");
            }catch (JSONException e){
                try {
                    placeName = components.getString("village");
                }catch (JSONException jE){
                    try {
                        placeName = components.getString("hamlet");
                    }catch (JSONException jEH){
                        placeName = placeInfo.getString("formatted").split(",")[0];
                    }

                }catch (NullPointerException nE){
                    Dialog_menu.getCity.setText("Нет сети");
                }
            }

           // shortPlaceName=placeName.split(",")[0];

            Dialog_menu.weHaveGeoPosition = true;

            Dialog_menu.getCity.setText(placeName);
            saveCityAndCoords(placeName, GeoLocationFinder.getLatitude(), GeoLocationFinder.getLongitude());
            Dialog_menu.setLoaderVisibility();

        } catch (JSONException e) {
            Dialog_menu.getCity.setText("Ваше местоположение");
            if (placeInfo != null) {
                saveCityAndCoords( "Ваше местоположение", GeoLocationFinder.getLatitude(), GeoLocationFinder.getLongitude());
                Dialog_menu.setLoaderVisibility();
            }

        }catch (NullPointerException nE) {
            Dialog_menu.getCity.setText("Нет сети");
        }finally
         {
            Dialog_menu.weHaveGeoPosition = false;
        }
    }
    protected void saveCityAndCoords(String city, String lat, String lon) {
        editor.putString("city_name", city);
        editor.putString("cityLon",lon);
        editor.putString("cityLat", lat);
        editor.apply();
    }

}