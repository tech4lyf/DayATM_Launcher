package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class AEPSLoadActivity extends AppCompatActivity {

    MqttAndroidClient client;
    ProgressDialog progressDialog;
    String macid="",loc="",userid="";

    Boolean isHome=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aepsload);

        SharedPreferences sh1 = getSharedPreferences("MerchantData", MODE_PRIVATE);
        userid = sh1.getString("userid", "");
        loc = sh1.getString("loc", "");
        macid = sh1.getString("devid", "");
        Log.e("USER",userid);

        progressDialog=new ProgressDialog(AEPSLoadActivity.this);
        progressDialog.setMessage("Waiting for the Response from the Server");
        progressDialog.setCancelable(true);

        progressDialog = new ProgressDialog(AEPSLoadActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();//dismiss dialog
                if(isHome==true) {
                    Intent actHome = new Intent(AEPSLoadActivity.this, MainActivity.class);
                    startActivity(actHome);
                }
                else if(isHome==false)
                {
                    Toast.makeText(AEPSLoadActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        
        new WaitAeps().execute();


    }


    public class WaitAeps extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
//            progressDialog.dismiss();
            String clientId = MqttClient.generateClientId();
            client =
                    new MqttAndroidClient(getApplicationContext(), "tcp://broker.hivemq.com:1883",
                            clientId);

            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d("TAG", "onSuccess");

                        try {
                            client.subscribe("dayatm",0);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d("TAG", "onFailure");

                    }
                });
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {

                        //{"type":"aeps","macid":"DAY0007","status":"true","txnType":"CW","amt":"500","rrn","125114494889"}

                        Log.e("Hello",message.toString());
                        Log.e("DEV",macid);
                        JSONObject jsonObject=new JSONObject(message.toString());
                        String type=jsonObject.getString("type");
                        String macId=jsonObject.getString("macid");
                        String status=jsonObject.getString("status");
                        String txnType=jsonObject.getString("txnType");
                        String amt=jsonObject.getString("amt");
                        String rrn=jsonObject.getString("rrn");
                        String errorMsg=jsonObject.getString("errorMsg");
                        Log.e("rrn",type);


                        if(status.equals("true") && type.equals("aeps") && macId.equals(macid) && txnType.equals("CW"))
                        {
                            Log.e("STATUS","TRUE");
                            Toast.makeText(AEPSLoadActivity.this, "Transaction Success!", Toast.LENGTH_SHORT).show();
                            Intent actDIs = new Intent(getApplicationContext(), MQTTDispense.class);
                            client.disconnect();
                            actDIs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            actDIs.putExtra("amount", Integer.parseInt(amt));
                            actDIs.putExtra("refId",rrn);
                            actDIs.putExtra("loc",loc);
                            actDIs.putExtra("mode","AEPS");
                            startActivity(actDIs);
                        }
                        else {
                            Toast.makeText(AEPSLoadActivity.this, "Transaction Failed due to "+errorMsg, Toast.LENGTH_LONG).show();
                            Log.e("STATUS","FALSE");
                            client.disconnect();
                            Intent actHome = new Intent(AEPSLoadActivity.this, MainActivity.class);
                            startActivity(actHome);
                        }

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });

            } catch (MqttException e) {
                e.printStackTrace();
            }
            Toast.makeText(AEPSLoadActivity.this, "Done", Toast.LENGTH_SHORT).show();
        }
    }


    void connect()
    {


    }

}