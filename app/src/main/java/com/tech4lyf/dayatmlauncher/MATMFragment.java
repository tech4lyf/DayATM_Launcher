package com.tech4lyf.dayatmlauncher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fingpay.microatmsdk.MicroAtmLoginScreen;
import com.fxn769.Numpad;
import com.google.android.material.textfield.TextInputEditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.fingpay.microatmsdk.utils.Constants;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MATMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MATMFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MATMFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MATMFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MATMFragment newInstance(String param1, String param2) {
        MATMFragment fragment = new MATMFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View rootView;
    Button btnNext;
    TextInputEditText tipAmt;
    String userid="";
    String loc="";
    String macid="";

    String timeStamp,txnId="";
    String msg="",type="";
    boolean status=false;
    MqttAndroidClient client;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_m_a_t_m, container, false);

        tipAmt = (TextInputEditText) rootView.findViewById(R.id.tipAtmAmt);
        btnNext = (Button) rootView.findViewById(R.id.btnAtmNext);

        progressDialog = new ProgressDialog(getActivity());

        jsonParser = new JSONParser();
        new GetMerchantData().execute();


        SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
        userid = sh1.getString("userid", "");
        loc = sh1.getString("loc", "");
        macid = sh1.getString("devid", "");
        Log.e("USER",userid);

        Numpad numpad = rootView.findViewById(R.id.numATMamt);
        numpad.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);

            tipAmt.setText(text);


        });

        connect();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                txnId="DAYATM" + timeStamp;

                amt = tipAmt.getText().toString();
                if(amt.isEmpty()) {
                    Toast.makeText(getActivity(), "Amount should not be emoty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (Integer.parseInt(amt) < 200) {
                        Toast.makeText(getActivity(), "Amount should be greater than 200.", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(amt) > 5000)
                    {
                        Toast.makeText(getActivity(), "Amount should be lesser than 5000.", Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        dispense();
                        new Withdraw().execute();
                    }
                }

            }
        });

        return rootView;


    }

    ProgressDialog progressDialog;
    JSONParser jsonParser;
    String mId, mPin, amt, sMid;

    private static final int CODE = 1;


    class GetMerchantData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("user_id", userid);
            params.put("ipaddress", "0.0.0.0");
            params.put("apptoken", getString(R.string.dmt_app_token));

            try {
                JSONObject jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictmatm/getmerchant", "POST", params);
                mId = jsonObject.getString("merchantid");
                mPin = jsonObject.getString("merchantpin");
                sMid = jsonObject.getString("supermerchantid");
            } catch (Exception ex) {
                ex.toString();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
        }
    }

    class Withdraw extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String imei = "1234";
                Intent intent = new Intent(getActivity().getApplicationContext(), MicroAtmLoginScreen.class);

                intent.putExtra(Constants.MERCHANT_USERID, mId);
                intent.putExtra(Constants.MERCHANT_PASSWORD, mPin);
                intent.putExtra(Constants.AMOUNT, amt);
                intent.putExtra(Constants.REMARKS, "test");
                intent.putExtra(Constants.MOBILE_NUMBER, "7824043013");
                intent.putExtra(Constants.AMOUNT_EDITABLE, false);

                intent.putExtra(Constants.TXN_ID, txnId);
                intent.putExtra(Constants.SUPER_MERCHANTID, sMid);
                intent.putExtra(Constants.IMEI, imei);
                intent.putExtra(Constants.LATITUDE, 13.0836939);
                intent.putExtra(Constants.LONGITUDE, 80.270186);
                intent.putExtra(Constants.TYPE, Constants.CASH_WITHDRAWAL);
                intent.putExtra(Constants.MICROATM_MANUFACTURER, Constants.MoreFun);
                startActivityForResult(intent, CODE);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            sendMqtt("{\"devid\":\""+macid+"\",\"devtype\":\"esp\",\"cmd\":\"card_on\"}","dayatm");
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && requestCode == CODE) {

                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        Log.e("Tag", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                    }

                    type = bundle.getString("TRANS_TYPE", "");
                    msg = bundle.getString("MESSAGE", "");
                    status = bundle.getBoolean("TRANS_STATUS", false);

                    Log.e("CARDSTATUS", String.valueOf(status));
                    Log.e("CARDTYPE", type);
                    Log.e("CARDMESSAGE", msg);


                    if (status == true) {
                        if (type.equals("WDLS")) {
                            if (msg.equals("Request Completed")) {
                                Toast.makeText(getActivity(), "Transaction Success!", Toast.LENGTH_SHORT).show();
                                try {
                                    dispense();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Transaction failed! due to "+msg, Toast.LENGTH_SHORT).show();
                        sendMqtt("{\"devid\":\""+macid+"\",\"devtype\":\"esp\",\"cmd\":\"card_off\"}","dayatm");
//                        Toast.makeText(getActivity(), ""+msg, Toast.LENGTH_SHORT).show();
                    }


//                Log.e("RESP", data.getExtras().keySet().toString());
//                new CheckTXN().execute();
                }
            }

//                Log.e("RESP", data.getExtras().keySet().toString());
//                new CheckTXN().execute();
//
//
//            }
//        }
        }
        catch (Exception ex)
        {
            ex.toString();
            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
//        new CheckTXN().execute();
    }

    void dispense()
    {
        sendMqtt("{\"devid\":\""+macid+"\",\"devtype\":\"esp\",\"cmd\":\"card_off\"}","dayatm");
        Intent actDIs = new Intent(getActivity().getApplicationContext(), MQTTDispense.class);
        actDIs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actDIs.putExtra("amount", Integer.parseInt(amt));
        actDIs.putExtra("refId",txnId);
        actDIs.putExtra("loc",loc);
        actDIs.putExtra("mode","ATM");

        startActivity(actDIs);
    }

    class CheckTXN extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("apptoken",getString(R.string.dmt_app_token));
            params.put("user_id",userid);
            params.put("ipaddress","0.0.0.0");
            params.put("transid",txnId);
            Log.e("TXN_RESP",""+params.toString());

            JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictmatm/checkstatus","POST",params);

            Log.e("RESP",jsonObject.toString());

            String status="";

            try {
                status=jsonObject.getString("trans_status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!status.isEmpty())
            {
                if(status.equals("success"))
                {
                    try {
                    Intent actDIs=new Intent(getActivity().getApplicationContext(),MQTTDispense.class);
                    actDIs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    actDIs.putExtra("amount", Integer.parseInt(amt));
                    actDIs.putExtra("refId",txnId);
                    actDIs.putExtra("loc",loc);
                    actDIs.putExtra("mode","ATM");

                    startActivity(actDIs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

        }
    }

    private void sendMqtt(String data,String topic) {
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
                new MqttAndroidClient(getActivity().getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("TAG", "onSuccess");
                }
                void connect()
                {
                    String clientId = MqttClient.generateClientId();
                    client =
                            new MqttAndroidClient(getActivity().getApplicationContext(), "tcp://broker.hivemq.com:1883",
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