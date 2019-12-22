package com.example.simpleweather.Utility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.example.simpleweather.MainActivity;
import com.example.simpleweather.NominativeConnect;
import com.example.simpleweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class Dialog_menu {

    private SharedPreferences sharedPreferences;
    private Context context;
    private ImageButton ok_button, getLocationButton;
    static EditText getCity;
    private ListView bar;
    protected NominativeConnect nominativeConnect;
    private SharedPreferences.Editor editor;
    private RelativeLayout dialogLayout;
    GeoLocationFinder geoFinder;
    static boolean weHaveGeoPosition;
    static ProgressBar loader;


    private SimpleAdapter adapter;


    public Dialog_menu(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;

    }



    public void showMenuDialog() {
        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = d.getWindow();
        editor = sharedPreferences.edit();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        geoFinder = new GeoLocationFinder(context, sharedPreferences);


        d.setContentView(R.layout.input_dialog);
        dialogLayout = d.findViewById(R.id.dialog_layout);
        ok_button = d.findViewById(R.id.ok_Btn);
        getLocationButton = d.findViewById(R.id.getLocationButton);
        getCity = d.findViewById(R.id.cityNameEditText);
        getCity.setTextColor(context.getResources().getColor(R.color.blackTextColor));
        bar = d.findViewById(R.id.cityList);
        loader = d.findViewById(R.id.loaderCitylist);


        nominativeConnect = new NominativeConnect();

        if (sharedPreferences.getBoolean("day", false)) {
            dialogLayout.setBackgroundResource(R.drawable.bg_gradient_day);
        }

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cityName", getCity.getText().toString());
                editor.apply();
                d.hide();
                ((MainActivity) context).executeWeatherTask();
            }
        });

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoFinder.getLastLocation();
                loader.setVisibility(View.VISIBLE);


            }
        });

        getCity.addTextChangedListener(textWatcher);


        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {


            }
        });


        d.show();
    }


    public class SearchLocationNameTask extends AsyncTask<String, Void, String> {
        String getCityName = getCity.getText().toString();
        String locale = Locale.getDefault().getLanguage();
                ;
        final static String URL_REQUEST_FORECAST=
                "https://nominatim.openstreetmap.org/search?city=%s&format=json&place=city&accept-language=%s";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {



                return NominativeConnect.excuteGet(String.format(URL_REQUEST_FORECAST,getCityName,locale));

        }

        @Override
        public void onPostExecute(String result) {
            ArrayList<HashMap<String, String>> cityNames = new ArrayList<>();
            ArrayList<HashMap<String, String>> cityLon = new ArrayList<>();
            ArrayList<HashMap<String, String>> cityLat = new ArrayList<>();


            try {

                JSONArray geoData = new JSONArray(result);

                for (int i = 0; i < geoData.length(); i++) {
                    JSONObject placeInfo = geoData.getJSONObject(i);


                    HashMap<String, String> getCityName = new HashMap<>();
                    HashMap<String, String> getCityLon = new HashMap<>();
                    HashMap<String, String> getCityLat = new HashMap<>();

                    getCityName.put("name", placeInfo.getString("display_name"));
                    getCityLon.put("lon", placeInfo.getString("lon"));
                    getCityLat.put("lat", placeInfo.getString("lat"));

                    cityNames.add(getCityName);
                    cityLon.add(getCityLon);
                    cityLat.add(getCityLat);


                }

                adapter = new SimpleAdapter(context, cityNames, R.layout.listview_item, new String[]{"name"}, new int[]{R.id.colName});

                bar.setAdapter(adapter);
                bar.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);

                bar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getCity.removeTextChangedListener(textWatcher);

                        String str = cityNames.get(position).get("name");
                        try {
                            String kept = str.substring(0, str.indexOf(","));

                            getCity.setText(kept);
                            saveCityAndCoords(kept, cityLat.get(position).get("lat"), cityLon.get(position).get("lon"));
                        } catch (StringIndexOutOfBoundsException sE) {
                            getCity.setText(str);
                            saveCityAndCoords(str, cityLat.get(position).get("lat"), cityLon.get(position).get("lon"));
                        }


                        ok_button.setVisibility(View.VISIBLE);
                        bar.setVisibility(View.GONE);

                    }
                });


            } catch (JSONException e) {
                getCity.setText("Город не найден");
            } catch (NullPointerException nE){

               getCity.setText("Нет сети");

            }


        }

    }

    protected void saveCityAndCoords(String city, String lat, String lon) {
        editor.putString("city_name", city);
        editor.putString("cityLon", lon);
        editor.putString("cityLat", lat);
        editor.apply();
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!s.equals("\\s*\\S+") & !weHaveGeoPosition) {
                loader.setVisibility(View.VISIBLE);


                bar.setVisibility(View.VISIBLE);
                String pattern = getCity.getEditableText().toString();
                SearchLocationNameTask searchLocationNameTask = new SearchLocationNameTask();
                searchLocationNameTask.execute(pattern);
//                ok_button.setVisibility(View.INVISIBLE);

            } else {

            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    public static void setLoaderVisibility() {

        loader.setVisibility(View.INVISIBLE);


    }
}
