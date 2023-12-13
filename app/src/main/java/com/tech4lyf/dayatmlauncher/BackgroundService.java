package com.tech4lyf.dayatmlauncher;

import android.app.Service;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    String refId,loc;
    int amount;
    String cmd="";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

//        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                JSONParser jsonParser=new JSONParser();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("refId",refId);
                Log.e("BGREFID","\"" +refId+"\"");
                JSONObject jsonObject=jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictupi/atmtxnstatus", "POST", params);
                try {
                    String test=jsonObject.getString("status");
                    Log.e("Response",test);
                        Log.e("TXNSTATUS",test);
                    if(test.equals("success"))
                        {
                            Toast.makeText(getApplicationContext(), "Transaction Success", Toast.LENGTH_SHORT).show();
                            Log.e("STATUS","Transaction Success");
                            Intent intent = new Intent();
                            intent.setClassName("com.tech4lyf.ndr2000", "com.tech4lyf.ndr2000.MyBroadcastReceiver");
                            intent.setAction("com.tech4lyf.ndr2000.MyBroadcastReceiver");
                            intent.putExtra("amount","1000");
                            //sendBroadcast(intent);
                            BackgroundService.this.stopSelf();
                            MainActivity mainActivity=new MainActivity();
                            try {
                                Intent actDIs=new Intent(getApplicationContext(),MQTTDispense.class);
                                actDIs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                actDIs.putExtra("amount", amount);
                                actDIs.putExtra("refId",refId);
                                actDIs.putExtra("loc",loc);
                                actDIs.putExtra("mode","QR_CODE");
                                startActivity(actDIs);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//
                            BackgroundService.this.stopSelf();
                        }

                        else if(test.equals("pending"))
                        {
                            //Toast.makeText(getApplicationContext(), "Transaction Pending!", Toast.LENGTH_SHORT).show();
                            Log.e("STATUS","Transaction Pending");
                        }
                        else if(test.equals("failed"))
                        {
                            Toast.makeText(getApplicationContext(), "Transaction Failed!", Toast.LENGTH_SHORT).show();
                            Log.e("STATUS","Transaction Pending");
                            BackgroundService.this.stopSelf();
                        }


                    }catch (JSONException err){
                    Log.d("Error", err.toString());
            }


                handler.postDelayed(runnable, 2000);
            }
        };

        handler.postDelayed(runnable, 10000);


    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        Log.e("SERVICE","TERMINATED");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        Log.e("SERVICE","STARTED");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            refId = intent.getStringExtra("refId");
            amount = Integer.parseInt(intent.getStringExtra("amount"));
            loc=intent.getStringExtra("loc");
            Log.e("Amount",""+amount);

        }
        return START_STICKY;
    }




    private void output(final String txt) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                output.setText(output.getText().toString() + "\n\n" + txt);
////              Toast.makeText(PayActivity.this, txt, Toast.LENGTH_SHORT).show();
//
//
//
//
//            }
//        });
//        Toast.makeText(context, ""+txt, Toast.LENGTH_SHORT).show();
        Log.e("OP",txt);
    }
}