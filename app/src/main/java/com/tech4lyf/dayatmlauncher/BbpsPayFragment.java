package com.tech4lyf.dayatmlauncher;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BbpsPayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BbpsPayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BbpsPayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BbpsPayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BbpsPayFragment newInstance(String param1, String param2) {
        BbpsPayFragment fragment = new BbpsPayFragment();
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

    TextView tvBType;
    JSONObject jsonObject;
    JSONParser jsonParser;
    String billers_type;
    Spinner spinner;
    ArrayList<String> billersName = new ArrayList<>();
    ArrayList<String> billersCode = new ArrayList<>();

    TextInputEditText tipParam1,tipAmount,tipMobile,tipMail;
    TextInputLayout tilParam1;
    String billCode="";
    String param1="",paramHint="";

    Button btnPay;
    String amt="",mobile="",email="",status="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_bbps_pay, container, false);

        tvBType=(TextView) rootView.findViewById(R.id.tvBillType);
        tipParam1=(TextInputEditText) rootView.findViewById(R.id.tipParam1);
        tipAmount=(TextInputEditText)rootView.findViewById(R.id.tipBbpsAmount);
        tipMobile=(TextInputEditText)rootView.findViewById(R.id.tipBbpsMobile);
        tipMail=(TextInputEditText)rootView.findViewById(R.id.tipBbpsMail);
        tilParam1= (TextInputLayout)rootView.findViewById(R.id.tilParam1);
        jsonParser=new JSONParser(getActivity().getApplicationContext());
        spinner=(Spinner)rootView.findViewById(R.id.spinnerBillerName);
        btnPay=(Button) rootView.findViewById(R.id.buttonBbpsPay);

        Bundle arguments = getArguments();
        billers_type= arguments.getString("billers_type");
        //Toast.makeText(getActivity(), ""+billers_type, Toast.LENGTH_SHORT).show();
        tvBType.setText(billers_type);


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                param1=tipParam1.getText().toString();
                mobile=tipMobile.getText().toString();
                email=tipMail.getText().toString();
                amt=tipAmount.getText().toString();

                if(param1.equals("") || param1.isEmpty())
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter "+paramHint, Toast.LENGTH_SHORT).show();
                }
                else if(amt.equals("") || amt.isEmpty())
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter Amount ", Toast.LENGTH_SHORT).show();
                }
                else if(mobile.equals("") || mobile.isEmpty())
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter Mobile Number ", Toast.LENGTH_SHORT).show();
                }
                else if(email.equals("") || email.isEmpty())
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter E-mail ", Toast.LENGTH_SHORT).show();
                }

                else {
                    new ProcessPay().execute();
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                Log.e("ID", "" + pos);
                if (pos == 0) {
                    billCode = billersCode.get(pos);
//                    bankName="";
                } else {
                    billCode = billersCode.get(pos);
//                    bankName=stringsName.get(pos);

                }
//                Toast.makeText(getActivity(), "BankId" + billerCode, Toast.LENGTH_SHORT).show();
                Log.e("BILLCODE", "" + billCode);
                new GetParams().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new GetBillers().execute();


        return rootView;
    }

    public class GetBillers extends AsyncTask<Void,Void,Void>
    {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("apptoken", "63R99549163A49A9T422D16219A36A45");
            params.put("retailerid", "9344241035");
            params.put("usermobile", "8678939278");
            params.put("ipaddress", "0.0.0.0");
            params.put("billers_type",billers_type);

            try {
                jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictbbps/getbillers", "POST", params);

                Log.e("BBPS", jsonObject.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                    String billerName = jsonObject2.optString("biller_name").toString();
                    String billerCode = jsonObject2.optString("biller_code").toString();


                    billersName.add(billerName);
                    billersCode.add(billerCode);

                    Log.e("NAME",billerName.toString());
                    Log.e("CODE",billerCode.toString());

                }
            }
            catch (Exception ex)
            {ex.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,billersName);
            spinner.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }

    public class GetParams extends AsyncTask<Void,Void,Void>
    {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("apptoken", "63R99549163A49A9T422D16219A36A45");
            params.put("retailerid", "9344241035");
            params.put("usermobile", "8678939278");
            params.put("ipaddress", "0.0.0.0");
            params.put("billers_type",billers_type);
            params.put("biller_code",billCode);

            try {
                jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictbbps/getbillers_param", "POST", params);

                Log.e("BBPS_PARAM", jsonObject.toString());
                paramHint=jsonObject.getString("param1");

                tilParam1.setHint(paramHint);


//                tipParam1.setHint(param1);


            }
            catch (Exception ex)
            {ex.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            progressDialog.dismiss();
        }
    }


    public class ProcessPay extends AsyncTask<Void,Void,Void>
    {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();

//                    "param2": "",
//                    "param3": "",
//                    "param4": "",
//                    "param5": "",
//                    "refid_return": "",
//                    "transid_return": ""

            params.put("apptoken", "63R99549163A49A9T422D16219A36A45");
            params.put("retailerid", "9344241035");
            params.put("usermobile", "8678939278");
            params.put("ipaddress", "0.0.0.0");
            params.put("billers_type",billers_type);
            params.put("biller_code",billCode);
            params.put("walkin_mobileno", mobile);
            params.put("walkin_emailid", email);
            params.put("billno","");
            params.put("billdate", "");
            params.put("billduedate", "");
            params.put("amount",amt);
            params.put(paramHint, param1);
            params.put("name","");
            params.put("param2", "");
            params.put("param3", "");
            params.put("param4", "");
            params.put("param5", "");
            params.put("refid_return", "");
            params.put("transid_return", "");



            try {
                jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictbbps/billpay", "POST", params);

                Log.e("BBPS_PAY", jsonObject.toString());

                status=jsonObject.getString("status");

                if(status.equals("true"))
                {
                    Toast.makeText(getActivity(), "Thanks for using DAY ATM", Toast.LENGTH_SHORT).show();
                    Intent actMain=new Intent(getActivity(),SuccessActivity.class);
                    SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
                    String userid = sh1.getString("userid", "");
                    String pin=sh1.getString("pin","");
                    String loc=sh1.getString("loc","");

                    actMain.putExtra("loc",loc);
                    actMain.putExtra("amt",amt);
                    actMain.putExtra("txnId","000");
                    actMain.putExtra("mode","BBPS");
                    //Toast.makeText(getActivity, "Mode"+mode, Toast.LENGTH_SHORT).show();

                    getActivity().finish();
                    startActivity(actMain);
                }

                if(status.equals("false"))
                {
                    Toast.makeText(getActivity(), "Transaction Pending!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Please contact customer care", Toast.LENGTH_LONG).show();
                    Intent actHome=new Intent(getActivity(),HomeActivity.class);
                    startActivity(actHome);

                }




                //tilParam1.setHint(param1);

//                tipParam1.setHint(param1);


            }
            catch (Exception ex)
            {ex.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            progressDialog.dismiss();
        }
    }

}