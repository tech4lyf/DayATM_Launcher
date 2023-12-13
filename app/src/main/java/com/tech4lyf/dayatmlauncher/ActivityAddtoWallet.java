package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Set;

public class ActivityAddtoWallet extends AppCompatActivity {

    String amt,benId,custId;
    TextView tvDepAmount;
    static TextView tvInsAmount;
    static long dec;
    static int amtins=0;
    static Button btnDep;
    ProgressDialog progressDialog;
    JSONObject jsonObject;

    JSONParser jsonParser;
    String userid="",phone="",ip="",pin="",loc="";
    static double txnAmt=0;
    static int printAmt=0;

    static TextView tvHundred,tvTwoHundred,tvFiveHundred,tvTwoThousand;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private static UsbService usbService;
    private ActivityAddtoWallet.MyHandler mHandler;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<ActivityAddtoWallet> mActivity;

        public MyHandler(ActivityAddtoWallet activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    // mActivity.get().display.append(data);
                    Log.e("Data Rcvd",(String)data);

                    long dec1 =Hextonumber(data);
                    dec=dec1;

                    Log.e("Conv",""+dec1);

                    if(dec==68)
                    {
                        Log.e("Note Detected","Hundred");
                        new ActivityAddtoWallet.Accept().execute();
                        amtins=amtins+100;
                        printAmt=printAmt+100;
                        int tmp=0;
                        int h=Integer.parseInt(tvHundred.getText().toString());
                        tmp=h+1;
                        tvHundred.setText(""+tmp);

                        updateAmt(amtins);
                    }
                    else if(dec==72)
                    {
                        Log.e("Note Detected","Two Hundred");
                        new ActivityAddtoWallet.Accept().execute();
                        amtins=amtins+200;

                        printAmt=printAmt+200;
                        int tmp=0;
                        int h=Integer.parseInt(tvTwoHundred.getText().toString());
                        tmp=h+1;
                        tvTwoHundred.setText(""+tmp);

                        updateAmt(amtins);
                    }
                    else if(dec==69)
                    {
                        Log.e("Note Detected","Five Hundred");
                        new ActivityAddtoWallet.Accept().execute();
                        amtins=amtins+500;

                        printAmt=printAmt+500;
                        int tmp=0;
                        int h=Integer.parseInt(tvFiveHundred.getText().toString());
                        tmp=h+1;
                        tvFiveHundred.setText(""+tmp);
                        updateAmt(amtins);
                    }

                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    public static long Hextonumber(String hexval)
    {
        Long number = 0l;
        try {
            hexval = "0x" + hexval;

//      String decimal="0x00000bb9";
            if (!hexval.isEmpty()) {

                number = Long.decode(hexval);
            }
        }
        catch (Exception ex){ex.toString();}
//.......       System.out.println("String [" + hexval + "] = " + number);
        return number;
        //3001
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_wallet);
        mHandler = new ActivityAddtoWallet.MyHandler(this);
        progressDialog=new ProgressDialog(this);
        jsonParser=new JSONParser(this);
        amt="";
        amtins=0;

        tvHundred=(TextView)findViewById(R.id.tvHundredNotes);
        tvTwoHundred=(TextView)findViewById(R.id.tvTwoHundredNotes);
        tvFiveHundred=(TextView)findViewById(R.id.tvFiveHundredNotes);
        tvTwoThousand=(TextView)findViewById(R.id.tvTwoThousandNotes);

        tvDepAmount=(TextView)findViewById(R.id.tvAmounttoDep);
        tvInsAmount=(TextView)findViewById(R.id.tvAmountInserted);
        btnDep=(Button)findViewById(R.id.btnDepositSend);

//        amt=getIntent().getStringExtra("amt");
//        benId=getIntent().getStringExtra("benId");
//        custId=getIntent().getStringExtra("custId");
//        tvDepAmount.setText(amt);



        btnDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityAddtoWallet.Transfer().execute();
            }
        });


        Toast.makeText(this, "Amount:"+amt, Toast.LENGTH_SHORT).show();

//        accetpCash(amt);

        new ActivityAddtoWallet.GetCash().execute();


    }

    void accetpCash(String amt)
    {

    }

    class Transfer extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait we're transferring your money...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String, String> params = new HashMap<String, String>();
            SharedPreferences sh = getSharedPreferences("UserData", MODE_PRIVATE);
            phone = sh.getString("phone", "");
            ip=sh.getString("ipaddress","");

            SharedPreferences sh1 = getSharedPreferences("MerchantData", MODE_PRIVATE);
            userid = sh1.getString("userid", "");
            pin=sh1.getString("pin","");
            loc=sh1.getString("loc","");


            params.put("mobile", phone);
            params.put("ipaddress", ip);
            params.put("debit", "0");
            txnAmt=100;
            params.put("credit", String.valueOf((int)txnAmt));
            params.put("remark","Amount Added");


            try {
                jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictuser/amountcrdr", "POST", params);

                Log.e("DMT", jsonObject.toString());
            }
            catch (Exception ex)
            {ex.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            progressDialog.dismiss();
            String status="";
            String txnStatus="";
            String txnId="";
            try {
                status=jsonObject.getString("status");
//                txnStatus=jsonObject.getString("trans_status");
                txnId=jsonObject.getString("transid");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(status.equals("true"))
            {
                if(status.equals("true")) {
                    Toast.makeText(ActivityAddtoWallet.this, "Your money added to the walled Successfully!", Toast.LENGTH_SHORT).show();
                    Intent actHome = new Intent(ActivityAddtoWallet.this, SuccessActivity.class);
                    actHome.putExtra("txnId",txnId);
                    actHome.putExtra("amt",(int)txnAmt);
                    actHome.putExtra("loc",loc);
                    actHome.putExtra("mode","DMT");

                    startActivity(actHome);
                }
                else if(status.equals("success")) {
                    Toast.makeText(ActivityAddtoWallet.this, "Transaction Successful!", Toast.LENGTH_SHORT).show();
                    Intent actHome = new Intent(ActivityAddtoWallet.this, SuccessActivity.class);

                    startActivity(actHome);
                }
                else if(status.equals("false")) {
                    Toast.makeText(ActivityAddtoWallet.this, "Transaction Failed!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ActivityAddtoWallet.this, "Please contact customer care", Toast.LENGTH_LONG).show();
                    Intent actHome = new Intent(ActivityAddtoWallet.this, HomeActivity.class);
                    startActivity(actHome);
                }
                else{
                    Toast.makeText(ActivityAddtoWallet.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ActivityAddtoWallet.this, "Please contact customer care", Toast.LENGTH_LONG).show();
                    Intent actHome = new Intent(ActivityAddtoWallet.this, HomeActivity.class);
                    startActivity(actHome);
                }
            }
            else if(status.equals("false"))
            {
                Toast.makeText(ActivityAddtoWallet.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                Toast.makeText(ActivityAddtoWallet.this, "Please contact customer care", Toast.LENGTH_LONG).show();
                Intent actHome=new Intent(ActivityAddtoWallet.this,HomeActivity.class);
                startActivity(actHome);
            }
            else
            {
                Toast.makeText(ActivityAddtoWallet.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                Intent actHome=new Intent(ActivityAddtoWallet.this,HomeActivity.class);
                startActivity(actHome);
            }


        }
    }

    class GetCash extends AsyncTask<Void,Void,Void>
    {


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
//                    Thread.sleep(2000);
                // if UsbService was correctly binded, Send data
                Log.e("Data","Hi");
                byte[] inp = new byte[]{48};
                Log.d("msg", inp.toString());
                if (usbService != null) {
                    // if UsbService was correctly binded, Send data
                    usbService.write(inp);

                    usbService.write(inp);
                    Thread.sleep(50);
                    inp = new byte[]{2};
                    usbService.write(inp);
                    Thread.sleep(50);


                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


    }
    static void updateAmt(double amt)
    {

        if(amt<100)
        {

            amt=amt-10;
            txnAmt=amt;
            tvInsAmount.setText(""+amt);
            btnDep.setText("Deposit ("+amt+")");
        }

        else if(amt<2000 && amt>=100)
        {

            amt=amt-10;
            txnAmt=amt;
            tvInsAmount.setText(""+amt);
            btnDep.setText("Deposit ("+amt+")");
        }
        else if(amt<=25000 && amt>=2000)
        {
            double commission=(double)(amt/100)*1;
            amt=amt-commission;
            txnAmt=amt;
            tvInsAmount.setText(""+amt);
            btnDep.setText("Deposit ("+amt+")");

        }


    }

    static class Accept extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
//                    Thread.sleep(2000);
                // if UsbService was correctly binded, Send data
                Log.e("Data", "Hi");
                byte[] inp = new byte[]{48};
                Log.d("msg", inp.toString());
                if (usbService != null) {
                    Thread.sleep(50);
                    inp = new byte[]{2};
                    usbService.write(inp);

                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return null;
        }
    }
}