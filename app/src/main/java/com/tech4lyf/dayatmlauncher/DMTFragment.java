package com.tech4lyf.dayatmlauncher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
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
import com.example.numpad.NumPad;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DMTFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DMTFragment extends Fragment {

    JSONParser jsonParser;
    ArrayList<String> stringsName = new ArrayList<>();
    ArrayList<String> stringsId = new ArrayList<>();
    Spinner spinner;
    String benId="",amt="",custId="",userid="";
    Button btnNextDep;
    TextInputEditText tipAmount;

    TextView tvAddBen;

    NumberKeyboard numberKeyboard;
    ProgressDialog progressDialog;

    String phone="",balance="",ip="",result="";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DMTFragment() {
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

        rootView=inflater.inflate(R.layout.fragment_d_m_t, container, false);

        jsonParser=new JSONParser();
//        new GetBen().execute();
        new CheckRemitter().execute();

        spinner=(Spinner)rootView.findViewById(R.id.spinnerBen);
        tipAmount=(TextInputEditText)rootView.findViewById(R.id.tipAmount);
        numberKeyboard=(NumberKeyboard)rootView.findViewById(R.id.numkeyboardDMT);

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
                        else {
                            Intent actCash = new Intent(getActivity(), INBNFAcceptActivity.class);
                            actCash.putExtra("amt", amt);
                            actCash.putExtra("benId", benId);
                            actCash.putExtra("custId", custId);

                            startActivity(actCash);
                        }

                    }
                }
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
}