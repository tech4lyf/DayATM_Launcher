package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MQTTDispense extends AppCompatActivity {

    int amount = 0;
    String txnId = "";
    String loc = "";
    String data="",mode="";

    MqttAndroidClient client;
    String topic="dayatm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqttdispense);


        amount = getIntent().getIntExtra("amount", 0);
        txnId = getIntent().getStringExtra("refId");
        loc=getIntent().getStringExtra("loc");
        mode=getIntent().getStringExtra("mode");

//        Toast.makeText(this, "AMount:"+amount, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "MMODE:"+mode, Toast.LENGTH_SHORT).show();


        connect();

        Log.e("MQTTDIspense", amount + "//" + txnId);

//        new Dispense().execute();

    }

    public class Dispense extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            connect();

        }

        @Override
        protected Void doInBackground(Void... voids) {



            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            SharedPreferences sh =getSharedPreferences("MerchantData", MODE_PRIVATE);
            String macid = sh.getString("devid", "");
            String userid = sh.getString("userid", "");

             data = "{\"type\":\"dispense\",\"amount\":\"" + amount + "\",\"macid\":\""+macid+"\"}";

            Log.e("MQTTDIspense", data);
            try {
//                sendMqtt(data, topic);
                Log.e("MSG",data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMqtt(String data, String topic) throws InterruptedException {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = data.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


    void connect()
    {
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
                        SharedPreferences sh = getSharedPreferences("MerchantData", MODE_PRIVATE);
                        String devid = sh.getString("devid", "");

                        data = "{\"type\":\"dispense\",\"amount\":\"" + amount + "\",\"macid\":\""+devid+"\"}";
                        sendMqtt(data,topic);
                        Toast.makeText(MQTTDispense.this, "Thanks for using DAY ATM", Toast.LENGTH_SHORT).show();
                        Intent actMain=new Intent(MQTTDispense.this,SuccessActivity.class);
                        actMain.putExtra("loc",loc);
                        actMain.putExtra("amt",amount);
                        actMain.putExtra("txnId",txnId);
                        actMain.putExtra("mode",mode);
                        //Toast.makeText(MQTTDispense.this, "Mode"+mode, Toast.LENGTH_SHORT).show();
                        amount=0;
                        finish();
                        startActivity(actMain);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                void connect()
                {
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


                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                // Something went wrong e.g. connection timeout or firewall problems
                                Log.d("TAG", "onFailure");

                            }
                        });
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
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}