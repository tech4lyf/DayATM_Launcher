package com.tech4lyf.dayatmlauncher;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterDMTFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterDMTFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterDMTFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterDMTFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterDMTFragment newInstance(String param1, String param2) {
        RegisterDMTFragment fragment = new RegisterDMTFragment();
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
    TextInputEditText tipFName,tipLName,tipMail,tipAddress;
    String fname="",lname="",pincode="",address="",phone="",userid="",ip="",email="";
    Button btnSignup;
    JSONParser jsonParser;
    JSONObject jsonObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_register_d_m_t, container, false);

        tipFName=(TextInputEditText)rootView.findViewById(R.id.tipFname);
        tipLName=(TextInputEditText)rootView.findViewById(R.id.tipLname);
        tipMail=(TextInputEditText)rootView.findViewById(R.id.tipDMTmail);
//        tipAddress=(TextInputEditText)rootView.findViewById(R.id.tipAddress);

        btnSignup=(Button)rootView.findViewById(R.id.btnDmtReg);

        SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        phone = sh.getString("phone", "");
        ip=sh.getString("ipaddress","");

        SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
        userid = sh1.getString("userid", "");


        jsonParser=new JSONParser();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname=tipFName.getText().toString();
                lname=tipLName.getText().toString();
//                address=tipAddress.getText().toString();
//                pincode=tipPinCode.getText().toString();
                email=tipMail.getText().toString();
                new Register().execute();
            }
        });

        return rootView;

    }

    class Register extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {



            HashMap<String, String> params = new HashMap<String, String>();

            params.put("apptoken", getString(R.string.dmt_app_token));
            params.put("user_id", userid);
            params.put("ipaddress", ip);
            params.put("firstname", fname);
            params.put("lastname", lname);
            params.put("custmobile",phone);
            params.put("emailid",email);

            jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictdmt2/remitterreg","POST",params);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            String status_desc="";
            try {
                status_desc=jsonObject.getString("status_desc");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(status_desc.equals("Remitter Register Successfull"))
            {
                Toast.makeText(getActivity(), "Registration completed successfully!...", Toast.LENGTH_SHORT).show();
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