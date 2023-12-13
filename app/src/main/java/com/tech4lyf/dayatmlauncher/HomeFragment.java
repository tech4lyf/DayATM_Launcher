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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    JSONParser jsonParser;
    ProgressDialog progressDialog;
    String status,phone,ip,userid;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    CardView cardDeposit,cardWithdraw,cardWallet,cardBBPS;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog=new ProgressDialog(getActivity());
        jsonParser=new JSONParser();


        cardDeposit=(CardView)rootView.findViewById(R.id.cardDeposit);
        cardWithdraw=(CardView)rootView.findViewById(R.id.cardWithdraw);
        cardWallet=(CardView)rootView.findViewById(R.id.cardWALLET);
        cardBBPS=(CardView)rootView.findViewById(R.id.cardBBPS);

        SharedPreferences sh = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        phone = sh.getString("phone", "");
        ip = sh.getString("ipaddress", "");

        cardDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new CheckDMT().execute();
                Intent actDep=new Intent(getActivity(),INBNFAcceptActivity.class);
                startActivity(actDep);


            }
        });

        cardWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new CheckDMT().execute();
                Fragment fragment = new WalletBenFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });

        cardWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new WithdrawFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardBBPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

//        return inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    class CheckDMT extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            SharedPreferences sh1 = getActivity().getSharedPreferences("MerchantData", MODE_PRIVATE);
            userid = sh1.getString("userid", "");

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("apptoken", getString(R.string.dmt_app_token));
            params.put("user_id", userid);
            params.put("custmobile", phone);
            params.put("ipaddress", ip);

            JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictdmt/remitter","POST",params);
            try {
                status=jsonObject.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            progressDialog.dismiss();
            if(status.equals("true"))
            {
                Fragment fragment = new DMTFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //Toast.makeText(getActivity(), "Functionality not available", Toast.LENGTH_SHORT).show();
            }




        }
    }
}