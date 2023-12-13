package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.fingpay.microatmsdk.data.MerchantLoginData;
import com.zj.usbsdk.UsbController;

import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private int[][] u_infor;
    static UsbController usbCtrl = null;
    static UsbDevice dev = null;

    final byte[][] byteCommands = {
            {0x1b, 0x40, 0x0a},// 复位打印机
            {0x0a}, //打印并走纸
            {0x1b, 0x4d, 0x00},// 标准ASCII字体
            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
            {0x1d, 0x21, 0x00},// 字体不放大
            {0x1d, 0x21, 0x11},// 宽高加倍
            {0x1b, 0x45, 0x00},// 取消加粗模式
            {0x1b, 0x45, 0x01},// 选择加粗模式
            {0x1b, 0x7b, 0x00},// 取消倒置打印
            {0x1b, 0x7b, 0x01},// 选择倒置打印
            {0x1d, 0x42, 0x00},// 取消黑白反显
            {0x1d, 0x42, 0x01},// 选择黑白反显
            {0x1b, 0x56, 0x00},// 取消顺时针旋转90°
            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°
            {0x0a, 0x1d, 0x56, 0x42, 0x01, 0x0a},//切刀指令
            {0x1b, 0x42, 0x03, 0x03},//蜂鸣指令
            {0x1b, 0x70, 0x00, 0x50, 0x50},//钱箱指令
            {0x10, 0x14, 0x00, 0x05, 0x05},//实时弹钱箱指令
            {0x1c, 0x2e},// 进入字符模式
            {0x1c, 0x26}, //进入中文模式
            {0x1f, 0x11, 0x04}, //打印自检页
            {0x1b, 0x63, 0x35, 0x01}, //禁止按键
            {0x1b, 0x63, 0x35, 0x00}, //取消禁止按键
            {0x1b, 0x2d, 0x02, 0x1c, 0x2d, 0x02}, //设置下划线
            {0x1b, 0x2d, 0x00, 0x1c, 0x2d, 0x00}, //取消下划线
            {0x1f, 0x11, 0x03}, //打印机进入16进制模式
    };

    final byte[][] byteCodebar = {
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
    };

    public boolean CheckUsbPermission(){
        if( dev != null ){
            if( usbCtrl.isHasPermission(dev)){

                return true;
            }
            else
            {
                Toast.makeText(this, "Printer permission needed!", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }


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
    private MyHandler myHandler;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(myHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.e("STATUS","RESUME");
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, new VideoFragment(), "videos").commit();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("STATUS","PAUSE");
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
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
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
        setContentView(R.layout.activity_main);

        SharedPreferences sh = getSharedPreferences("MerchantData", MODE_PRIVATE);
        String isFirstlaunch = sh.getString("isFirstlaunch", "");

        CheckUsbPermission();
        usbCtrl = new UsbController(this, mHandler);
        u_infor = new int[8][2];
        u_infor[0][0] = 0x1CBE;
        u_infor[0][1] = 0x0003;
        u_infor[1][0] = 0x1CB0;
        u_infor[1][1] = 0x0003;
        u_infor[2][0] = 0x0483;
        u_infor[2][1] = 0x5740;
        u_infor[3][0] = 0x0493;
        u_infor[3][1] = 0x8760;
        u_infor[4][0] = 0x0416;
        u_infor[4][1] = 0x5011;
        u_infor[5][0] = 0x0416;
        u_infor[5][1] = 0xAABB;
        u_infor[6][0] = 0x1659;
        u_infor[6][1] = 0x8965;
        u_infor[7][0] = 0x0483;
        u_infor[7][1] = 0x5741;
        usbCtrl.close();

        int  i = 0;
        for( i = 0 ; i < 8 ; i++ ){
            dev = usbCtrl.getDev(u_infor[i][0],u_infor[i][1]);
            if(dev != null)
                break;
        }

        if( dev != null ){
            if( !(usbCtrl.isHasPermission(dev))){
                //Log.d("usb调试","请求USB设备权限.");
                usbCtrl.getPermission(dev);
            }else{
               // Toast.makeText(getApplicationContext(), "Permission granted",
                 //       Toast.LENGTH_SHORT).show();

                new Accept().execute();

            }
        }

        if(isFirstlaunch.equals("0"))
        {

            new Accept().execute();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new VideoFragment(), "videos").commit();
        }
        else
        {
            new Accept().execute();
            Intent actMLogin=new Intent(MainActivity.this, RegisterMerchantActivity.class);
            startActivity(actMLogin);


        }


//        getSupportFragmentManager().beginTransaction().add(R.id.fragment, new VideoFragment(), "videos").commit();
//https://www.youtube.com/watch?v=x0FKZe_q9VU
    }

    @SuppressLint("HandlerLeak") private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbController.USB_CONNECTED:
//                    Toast.makeText(getApplicationContext(),"Perm",
//                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    class Accept extends AsyncTask<Void,Void,Void>
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
                    //usbService.write(inp);

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