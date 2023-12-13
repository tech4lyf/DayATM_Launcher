package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    TextView tvWallet;
    QrFragment qrFragment;
    CardView cardViewHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardViewHome=(CardView)findViewById(R.id.cardHome);

        SharedPreferences sh = getSharedPreferences("UserData", MODE_PRIVATE);

// The value will be default as empty string because for
// the very first time when the app is opened, there is nothing to show
        String bal = sh.getString("balance", "");

        cardViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actHome=new Intent(HomeActivity.this,MainActivity.class);

                startActivity(actHome);

            }
        });

// We can then use the data

        qrFragment=new QrFragment();

        tvWallet=(TextView)findViewById(R.id.tvWalletBal);

        tvWallet.setText("Wallet Balance (Rs."+bal+")");
        new GetBalance().execute();

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentMain, new HomeFragment(), "home").commit();
    }

    @Override
    public void onBackPressed() {
        Intent actHome=new Intent(HomeActivity.this,MainActivity.class);
        startActivity(actHome);
        finish();

        
    }

    public class GetBalance extends AsyncTask<Void,Void,Void>
    {

        String balance="";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("Checking your Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String,String> params=new HashMap<>();
            SharedPreferences sh = getSharedPreferences("UserData", MODE_PRIVATE);
            String phone = sh.getString("phone", "");
            String ip=sh.getString("ipaddress","");

            params.put("mobile", phone);
            params.put("ipaddress", "0.00.00.00");

            JSONParser jsonParser1=new JSONParser();
            JSONObject jsonObject1=jsonParser1.makeHttpRequest("https://dayatms.com/webapi/ictuser/getbalance","POST",params);
            Log.e("WallResp",jsonObject1.toString());

            try {
                balance=jsonObject1.getString("balance");
                Log.e("Balance",balance);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            tvWallet.setText(balance);
            progressDialog.dismiss();

        }
    }

}