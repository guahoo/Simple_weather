package com.example.simpleweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.simpleweather.Utility.Convert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    JSONArray jArr;
    TextView  updated_atTxt,pressureTxt, humidityTxt,windTxt,statusTxt;
    TextView   tempTxt;
;


    public ViewPagerAdapter(Context context, JSONArray jArr) {
        this.mContext = context;
        this.jArr=jArr;

    }

    @Override
    public int getCount() {
        return jArr.length();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager, container,
                false);

        try {
            JSONObject jsonObject=jArr.getJSONObject(position);
            JSONObject main = jsonObject.getJSONObject("main");
            Long updatedAt = jsonObject.getLong("dt");
            JSONObject wind = jsonObject.getJSONObject("wind");
            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);






            String temp = Convert.tempString(main.getString("temp"));
            String updatedAtText = new SimpleDateFormat("dd/MM/yyyy HH:mm ", Locale.ENGLISH)
                    .format(new Date(updatedAt * 1000));
            String pressure = main.getString("pressure");
            String humidity = main.getString("humidity");
            String windSpeed = wind.getString("speed");
            String weatherDescription = weather.getString("description");





            tempTxt = itemView.findViewById(R.id.temp);
            updated_atTxt = itemView.findViewById(R.id.updated_at);
            windTxt = itemView.findViewById(R.id.wind);
            pressureTxt = itemView.findViewById(R.id.pressure);
            humidityTxt = itemView.findViewById(R.id.humidity);
            statusTxt = itemView.findViewById(R.id.status);




            tempTxt.setText(temp);
            updated_atTxt.setText(updatedAtText);
            pressureTxt.setText(pressure);
            humidityTxt.setText(humidity);
            statusTxt.setText(weatherDescription.toUpperCase());
            windTxt.setText(windSpeed);

        }


        catch (JSONException e) {
            e.printStackTrace();
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}