package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fxn769.Numpad;
import com.google.android.material.textfield.TextInputEditText;

public class AdminSetup extends AppCompatActivity {

    TextInputEditText tipAmount;
    Button btnUpdate;
    Numpad numPad;
    String amt="";
    String amt1="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_setup);

        SharedPreferences sh1 = getSharedPreferences("DeviceData", MODE_PRIVATE);
        amt1 = sh1.getString("amt", "");
        Toast.makeText(this, amt1, Toast.LENGTH_SHORT).show();
        if(amt1.isEmpty())
        {
            amt1="0";
        }

        tipAmount=(TextInputEditText)findViewById(R.id.tipAmountinMachine);
        btnUpdate=(Button)findViewById(R.id.btnAmtUpdate);

        numPad = (Numpad)findViewById(R.id.numPadAmt);
//        Toast.makeText(this, ""+mpin, Toast.LENGTH_SHORT).show();

        numPad.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);
            tipAmount.setText(text);
        });

        tipAmount.setText(amt1);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amt=tipAmount.getText().toString();

                if(amt.isEmpty())
                {
                    Toast.makeText(AdminSetup.this, "Please enter amount!", Toast.LENGTH_SHORT).show();
                }
                else {

                    SharedPreferences sharedPreferences = getSharedPreferences("DeviceData", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("amt", amt);
                    myEdit.commit();

                    Intent actHome=new Intent(AdminSetup.this,MainActivity.class);
                    startActivity(actHome);
                    finish();

                }
            }
        });


    }
}