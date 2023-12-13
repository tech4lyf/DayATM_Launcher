package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TestPrint extends AppCompatActivity {

    Button btnDep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_print);

        btnDep=(Button)findViewById(R.id.btnDep);



        btnDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestPrint.this, "Deposit", Toast.LENGTH_SHORT).show();
                Intent actHome = new Intent(TestPrint.this, SuccessActivity.class);
                actHome.putExtra("mode","DMT");
                actHome.putExtra("txnId","TEST00001");
                actHome.putExtra("amt",1000);
                actHome.putExtra("loc","DAYATM Head Office");

                startActivity(actHome);
            }
        });

    }
}