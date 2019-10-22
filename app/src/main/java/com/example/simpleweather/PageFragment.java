package com.example.simpleweather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class PageFragment extends Fragment {

//    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
//
//    int pageNumber;
//    String CITY = "Moscow";
//    String API = "b542736e613d2382837ad821803eb507";
//    TextView  statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
//            sunsetTxt, windTxt, pressureTxt, humidityTxt;
//    Context context;
//    ProgressBar loader;
//    RelativeLayout mainContainer;
//    TextView errorText;
//
//    public void setWeather(String weather) {
//        this.weather = weather;
//    }
//
//    String weather;
//
//
//
//
//    SharedPreferences sharedPreferences;
//    String PREFERENCES;
//
//
//    static PageFragment newInstance(int page,Context context) {
//        PageFragment pageFragment = new PageFragment(context);
//        Bundle arguments = new Bundle();
//        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
//        pageFragment.setArguments(arguments);
//        return pageFragment;
//    }
//
//    public PageFragment(){
//
//    }
//
//    public PageFragment(Context context) {
//        this.context=context;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
//        Toast toast=Toast.makeText(context.getApplicationContext(),String.valueOf(pageNumber),Toast.LENGTH_SHORT);
//        toast.show();
//
//
//
//
//
//
//
//
//
//
//       sharedPreferences = context.getApplicationContext ().getSharedPreferences (PREFERENCES, MODE_PRIVATE );
//        CITY = "Moscow";
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.fragment, null);
//        statusTxt = view.findViewById(R.id.status);
//        tempTxt = view.findViewById(R.id.temp);
//        temp_minTxt = view.findViewById(R.id.temp_min);
//        temp_maxTxt = view.findViewById(R.id.temp_max);
//        sunriseTxt = view.findViewById(R.id.sunrise);
//        sunsetTxt = view.findViewById(R.id.sunset);
//        windTxt = view.findViewById(R.id.wind);
//        pressureTxt = view.findViewById(R.id.pressure);
//        humidityTxt = view.findViewById(R.id.humidity);
//        loader = view.findViewById(R.id.loader);
//        mainContainer=view.findViewById(R.id.mainContainer);
//        errorText=view.findViewById(R.id.errorText);
//
//        statusTxt.setText(weather);
//
//
//
//
//        return view;
//    }



}