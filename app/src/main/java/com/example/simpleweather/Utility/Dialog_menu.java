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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;
import com.example.simpleweather.MainActivity;
import com.example.simpleweather.R;
import com.example.simpleweather.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class Dialog_menu {

    SharedPreferences sharedPreferences;
    Context context;
    ImageButton ok_button;
    EditText getCity;
    ListView bar;




    SimpleAdapter adapter;


    public Dialog_menu(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;


    }


    public void showMenuDialog() {
        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = d.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        d.setContentView(R.layout.input_dialog);
        ok_button = d.findViewById(R.id.ok_Btn);
        getCity=d.findViewById(R.id.cityNameEditText);


        bar=d.findViewById(R.id.cityList);

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cityName",getCity.getText().toString());
                editor.apply();
                d.hide();
                ((MainActivity)context).executeWeatherTask();



            }
        });

        getCity.addTextChangedListener(textWatcher);


        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {



            }
        });


        d.show();
    }
    public  class JSONSearchTask extends AsyncTask<String, Void, String> {
        String getCityName = getCity.getText().toString();
        String API = "b542736e613d2382837ad821803eb507";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            String result = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/find?mode=json&q="+getCityName+"&appid="+API+"&units=metric");
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            ArrayList<HashMap<String,String>>cityList = new ArrayList<>();


            try {
                JSONObject jObj = new JSONObject(result);
                JSONArray jArr = jObj.getJSONArray("list");

                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    HashMap<String,String> getCityName = new HashMap<>();

                    String name = obj.getString("name");
                    String id = obj.getString("id");

                    JSONObject sys = obj.getJSONObject("sys");
                    String country = sys.getString("country");


                    getCityName.put("name",name+", "+country+", "+id);
                    cityList.add(getCityName);


                }



                for (int i=0;i<cityList.size();i++){
                    System.out.println(cityList.get(i).get("name"));
                }


                adapter=new SimpleAdapter( context, cityList,R.layout.listview_item, new String[]{"name"}, new int[]{R.id.colName} );

                bar.setAdapter( adapter );
                bar.setVisibility(View.VISIBLE);

                bar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getCity.removeTextChangedListener(textWatcher);
                        getCity.setText(cityList.get(position).get("name"));
                        ok_button.setVisibility(View.VISIBLE);
                        bar.setVisibility(View.GONE);


                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }





        }

    }
    TextWatcher textWatcher=new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.equals("")){
                bar.setVisibility(View.VISIBLE);
                JSONSearchTask task = new JSONSearchTask();
                String pattern = getCity.getEditableText().toString();
                task.execute(new String[]{pattern});
                ok_button.setVisibility(View.INVISIBLE);

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




}
