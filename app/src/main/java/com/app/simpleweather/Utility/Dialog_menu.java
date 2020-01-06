package com.app.simpleweather.Utility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
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

import com.app.simpleweather.MainActivity;
import com.app.simpleweather.NominativeConnect;
import com.app.simpleweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.app.simpleweather.Utility.OftenUsedStrings.CITY_NAME;
import static com.app.simpleweather.Utility.OftenUsedStrings.COMMA;
import static com.app.simpleweather.Utility.OftenUsedStrings.ISADAY;
import static com.app.simpleweather.Utility.OftenUsedStrings.LATITUDE;
import static com.app.simpleweather.Utility.OftenUsedStrings.LONGITUDE;


public class Dialog_menu {

    private static final String CITY_NAME_DISPLAY_LIST = "name";
    private static final String DISPLAYED_NAME = "display_name";
    private static final String LON = "lon";
    private static final String LAT = "lat";
    private static final String NO_CONNECTION = "No connection";
    private static final String NOT_FOUND = "Not found";
    private SharedPreferences sharedPreferences;
    private Context context;
    private ImageButton ok_button, getLocationButton;
    static EditText getCity;
    private ListView bar;
    protected NominativeConnect nominativeConnect;
    private SharedPreferences.Editor editor;
    private RelativeLayout dialogLayout;
    GeoLocationFinder geoFinder;
    static volatile boolean weHaveGeoPosition;
    static ProgressBar loader;
    Dialog d;
    private static Dialog_menu instance;


//    public synchronized Dialog_menu getInstance(SharedPreferences sharedPreferences, Context context) {
//        this.sharedPreferences = sharedPreferences;
//        this.context = context;
//        if (instance == null)
//            instance = new Dialog_menu();
//        return instance;
//    }


    private SimpleAdapter adapter;
    //public static final Dialog_menu INSTANCE = new Dialog_menu(SharedPreferences sharedPreferenses,Context condtext);

//
    public Dialog_menu(SharedPreferences sharedPreferences, Context context) {

        this.sharedPreferences = sharedPreferences;
        this.context = context;

    }




    public void showMenuDialog() {

        d = new Dialog(context);
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

        if (sharedPreferences.getBoolean(ISADAY, false)) {
            dialogLayout.setBackgroundResource(R.drawable.bg_gradient_day);
        }

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    getCityName.put(CITY_NAME_DISPLAY_LIST, placeInfo.getString(DISPLAYED_NAME));
                    getCityLat.put(LAT, placeInfo.getString(LAT));
                    getCityLon.put(LON, placeInfo.getString(LON));


                    cityNames.add(getCityName);
                    cityLon.add(getCityLon);
                    cityLat.add(getCityLat);


                }

                adapter = new SimpleAdapter(context, cityNames, R.layout.listview_item, new String[]{CITY_NAME_DISPLAY_LIST}, new int[]{R.id.colName});

                bar.setAdapter(adapter);
                bar.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);

                bar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getCity.removeTextChangedListener(textWatcher);

                        String str = cityNames.get(position).get(CITY_NAME_DISPLAY_LIST);
                        try {
                            String kept = str.substring(0, str.indexOf(COMMA));

                            setTextCityName(kept);
                            saveCityAndCoords(kept, cityLat.get(position).get(LAT), cityLon.get(position).get(LON));
                        } catch (StringIndexOutOfBoundsException sE) {
                            setTextCityName(str);
                            saveCityAndCoords(str, cityLat.get(position).get(LAT), cityLon.get(position).get(LON));
                        }


                        ok_button.setVisibility(View.VISIBLE);
                        bar.setVisibility(View.GONE);

                    }
                });


            } catch (JSONException e) {
                setTextCityName(NOT_FOUND);

            } catch (NullPointerException nE){

                setTextCityName(NO_CONNECTION);



            }


        }

    }

    protected void saveCityAndCoords(String city, String lat, String lon) {
        editor.putString(CITY_NAME, city);
        editor.putString(LATITUDE, lat);
        editor.putString(LONGITUDE, lon);

        editor.apply();
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!weHaveGeoPosition && !s.equals("\\s*\\S+")) {
                loader.setVisibility(View.VISIBLE);
                bar.setVisibility(View.VISIBLE);
                String pattern = getCity.getEditableText().toString();
                SearchLocationNameTask searchLocationNameTask = new SearchLocationNameTask();
                searchLocationNameTask.execute(pattern);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
    public static void setTextCityName(String text) {

        int spaces = text.replaceAll("[^ ]", "").length();
        if (spaces>=2)getCity.setTextSize(TypedValue.COMPLEX_UNIT_SP,15f);
        getCity.setText(text);
    }

    public void hideMenuDialog(){
        d.hide();
    }


}
