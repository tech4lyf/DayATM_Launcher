package com.tech4lyf.dayatmlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    VideoView videoView;
    WebView mWebView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
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
    CardView cardStart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
        rootView = inflater.inflate(R.layout.fragment_video, container, false);


        cardStart = (CardView)rootView.findViewById(R.id.cardStart);
        videoView = (VideoView) rootView.findViewById(R.id.vidIntro);

//        videoView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

//        Uri video = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.intro);
//
//        //Uri video = Uri.parse("http://clients.tech4lyf.com/dayatm/intro.mp4");
//        videoView.setMediaController(new MediaController(getActivity().getApplicationContext()));
//        videoView.setVideoURI(video);
//
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
//
//        if(videoView.isPlaying())
//        {
//
//        }
//        else {
//            videoView.start();
//        }

//        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/media/1.mp4");

        Uri video=Uri.parse("android.resource://com.tech4lyf.dayatmlauncher/raw/intro");
        MediaController mediaController= new MediaController(getActivity().getApplicationContext());
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.requestFocus();
        videoView.start();

        cardStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                cardStart.setEnabled(false);
//                    Toast.makeText(getActivity().getApplicationContext(), "Welcome to DAY ATM", Toast.LENGTH_SHORT).show();
//                videoView.stopPlayback();
                    Intent i = new Intent(getActivity(), Selector_Activity.class);
                    startActivity(i);
                    ((Activity) getActivity()).overridePendingTransition(0, 0);

            }
        });


//        return inflater.inflate(R.layout.fragment_video, container, false);
        return rootView ;


    }
}