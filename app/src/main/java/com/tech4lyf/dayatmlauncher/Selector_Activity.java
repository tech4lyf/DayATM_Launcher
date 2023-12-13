package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Selector_Activity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);


        getSupportFragmentManager().beginTransaction().add(R.id.fragConSelector, new Selector_Fragment(), "home").commit();


    }
}