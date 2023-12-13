package com.tech4lyf.dayatmlauncher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.fxn769.Numpad;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QrFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QrFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QrFragment newInstance(String param1, String param2) {
        QrFragment fragment = new QrFragment();
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
    NumberKeyboard numberKeyboard;
    TextInputEditText tipAmount;
    Button btnQr;
    String amt,loc;
    ImageView imgQr;
    JSONParser jsonParser;
    TextView tvService;
    String serv;
    double gst;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=inflater.inflate(R.layout.fragment_qr, container, false);
        tipAmount=(TextInputEditText)rootView.findViewById(R.id.tipQrAmt);
        btnQr=(Button)rootView.findViewById(R.id.btnQrGen);
        jsonParser=new JSONParser();
        imgQr=(ImageView)rootView.findViewById(R.id.ivQr);
        tvService=(TextView)rootView.findViewById(R.id.tvService);

        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                amt=tipAmount.getText().toString();


                if(amt.isEmpty())
                {
                    Toast.makeText(getActivity(), "Amount should not be empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    if (Integer.parseInt(amt) < 200) {
                        Toast.makeText(getActivity(), "Please enter amount greater than 200", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(amt)>5000)
                    {
                        Toast.makeText(getActivity(), "Please enter amount lesser than 5000", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(Integer.parseInt(amt)<=1900)
                        {
                            gst=17.70;
                        }
                        else if(Integer.parseInt(amt)>1901)
                        {
                            gst=17.70;
                        }

                    GetRefID getRefID = new GetRefID();
                    getRefID.execute();}
                }
            }
        });

        Numpad numpad = rootView.findViewById(R.id.numQRamt);
        numpad.setOnTextChangeListner((String text, int digits_remaining) -> {
            Log.d("input",text+"  "+digits_remaining);

            tipAmount.setText(text);


        });


        numberKeyboard=(NumberKeyboard)rootView.findViewById(R.id.numkeyboardQr); 
        
        numberKeyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {
                    String tmp = tipAmount.getText().toString();
                    tipAmount.setText(tmp + "" + i);
                
            }

            @Override
            public void onLeftAuxButtonClicked() {

            }

            @Override
            public void onRightAuxButtonClicked() {

                String text = tipAmount.getText().toString();
                if (!text.isEmpty()) {
                    text = text.substring(0, text.length() - 1);
                    tipAmount.setText(text);
                }
            }

            @Override
            public void onModifierButtonClicked(int i) {

            }
        });
        
        return rootView;
    }

    public class GetRefID extends AsyncTask<Void,Void,Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HashMap<String, String> params = new HashMap<String, String>();

                SharedPreferences sh = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
                String macid = sh.getString("devid", "");
                String userid = sh.getString("userid", "");
                loc = sh.getString("loc", "");

                double gstamt=Integer.parseInt(amt)+gst;
                params.put("amount", amt);
                params.put("userid",userid);

                JSONObject json = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictupi/atmtransaction", "POST", params);
                Log.d("JSON", json.toString());
                JSONObject jsonresp = new JSONObject(json.getString("response"));
                Log.e("RefID", jsonresp.getString("refId"));
                String refId = jsonresp.getString("refId");
                if (refId != null) {
                    Intent i = new Intent(getActivity().getApplicationContext(), BackgroundService.class);
                    i.putExtra("refId", refId);
                    i.putExtra("amount", amt);
                    i.putExtra("loc",loc);
                    i.putExtra("mode","QR_CODE");
                    getActivity().startService(i);

                    QRGEncoder qrgEncoder = new QRGEncoder("upi://pay?pa=npspl@icici&pn=NadanPay&tr=" + refId + "&am=" +gstamt + "&cu=INR&mc=5411", null, QRGContents.Type.TEXT, 512);
                    try {
                        // Getting QR-Code as Bitmap
                        Bitmap bitmap = qrgEncoder.getBitmap();
                        // Setting Bitmap to ImageView
                        imgQr.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        Log.v("Error", e.toString());
                    }

                }
            } catch (Exception ex) {
                Log.e("Error", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            double gst=Integer.parseInt(amt)*0.18;
            tvService.setText("Service charges "+gst+" Rs is includent in QR");
            progressDialog.dismiss();

        }
    }
}