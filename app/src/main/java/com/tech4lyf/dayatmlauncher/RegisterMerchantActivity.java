package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class RegisterMerchantActivity extends AppCompatActivity {

    TextInputEditText edtMid,edtMpin,edtDevid,edtLoc;
    Button btnLogin;
    String mpin="",mid="",devid="",loc="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_merchant);

        edtMid=(TextInputEditText)findViewById(R.id.edtMid);
        edtMpin=(TextInputEditText)findViewById(R.id.edtMPin);
        edtLoc=(TextInputEditText)findViewById(R.id.tipLoc);
        edtDevid=(TextInputEditText)findViewById(R.id.tipDevID);
        btnLogin=(Button)findViewById(R.id.btnMLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mid=edtMid.getText().toString();
                mpin=edtMpin.getText().toString();
                devid=edtDevid.getText().toString();
                loc=edtLoc.getText().toString();

                if(mid.isEmpty())
                {
                    Toast.makeText(RegisterMerchantActivity.this, "Please enter Merchant ID", Toast.LENGTH_SHORT).show();
                }
                else if(devid.isEmpty())
                {
                    Toast.makeText(RegisterMerchantActivity.this, "Please enter Device ID", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(mpin.isEmpty())
                    {
                        Toast.makeText(RegisterMerchantActivity.this, "Please enter Merchant Pin", Toast.LENGTH_SHORT).show();
                    }
                    else if(loc.isEmpty())
                    {
                        Toast.makeText(RegisterMerchantActivity.this, "Please enter Location", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        SharedPreferences sharedPreferences = getSharedPreferences("MerchantData",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("isFirstlaunch", "0");
                        myEdit.putString("userid", mid);
                        myEdit.putString("pin", mpin);
                        myEdit.putString("devid",devid);
                        myEdit.putString("apptoken", getString(R.string.dmt_app_token));
                        myEdit.putString("loc",loc);
                        myEdit.commit();

                        SharedPreferences sharedPreferences1 = getSharedPreferences("DeviceData", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit1 = sharedPreferences1.edit();
                        myEdit1.putString("amt", "100000");
                        myEdit1.commit();
                        Toast.makeText(RegisterMerchantActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                        Intent actHome=new Intent(RegisterMerchantActivity.this,MainActivity.class);

                        startActivity(actHome);
                    }
                }

            }
        });
    }
}