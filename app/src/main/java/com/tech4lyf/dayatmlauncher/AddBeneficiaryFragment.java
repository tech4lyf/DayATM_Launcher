package com.tech4lyf.dayatmlauncher;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBeneficiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBeneficiaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddBeneficiaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddBeneficiaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddBeneficiaryFragment newInstance(String param1, String param2) {
        AddBeneficiaryFragment fragment = new AddBeneficiaryFragment();
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
    ProgressDialog progressDialog;
    JSONParser jsonParser;
    ArrayList<String> stringsName = new ArrayList<>();
    ArrayList<String> stringsId = new ArrayList<>();
    Spinner spinner;

    Button btnAddben,btnVerify;
    TextInputEditText tipbenname,tipbenphone,tipbenacno,tipbenifsc,tipEmail;

    String bankId,bankName;

    String apptoken="",userid="",custmob="",ip="",custid="",benmobile="",benname="",benacno="",benifsc="",benbank="",email="";

    String status="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_add_beneficiary, container, false);
        progressDialog=new ProgressDialog(getActivity());
        jsonParser=new JSONParser(getActivity());
        spinner=(Spinner)rootView.findViewById(R.id.spinnerBankName);
        btnAddben=(Button)rootView.findViewById(R.id.btnAddBen);
        
        tipbenname=(TextInputEditText)rootView.findViewById(R.id.tipBenName);
        tipbenphone=(TextInputEditText)rootView.findViewById(R.id.tipBenPhone);
        tipbenacno=(TextInputEditText)rootView.findViewById(R.id.tipBenAcc);
        tipbenifsc=(TextInputEditText)rootView.findViewById(R.id.tipIfsc);
        tipEmail=(TextInputEditText)rootView.findViewById(R.id.tipDMTEmail);
        btnVerify=(Button)rootView.findViewById(R.id.btnVerifyBen);

        Bundle bundle = this.getArguments();

        if(bundle != null){
            // handle your code here.
            custmob=bundle.getString("phone");
            custid=bundle.getString("custid");
        }

        SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        custmob = sh.getString("phone", "");
        ip=sh.getString("ipaddress","");

        SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
        userid = sh1.getString("userid", "");

        apptoken=getString(R.string.dmt_app_token);


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

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benacno=tipbenacno.getText().toString();
                benifsc=tipbenifsc.getText().toString();
                if(benacno.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter Bank Account Number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(benifsc.isEmpty())
                    {
                        Toast.makeText(getActivity(), "Please enter IFSC Code", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        new VerifyBenName().execute();
                    }
                    }

            }
        });





        btnAddben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                benname=tipbenname.getText().toString();
                benmobile=tipbenphone.getText().toString();
                benacno=tipbenacno.getText().toString();
                benifsc=tipbenifsc.getText().toString();
                email=tipEmail.getText().toString();



                if(benname.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter Beneficiary name.", Toast.LENGTH_SHORT).show();
                }

                else if(benmobile.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter Beneficiary Mobile.", Toast.LENGTH_SHORT).show();
                }
                else if(benacno.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter Beneficiary Account number..", Toast.LENGTH_SHORT).show();
                }
                else if(benifsc.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter Beneficiary IFSC code", Toast.LENGTH_SHORT).show();
                }
                else if(bankName.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please select Beneficiary Bank", Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter Beneficiary Email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    new AddBen().execute();


                }
            }
        });


        new GetBanks().execute();

        return rootView;
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
//            connect();
            progressDialog.dismiss();


        }
    }

    class VerifyBenName extends AsyncTask<Void,Void,Void>
    {

        JSONParser jsonParser;
        HashMap<String,String> params;
        JSONObject jsonObject;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Validating your Beneficiary Account...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            jsonParser=new JSONParser();
            params =new HashMap<>();
            params.put("","");
            try {
                jsonObject = jsonParser.makeHttpRequest("https://payu.startrecharge.in/Bank/AccountVerify?token=wLVPDkDPFcPyIPJv5dgw2gDCXTyoJO&Account=" + benacno + "&IFSC=" + benifsc, "POST", params);
            }
            catch (Exception ex)
            {
                Log.e("MSG","Something went wrong");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();

            try {
            if(jsonObject!=null) {
                if (jsonObject.getString("Status").equals("Success")) {
                    Toast.makeText(getActivity(), "Account verified Successfully!", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                    String acName = jsonObject1.getString("BeneficiaryName");
                    tipbenname.setText(acName);
                } else {
                    Toast.makeText(getActivity(), "Invalid Account Number  or IFSC Code!", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();

            }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class AddBen extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading Banks...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("apptoken",getString(R.string.dmt_app_token));
            params.put("user_id", userid);
            params.put("custmobile", custmob);
            params.put("ipaddress", ip);
            params.put("custid", custid);
            params.put("benemobile", benmobile);
            params.put("benename", benname);
            params.put("beneaccno",benacno);
            params.put("benebankname",bankName);
            params.put("beneifsc",benifsc);
            params.put("beneemail",email);

            JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictdmt2/benereg","POST",params);



            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            if(status.equals("true"))
            {
                Toast.makeText(getActivity(), "Beneficiary added successfully", Toast.LENGTH_SHORT).show();
                Fragment fragment = new DMTFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            else
            {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                Fragment fragment = new DMTFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

        }
    }

}