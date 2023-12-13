package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.numpad.NumPad;
import com.example.numpad.NumPadClick;
import com.example.numpad.numPadClickListener;
import com.fxn769.Numpad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    NumberKeyboard numberKeyboard;
    TextInputEditText edtPhone,edtOTP;
    TextInputLayout tipOTP;
    Button btnLogin;
    String phone,otp,status,otpStatus,balance;
    boolean isOTP=false;

    ImageView logo;

    TextView tvResend;
    StringBuilder sb;
    JSONParser jsonParser;
    JSONObject jsonOTP;

    NumPad numPad;

    Numpad numpad,numpad1;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        builder = new AlertDialog.Builder(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        edtPhone = (TextInputEditText) findViewById(R.id.edtPhone);
        edtOTP = (TextInputEditText) findViewById(R.id.edtOTP);
        numberKeyboard = (NumberKeyboard) findViewById(R.id.numkeyboardLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvResend=(TextView)findViewById(R.id.tvResendLoginotp);

        logo=(ImageView)findViewById(R.id.imgLogo);

        tipOTP=(TextInputLayout)findViewById(R.id.tipOTP);

        jsonParser = new JSONParser(getApplicationContext());
        progressDialog=new ProgressDialog(LoginActivity.this);

        numPad = (NumPad)findViewById(R.id.numpadLogin);

        sb = new StringBuilder();

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login().execute();
            }
        });

        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();

                Intent actAdmin=new Intent(LoginActivity.this,AdminLogin.class);
                startActivity(actAdmin);

                return false;
            }
        });

        numPad.setOnNumPadClickListener(new NumPadClick(new numPadClickListener() {
            @Override
            public void onNumpadClicked(ArrayList<Integer> nums) {


                Log.d("MYTAG", "onNumpadClicked: " + nums);

                if(isOTP)
                {
                    String tmp = edtOTP.getText().toString();
//                    edtOTP.setText("");

//                    sb.setLength(0);
                    for (Integer number : nums) {
                        sb.append(number != null ? number.toString() : "");
                    }
                    System.out.println("The number string = " + sb.toString());

                    edtOTP.setText(""+sb.toString());
                    nums.clear();
                }
                else {
                    String tmp = edtPhone.getText().toString();
//
//                    sb.setLength(0);
                    for (Integer number : nums) {

                        sb.append(number != null ? number.toString() : "");
                    }
                    System.out.println("The number string = " + sb.toString());

                    edtPhone.setText(""+sb.toString());
                    nums.clear();

                }

            }
        }));

        numpad = findViewById(R.id.numPhoneLogin);
        numpad.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);
            edtPhone.setText(text);
        });

        numpad1= findViewById(R.id.numOTPLogin);
        numpad1.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);
            edtOTP.setText(text);
        });





        numberKeyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {
                if(isOTP)
                {
                    String tmp = edtOTP.getText().toString();
                    edtOTP.setText(tmp + "" + i);
                }
                else {
                    String tmp = edtPhone.getText().toString();
                    edtPhone.setText(tmp + "" + i);
                }
            }

            @Override
            public void onLeftAuxButtonClicked() {

            }

            @Override
            public void onRightAuxButtonClicked() {

                if(isOTP)
                {
                    String text = edtOTP.getText().toString();
                    if (!text.isEmpty()) {
                        text = text.substring(0, text.length() - 1);
                        edtOTP.setText(text);
                    }
                }
                else {
                    String text = edtPhone.getText().toString();
                    if (!text.isEmpty()) {
                        text = text.substring(0, text.length() - 1);
                        edtPhone.setText(text);
                    }
                }
            }

            @Override
            public void onModifierButtonClicked(int i) {

            }
        });

        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(edtPhone.getText().toString().length()==10)
                {
                    phone=edtPhone.getText().toString();
                    FetchIP fetchIP=new FetchIP();
                    fetchIP.execute();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPhone.setText("");
                requestFocus(edtPhone);
            }
        });

        edtOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtOTP.setText("");
                requestFocus(edtOTP);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=edtOTP.getText().toString();
                if(otp.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
                else if(otp.length()<6)
                {
                    Toast.makeText(LoginActivity.this, "Please enter a valid OTP!", Toast.LENGTH_SHORT).show();
                }
                else {

                HashMap<String, String> params = new HashMap<>();
                String valStatus = "";
                JSONObject jsonObjectValidate = jsonParser.makeHttpRequest("https://developers.tech4lyf.com/validate/?appid=0002", "GET", params);
                try {
                    valStatus = jsonObjectValidate.getString("isActive");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (valStatus.equals("false")) {
                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                } else {

                    new VerifyOTP().execute();
//                        Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    balance = "0.0";
                    String myip = "192.168.0.7";
                    Log.e("Balance", balance);


                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

// Storing the key and its value as the data fetched from edittext
                    myEdit.putString("phone", phone);
                    myEdit.putString("balance", balance);
                    myEdit.putString("ipaddress", myip);


// Once the changes have been made,
// we need to commit to apply those changes made,
// otherwise, it will throw an error
                    myEdit.commit();
                    SharedPreferences sh1 = getSharedPreferences("DeviceData", MODE_PRIVATE);
                    String amt1 = sh1.getString("amt", "");
                    if(Integer.parseInt(amt1)<=10000)
                    {
                        //Toast.makeText(getApplicationContext(), "Machine low balance alert!...\nPlease contact ShopKeeper", Toast.LENGTH_SHORT).show();
                        builder.setMessage("Machine low balance alert!...\nPlease contact ShopKeeper") .setTitle("Warning Alert!")

                                //Setting message manually and performing action on button click
                                //builder.setMessage("Do you want to close this application ?")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        //Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                        //      Toast.LENGTH_SHORT).show();
                                    }
                                });
                        // .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        //   public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        //dialog.cancel();
                        //Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                        //      Toast.LENGTH_SHORT).show();
                        // }
                        //});
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        //alert.setTitle("AlertDialogExample");
                        alert.show();

                    }
                    else {
                        Intent actHome = new Intent(LoginActivity.this, HomeActivity.class);

                        if(edtPhone.getText().toString().isEmpty())
                        {
                            Toast.makeText(LoginActivity.this, "Please enter Phone number!", Toast.LENGTH_SHORT).show();
                        }
                        else if(edtPhone.getText().toString().length()<10)
                        {
                            Toast.makeText(LoginActivity.this, "Enter 10 Digits.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                            startActivity(actHome);
                        }
                    }
                    }
                }
            }
        });
    }

    String ip;
    ProgressDialog progressDialog;

    class FetchIP extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching your IP Address...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }



        @Override
        protected Void doInBackground(Void... voids) {

            try {
                try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://api.ipify.org").openStream(), "UTF-8").useDelimiter("\\A")) {
//                    System.out.println("My current IP address is " + s.next());
                    ip=s.next();
                    System.out.println("My current IP address is " + ip);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    ip="0.0.0.0.0";
                }
                Log.e("IP", "" + ip);
            } catch (Exception ex) {
                ip = "00.00.00.00";
                Log.e("Ex",ex.toString());
//                Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            new Login().execute();


            btnLogin.performClick();
        }
    }



    class Login extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait while we sending your OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("mobile", phone);
            params.put("ipaddress", ip);

            jsonOTP = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictuser/getuser", "POST", params);
            Log.e("LOGINDATA", jsonOTP.toString());



            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();

            try {
                status=jsonOTP.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(status.equals("true"))
            {
                tipOTP.setVisibility(View.VISIBLE);
                isOTP=true;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tipOTP.requestFocus();
                requestFocus(tipOTP);
                numpad.setVisibility(View.GONE);
                numpad1.setVisibility(View.VISIBLE);

            }
            else
            {
                Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class VerifyOTP extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Verifying OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();

            try {
                params.put("mobile", phone);
                params.put("ipaddress", ip);
                params.put("otp", otp);

                JSONObject jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictuser/verifyuser", "POST", params);

                otpStatus = jsonObject.getString("status");
                balance=jsonObject.getString("balance");

            }
            catch (Exception ex)
            {
                //Toast.makeText(LoginActivity.this, "Something went wrong!...", Toast.LENGTH_SHORT).show();
                Log.e("Error",ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            if(otpStatus.equals("true"))
            {
                //Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                Log.e("Balance",balance);

                SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

// Storing the key and its value as the data fetched from edittext
                myEdit.putString("phone", phone);
                myEdit.putString("balance", balance);
                myEdit.putString("ipaddress", ip);

// Once the changes have been made,
// we need to commit to apply those changes made,
// otherwise, it will throw an error
                myEdit.commit();

                Intent actHome=new Intent(LoginActivity.this,HomeActivity.class);

                startActivity(actHome);


            }
            else
            {
                Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        }


    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }
}
