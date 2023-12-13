package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class NDEDispense extends AppCompatActivity {

    int amount = 0;
    String txnId="";
    String loc="";




    
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
    private UsbService usbService;
    private MyHandler mHandler;

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
        private final WeakReference<NDEDispense> mActivity;

        public MyHandler(NDEDispense activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    // mActivity.get().display.append(data);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndedispense);

        mHandler = new MyHandler(this);
        amount = getIntent().getIntExtra("amount", 0);
        txnId = getIntent().getStringExtra("txnId");



        new Disp().execute();
    }


    public int countDigit(int n) {
        int count = 0;
        while (n != 0) {
            n = n / 10;
            ++count;
        }
        return count;
    }

    public byte calcChecksum(byte[] data) {

        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: private byte calcChecksum(byte[] data)

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte num1 = 0;
        byte num1 = 0;
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: foreach (byte num2 in data)
        for (byte num2 : data) {
            num1 += num2;

        }
//        Toast.makeText(usbService, "CC : " + num1, Toast.LENGTH_SHORT).show();
        return num1;


    }

    public class Disp extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int _amt = amount;



            String amt = String.valueOf(amount / 100);
            int n = countDigit(Integer.parseInt(amt));
            if (n == 1) {
                amt = "000" + amt;
            } else if (n == 2) {
                amt = "00" + amt;
            } else if (n == 3) {
                amt = "0" + amt;
            } else if (n == 4) {
                amt = amt;
            } else {
                amt = "0000";
            }
            Log.e("Amt", amt);
            byte[] numArray = new byte[4];
            char[] charArray = amt.toCharArray();
            Log.e("Amount", "" + amt);
            for (int i = 0; i < charArray.length; i++) {
                numArray[i] = (byte) charArray[i];
                Log.e("BB", "" + charArray[i]);
            }

            byte[] inp = new byte[]{2, 48, 48, 66, numArray[0], numArray[1], numArray[2], numArray[3], calcChecksum(new byte[]{(byte) 2, (byte) 48, (byte) 48, (byte) 66, (byte) numArray[0], (byte) numArray[1], (byte) numArray[2], (byte) numArray[3]}), 3};


            Log.d("msg", inp.toString());
            if (usbService != null) {
                // if UsbService was correctly binded, Send data

                usbService.write(inp);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            loc="Day ATM Head Office";
            sendSMS(amount,txnId,loc);

//            Intent serviceIntent = new Intent(getApplicationContext(),PrintService.class);
//            serviceIntent.putExtra("amount", amount);
//            serviceIntent.putExtra("refId",txnId);
//            startService(serviceIntent);
        }
    }

    void sendSMS(int amt,String txnId,String loc) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timeStamp = sdf.format(new Date());

        JSONParser jsonParser = new JSONParser();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("refId", "");
        try {
            JSONObject jsonObject = jsonParser.makeHttpRequest("https://pay4sms.in/sendsms/?token=a77e8bff54b875afc733b50566bfde4c&credit=2&sender=DAYATM&message=Dear Customer, Thanks for using DAYATM. Your transaction of Rs. " + amt + " is done at " + loc + " on " + timeStamp + " and your Transaction ID is " + txnId + "&number=8678939278&templateid=1707162477970070852", "GET", params);
            JSONObject jsonObject1 = jsonParser.makeHttpRequest("https://pay4sms.in/sendsms/?token=a77e8bff54b875afc733b50566bfde4c&credit=2&sender=DAYATM&message=Dear Customer, Thanks for using DAYATM. Your transaction of Rs. " + amt + " is done at " + loc + " on " + timeStamp + " and your Transaction ID is " + txnId + "&number=9361556034&templateid=1707162477970070852", "GET", params);
        }

        catch(Exception ex)
        {
            Log.e("SMSEXC",ex.toString());
        }
//        Intent mainAct=new Intent(NDEDispense.this,PrintActivity.class);
//        mainAct.putExtra("amount", amount);
//        mainAct.putExtra("refId",txnId);
//        startActivity(mainAct);
    }
}
