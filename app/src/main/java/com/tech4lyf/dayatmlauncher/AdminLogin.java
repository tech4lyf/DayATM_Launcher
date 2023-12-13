package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.numpad.NumPad;
import com.fxn769.Numpad;
import com.google.android.material.textfield.TextInputEditText;

public class AdminLogin extends AppCompatActivity {

    Button btnLogin;
    TextInputEditText tipPin;
    String pin="",mpin="";
    Numpad numPad;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        
        tipPin=(TextInputEditText)findViewById(R.id.edtPin);
        btnLogin=(Button)findViewById(R.id.btnAdminLogin);

        SharedPreferences sh1 = getSharedPreferences("MerchantData", MODE_PRIVATE);
        mpin = sh1.getString("pin", "");
        numPad = (Numpad)findViewById(R.id.numPadAdmin);
//        Toast.makeText(this, ""+mpin, Toast.LENGTH_SHORT).show();

        numPad.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);
            tipPin.setText(text);
        });


        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               pin = tipPin.getText().toString();
               
               if(pin.isEmpty())
               {
                   Toast.makeText(AdminLogin.this, "Enter Pin", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   if(pin.equals(mpin))
                   {
                       Toast.makeText(AdminLogin.this, "Login Success", Toast.LENGTH_SHORT).show();
                       Intent actSetup=new Intent(AdminLogin.this,AdminSetup.class);
                       startActivity(actSetup);
                   }
                   else
                   {
                       Toast.makeText(AdminLogin.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                   }
               }
                       
            }
        });
        
    }
}