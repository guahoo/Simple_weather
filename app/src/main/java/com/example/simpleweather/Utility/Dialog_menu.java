package com.example.simpleweather.Utility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.simpleweather.MainActivity;
import com.example.simpleweather.R;


public class Dialog_menu {

    SharedPreferences sharedPreferences;
    Context context;
    ImageButton ok_button;
    String PREFERENCES;
    String city;
    EditText getCity;

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



        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cityName",getCity.getText().toString());
                editor.apply();
                d.hide();
                Intent intent=new Intent(context,MainActivity.class);
                context.startActivity(intent);

            }
        });

        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {



            }
        });


        d.show();
    }


}
