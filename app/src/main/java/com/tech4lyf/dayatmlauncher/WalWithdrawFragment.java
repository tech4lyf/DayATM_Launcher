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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalWithdrawFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalWithdrawFragment extends Fragment {

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
    String txnStatus="false";

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

    public WalWithdrawFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WalWithdrawFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalWithdrawFragment newInstance(String param1, String param2) {
        WalWithdrawFragment fragment = new WalWithdrawFragment();
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

        rootView=inflater.inflate(R.layout.fragment_wal_withdraw, container, false);

        SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
        userid = sh1.getString("userid", "");

        jsonParser=new JSONParser();
//        new GetBen().execute();
        new GetBalance().execute();

        tipAmount=(TextInputEditText)rootView.findViewById(R.id.tipWWAmount);
        tvWalBal=(TextView)rootView.findViewById(R.id.tvWalBal);
        tipOTP=(TextInputEditText)rootView.findViewById(R.id.tipWWOTP);
        tilOTP=(TextInputLayout)rootView.findViewById(R.id.tilWWOTP);
        numberKeyboard=(NumberKeyboard)rootView.findViewById(R.id.numkeyboardWWAmt);
        numberKeyboard1=(NumberKeyboard)rootView.findViewById(R.id.numkeyboardWWOTP);
        btnVerOTP=(Button)rootView.findViewById(R.id.btnWWDepOTP);

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

        btnNextDep=(Button)rootView.findViewById(R.id.btnWWNext);

        btnNextDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        });

        btnVerOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp=tipOTP.getText().toString();
                new VerifyOTP().execute();
            }
        });

        return rootView;


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
            if(otpStatus.equals("true"))
            {
                onWithdrawSuccess();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),"Transaciton Failed!", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    private void onWithdrawSuccess() {

        Intent actDIs=new Intent(getActivity().getApplicationContext(),MQTTDispense.class);
        actDIs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actDIs.putExtra("amount", Integer.parseInt(amt));
        actDIs.putExtra("refId",txnId);
        actDIs.putExtra("loc",loc);
        actDIs.putExtra("mode","ATM");
        startActivity(actDIs);
        getActivity().finish();

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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }



}