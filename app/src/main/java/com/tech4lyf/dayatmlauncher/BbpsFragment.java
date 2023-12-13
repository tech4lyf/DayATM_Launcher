package com.tech4lyf.dayatmlauncher;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BbpsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BbpsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    CardView cardMobRecharge,cardFastag,cardLpg,cardElectricity,cardWater,cardGas,cardLandline,cardCcard,cardLoan,cardInsurance,cardEntertainment;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BbpsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BbpsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BbpsFragment newInstance(String param1, String param2) {
        BbpsFragment fragment = new BbpsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bbps, container, false);

        cardMobRecharge=(CardView)rootView.findViewById(R.id.cardMobRecharge);
        cardFastag=(CardView)rootView.findViewById(R.id.cardFastag);
        cardElectricity=(CardView)rootView.findViewById(R.id.cardElectricity);
        cardLpg=(CardView)rootView.findViewById(R.id.cardLPG);
        cardWater=(CardView)rootView.findViewById(R.id.cardWater);
        cardGas=(CardView)rootView.findViewById(R.id.cardGas);
        cardLandline=(CardView)rootView.findViewById(R.id.cardLandline);
        cardCcard=(CardView)rootView.findViewById(R.id.cardCcard);
        cardLoan=(CardView)rootView.findViewById(R.id.cardLoan);
        cardEntertainment=(CardView)rootView.findViewById(R.id.cardEntertainment);
        cardInsurance=(CardView)rootView.findViewById(R.id.cardInsurance);

        cardMobRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "MOBILE POSTPAID");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardFastag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "FASTAG");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "ELECTRICITY");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardLpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "LPG GAS");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "WATER");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "GAS");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardLandline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "LANDLINE");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardCcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "CREDIT CARD");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "LOAN");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "INSURANCE");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new BbpsPayFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString( "billers_type" , "ENTERTAINMENT");
                fragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.fragmentMain, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }
}