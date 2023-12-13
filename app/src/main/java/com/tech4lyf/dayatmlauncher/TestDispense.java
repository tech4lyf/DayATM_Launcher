package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TestDispense extends AppCompatActivity {

    EditText edt1;
    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dispense);

        String amt="";

        SharedPreferences sharedPreferences = getSharedPreferences("DeviceData", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("amt","100000");
        myEdit.commit();

        btn1=(Button)findViewById(R.id.btnDisp);
        edt1=(EditText)findViewById(R.id.edtAmtTest);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actDIs=new Intent(getApplicationContext(),MQTTDispense.class);
                actDIs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                actDIs.putExtra("amount", Integer.parseInt(edt1.getText().toString()));
                actDIs.putExtra("refId","TXN0001");
                actDIs.putExtra("loc","Test Location");
                actDIs.putExtra("mode","ATM");
                startActivity(actDIs);
            }
        });


    }
}