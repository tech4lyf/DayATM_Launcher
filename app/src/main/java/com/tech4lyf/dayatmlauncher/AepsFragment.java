package com.tech4lyf.dayatmlauncher;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.fxn769.Numpad;
import com.google.android.material.textfield.TextInputEditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AepsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class AepsFragment extends Fragment {

    JSONParser jsonParser;
    ProgressDialog progressDialog;
    TextInputEditText tipAmt,tipAdhaar;
    ArrayList<String> stringsName = new ArrayList<>();
    ArrayList<String> stringsId = new ArrayList<>();
    Spinner spinner;
    String bankId,bankName;
    Button btnNext;
    String data;
    MqttAndroidClient client;
    String topic="dayatm";
    Numpad numpad,numpad1;
    String adhaar;
    boolean isAmt=false;


    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AepsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AepsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AepsFragment newInstance(String param1, String param2) {
        AepsFragment fragment = new AepsFragment();
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_aeps, container, false);

        jsonParser = new JSONParser(getContext());
        progressDialog = new ProgressDialog(getContext());
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        tipAdhaar=(TextInputEditText)rootView.findViewById(R.id.tipAdhaar);
        tipAmt=(TextInputEditText)rootView.findViewById(R.id.tipAepsAmt);
        btnNext=(Button)rootView.findViewById(R.id.btnAepsNext);

        tipAdhaar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(tipAdhaar.getText().toString().length()==12)
                {
                    Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
                    adhaar=tipAdhaar.getText().toString();
                    isAmt=true;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //tipOTP.requestFocus();
                    requestFocus(tipAmt);
                    Toast.makeText(getActivity(), "Please enter Amount.", Toast.LENGTH_SHORT).show();
                    numpad.setVisibility(View.GONE);
                    numpad1.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sh = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
                String macid = sh.getString("devid", "");
                String userid = sh.getString("userid", "");
                if(bankName.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please select Bank", Toast.LENGTH_SHORT).show();
                }
                else {


//                String adhaar=tipAdhaar.getText().toString();
                    String amt = tipAmt.getText().toString();
                    if (Integer.parseInt(amt) < 200) {
                        Toast.makeText(getActivity(), "Amount should be greater than 200.", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(amt) > 5000)
                    {
                        Toast.makeText(getActivity(), "Amount should be lesser than 5000.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        data = "{\"type\":\"aeps\",\"macid\":\"" + macid + "\",\"status\":\"true\",\"adhaar\":\"" + adhaar + "\",\"amt\":\"" + amt + "\",\"userid\":\"" + userid + "\",\"bankName\":\"" + bankName + "\",\"bankNo\":\"" + bankId + "\"}";
                        Log.e("Data", data);

                        sendMqtt(data, topic);

                        Toast.makeText(getActivity(), "Please place your finger in the Fingerprint Scanner", Toast.LENGTH_SHORT).show();

                        Intent actAeps = new Intent(getActivity(), AEPSLoadActivity.class);

                        startActivity(actAeps);
                    }

                }
            }
        });


        numpad = rootView.findViewById(R.id.numAdhaar);
        numpad.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);

            tipAdhaar.setText(text);

        });


        numpad1 = rootView.findViewById(R.id.numAmtaeps);
        numpad1.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);

            tipAmt.setText(text);

        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                Log.e("ID", "" + pos);
                if (pos == 0) {
                    bankId = "0";
                    bankName="";
                } else {
                    bankId = stringsId.get(pos - 1);
                    bankName=stringsName.get(pos);

                }
                Toast.makeText(getActivity(), "BankId" + bankId, Toast.LENGTH_SHORT).show();
                Log.e("BankId", "" + bankId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        new GetBanks().execute();
        return rootView;
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    class GetBanks extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading Banks...");
            progressDialog.show();
            stringsName.add("Select a Bank");

        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("", "");

            try{
                JSONObject jsonObject = jsonParser.makeHttpRequest("https://apiworld.co.in/AEPS/GetBankDetails?tokenkey=API414793&password=1596", "GET", params);
                JSONObject jsonObject1 = jsonObject.getJSONObject("details");
                JSONArray jsonArray = jsonObject1.getJSONArray("data");
                Log.e("Banks", jsonArray.toString());

                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                    String bankName = jsonObject2.optString("bankName").toString();
                    String bankId = jsonObject2.optString("iinno").toString();


                    stringsName.add(bankName);
                    stringsId.add(bankId);

                    Log.e("SpinData",stringsName.toString());
                    Log.e("SpinID",stringsId.toString());

                }

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,stringsName);
            spinner.setAdapter(adapter);
            connect();
            progressDialog.dismiss();


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
