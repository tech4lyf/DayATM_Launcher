package com.tech4lyf.dayatmlauncher;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletBenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletBenFragment extends Fragment {

    JSONParser jsonParser;
    ArrayList<String> stringsName = new ArrayList<>();
    ArrayList<String> stringsId = new ArrayList<>();
    Spinner spinner;
    String benId="",amt="",custId="",userid="",pin="",loc="";
    Button btnNextDep;
    TextInputEditText tipAmount;
    TextView tvWalBal;
    String wdReqStatus="false";
    TextInputEditText tipOTP;
    TextInputLayout tilOTP;
    TextView tvAddBen;
    Button btnVerOTP;
    String otp="";
    String otpStatus="false";
    String txnId="";
    String txnStatus="false",status="failed",status_desc="";

    NumberKeyboard numberKeyboard,numberKeyboard1;
    ProgressDialog progressDialog;

    String phone="",balance="",ip="",result="";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WalletBenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DMTFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DMTFragment newInstance(String param1, String param2) {
        DMTFragment fragment = new DMTFragment();
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

        rootView=inflater.inflate(R.layout.fragment_wallet_ben, container, false);

        jsonParser=new JSONParser();
//        new GetBen().execute();
        new GetBalance().execute();

        spinner=(Spinner)rootView.findViewById(R.id.spinnerBen);
        tipAmount=(TextInputEditText)rootView.findViewById(R.id.tipAmount);
        tvWalBal=(TextView)rootView.findViewById(R.id.tvWalBal);
        tipOTP=(TextInputEditText)rootView.findViewById(R.id.tipOTP);
        tilOTP=(TextInputLayout)rootView.findViewById(R.id.tilOTP);
        numberKeyboard=(NumberKeyboard)rootView.findViewById(R.id.numkeyboardDMT);
        numberKeyboard1=(NumberKeyboard)rootView.findViewById(R.id.numkeyboardOTP);
        btnVerOTP=(Button)rootView.findViewById(R.id.btnDepOTP);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                Log.e("ID",""+pos);
                if(pos==0){
                    benId="0";
                }
                else {
                    benId = stringsId.get(pos - 1);

                }
                Log.e("BENID", "" + benId);

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

        numberKeyboard1.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int i) {

                String tmp = tipOTP.getText().toString();
                tipOTP.setText(tmp + "" + i);

            }

            @Override
            public void onLeftAuxButtonClicked() {

            }

            @Override
            public void onRightAuxButtonClicked() {

                String text = tipOTP.getText().toString();
                if (!text.isEmpty()) {
                    text = text.substring(0, text.length() - 1);
                    tipOTP.setText(text);
                }

            }

            @Override
            public void onModifierButtonClicked(int i) {

            }
        });
        btnNextDep=(Button)rootView.findViewById(R.id.btnDepNext);
        btnNextDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(benId.equals("0"))
                {
                    Toast.makeText(getActivity(), "Please select a Beneficiary!", Toast.LENGTH_SHORT).show();
                }
                else {

                    amt = tipAmount.getText().toString();

                    if (amt.equals("")) {
                        Toast.makeText(getActivity(), "Please enter valid amount", Toast.LENGTH_SHORT).show();
                    } else {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("amt", amt);
                        if(Integer.parseInt(amt)<200)
                        {
                            Toast.makeText(getActivity(), "Deposit Amount must be more than Rs. 200", Toast.LENGTH_SHORT).show();
                        }
                        else if(Integer.parseInt(amt)>Double.parseDouble(balance))
                        {
                            Toast.makeText(getActivity(), "Amount requested is larger than the available balance Rs."+balance, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new WithdrawMoney().execute();
                        }

                    }
                }
            }
        });

        btnVerOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp=tipOTP.getText().toString();
                new VerifyOTP().execute();
            }
        });

        tvAddBen=(TextView)rootView.findViewById(R.id.tvAddBen);
        tvAddBen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("phone",phone);
                bundle.putString("custid",custId);

                Fragment fragment = new AddBeneficiaryFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


//        return inflater.inflate(R.layout.fragment_d_m_t, container, false);
        return rootView;
    }

    class GetBen extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            stringsName.add("Select a Beneficiary");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String, String> params = new HashMap<String, String>();
            SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
            phone = sh.getString("phone", "");
            ip=sh.getString("ipaddress","");

            SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
            userid = sh1.getString("userid", "");
            loc=sh1.getString("loc","");

            params.put("custmobile", phone);
            params.put("user_id",userid);
            params.put("apptoken",getString(R.string.dmt_app_token));
            params.put("ipaddress",ip);

            JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictdmt2/remitter","POST",params);
            Log.e("Data",jsonObject.toString());
            try {
                JSONArray jsonArray=jsonObject.getJSONArray("remitter");
                Log.e("CUSTID",jsonArray.toString());
                JSONObject jsonObject1=jsonArray.getJSONObject(0);
                custId=jsonObject1.getString("custid");
                Log.e("CUSTID",custId);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArray=jsonObject.getJSONArray("beneficiary");

                Log.e("Ben",jsonArray.toString());

                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                    String name = jsonObject1.optString("benename").toString();
                    String id = jsonObject1.optString("beneid").toString();


                    stringsName.add("["+id+"] "+name);
                    stringsId.add(id);

                    Log.e("SpinData",stringsName.toString());
                    Log.e("SpinID",stringsId.toString());

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_item,stringsName);
            spinner.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }

    class CheckRemitter extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Checking your Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();

            SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
            phone = sh.getString("phone", "");
            ip=sh.getString("ipaddress","");

            SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
            userid = sh1.getString("userid", "");


            params.put("custmobile", phone);
            params.put("user_id",userid);
            params.put("apptoken",getString(R.string.dmt_app_token));
            params.put("ipaddress",ip);

//            params.put("apptoken",getString(R.string.dmt_app_token));
//            params.put("user_id",userid);
//            params.put("custmobile",phone);
//            params.put("ipaddress",ip);

            JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictdmt2/remitter","POST",params);

            try {
                result=jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if(result.equals("register"))
            {
                Toast.makeText(getActivity(), "Please create an Account first", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Fragment fragment = new RegisterDMTFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
            else if(result.equals("found"))
            {

                progressDialog.dismiss();
                new GetBen().execute();

            }

        }
    }
    public class GetBalance extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Checking your Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String,String> params=new HashMap<>();
            SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
            phone = sh.getString("phone", "");
            ip=sh.getString("ipaddress","");

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
            tvWalBal.setText(""+balance);
            progressDialog.dismiss();
            new CheckRemitter().execute();
        }
    }

    public class WithdrawMoney extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("mobile", phone);
            params.put("ipaddress", ip);

            params.put("amount", amt);
            params.put("userid",userid);

            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictuser/amountwithdraw","POST",params);

            Log.e("WDResp",jsonObject.toString());
            try {
                wdReqStatus=jsonObject.getString("status");
                txnId=jsonObject.getString("transid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();

            if(wdReqStatus.equals("true"))
            {
                Toast.makeText(getActivity(), "Request Sent, Please enter OTP", Toast.LENGTH_SHORT).show();
                tilOTP.setVisibility(View.VISIBLE);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                requestFocus(tipOTP);
                numberKeyboard.setVisibility(View.GONE);
                numberKeyboard1.setVisibility(View.VISIBLE);
                btnVerOTP.setVisibility(View.VISIBLE);
                btnNextDep.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }


        }
    }

    public class VerifyOTP extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Verifying OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();
            SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);

            params.put("mobile", phone);
            params.put("userid",userid);
            params.put("ipaddress", ip);
            params.put("debit", amt);
            params.put("otp", otp);
            params.put("transid", txnId);
            params.put("remark", "Amount Debited");

            JSONParser jsonParser=new JSONParser();

            JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictuser/amountwithdrawverify","POST",params);
            try {
                otpStatus=jsonObject.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            if(otpStatus.equals("true"))
            {

                new SendMoney().execute();
            }

        }
    }

    public class SendMoney extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Sending money to the Beneficiary...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject jsonObject=null;
            JSONParser jsonParser=new JSONParser();
            HashMap<String, String> params = new HashMap<String, String>();
            SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
            phone = sh.getString("phone", "");
            ip=sh.getString("ipaddress","");

            SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
            userid = sh1.getString("userid", "");
            pin=sh1.getString("pin","");


            params.put("apptoken", getString(R.string.dmt_app_token));
            params.put("user_id", userid);
            params.put("custmobile", phone);
            params.put("ipaddress", ip);
            params.put("custid", custId);
            params.put("beneid", benId);
            int tAmt=Integer.parseInt(amt);
            int tAmt1=0;
            String _amt="0";
            if(tAmt>=200 && tAmt<=2000)
            {
                Log.e("Below 2000","-20");
                tAmt1=tAmt-20;
                _amt=String.valueOf(tAmt1);
            }
            else if(tAmt>=2001)
            {
                Log.e("Above 2000","-20");
                int p=tAmt/100;
                tAmt1=tAmt-p;
                _amt=String.valueOf(tAmt1);
            }

            params.put("amount", String.valueOf(tAmt));
//            params.put("amount", _amt);
            params.put("mpin", pin);

            try {
                jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictdmt2/transfer", "POST", params);

                Log.e("DMT", jsonObject.toString());
                txnStatus=jsonObject.getString("status");
                status=jsonObject.getString("trans_status");
                status_desc=jsonObject.getString("status_desc");


            }
            catch (Exception ex)
            {ex.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            if(txnStatus.equals("true"))
            {
                if(!status.equals("failed")) {

                    Intent actHome = new Intent(getActivity(), SuccessActivity.class);
                    actHome.putExtra("txnId", txnId);
                    Log.e("Amount to Print", "" + amt);
                    actHome.putExtra("amt", Integer.parseInt(amt));
                    actHome.putExtra("loc", loc);
                    actHome.putExtra("mode", "DMT");
                    startActivity(actHome);
                }
                else
                {

                    Toast.makeText(getActivity(), "Transaction Failed due to "+status_desc, Toast.LENGTH_SHORT).show();
                    Intent actHome = new Intent(getActivity(), MainActivity.class);
                    startActivity(actHome);
                }
            }
            else
            {

                Toast.makeText(getActivity(), "Transaction Failed!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Please contact customer care", Toast.LENGTH_LONG).show();
                Intent actHome = new Intent(getActivity(), MainActivity.class);
                startActivity(actHome);
            }

        }
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }




}