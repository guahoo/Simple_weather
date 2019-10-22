package com.example.simpleweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    JSONArray jArr;


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
        TextView   tempTxt;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager, container,
                false);

        try {
            JSONObject jsonObject=jArr.getJSONObject(position);
            JSONObject main = jsonObject.getJSONObject("main");
            String temp = main.getString("temp") + "°C";
            tempTxt = itemView.findViewById(R.id.temp);
            tempTxt.setText(temp);







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