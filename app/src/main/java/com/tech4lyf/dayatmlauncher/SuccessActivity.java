package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zj.command.sdk.Command;
import com.zj.command.sdk.PrintPicture;
import com.zj.command.sdk.PrinterCommand;
import com.zj.usbsdk.UsbController;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class SuccessActivity extends AppCompatActivity {

    CardView cardSMS,cardPrint;

    private int[][] u_infor;
    static UsbController usbCtrl = null;
    static UsbDevice dev = null;
    Button btn1;
    String damt="";

    String txnId,loc,phone,mode;
    int amount;
    boolean canPrint=false;


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
        Log.e("Status","Checking USB Permission");
        if( dev != null ){
            if( usbCtrl.isHasPermission(dev)){
                canPrint=true;
                Log.e("Status","Success");
                return true;
            }
            else
            {
                Log.e("Status","Failed");
                Toast.makeText(this, "Printer permission needed!", Toast.LENGTH_SHORT).show();
                canPrint=false;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        SharedPreferences sh1 = getSharedPreferences("DeviceData", MODE_PRIVATE);
        String amt1 = sh1.getString("amt", "");



        CheckUsbPermission();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loc=getIntent().getStringExtra("loc");
        amount=getIntent().getIntExtra("amt",0);
        txnId=getIntent().getStringExtra("txnId");
        mode=getIntent().getStringExtra("mode");

//        Toast.makeText(getApplicationContext(), "Mode:"+mode, Toast.LENGTH_SHORT).show();

        damt=String.valueOf(amount);
//        Toast.makeText(getApplicationContext(), ""+damt, Toast.LENGTH_SHORT).show();

        if(mode.equals("ATM") || mode.equals("QR_CODE") || mode.equals("AEPS") || mode.equals("WALLET")) {
            int tmpAmt = Integer.parseInt(amt1);
            tmpAmt = tmpAmt - amount;

            Toast.makeText(getApplicationContext(), tmpAmt + "-" + damt, Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences = getSharedPreferences("DeviceData", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("amt", String.valueOf(tmpAmt));
            myEdit.commit();
        }
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
        cardSMS = (CardView) findViewById(R.id.cardSMS);
        cardPrint = (CardView) findViewById(R.id.cardPrint);

        //new Dispense().execute();
        if(mode.equals("ATM") || mode.equals("QR_CODE") || mode.equals("AEPS") || mode.equals("WALLET")) {
//            new DispenseDual().execute();
            new DispenseSingle().execute();
        }
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
                Toast.makeText(getApplicationContext(), "Permission granted",
                        Toast.LENGTH_SHORT).show();
                canPrint=true;

            }
        }

        SharedPreferences sh = getSharedPreferences("UserData", MODE_PRIVATE);
        phone = sh.getString("phone", "");


        cardPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(canPrint==false)
                {
                    Toast.makeText(SuccessActivity.this, "Unable to Print", Toast.LENGTH_SHORT).show();
                }
                else {
                    usbCtrl.close();
                    int i = 0;
                    for (i = 0; i < 8; i++) {
                        dev = usbCtrl.getDev(u_infor[i][0], u_infor[i][1]);
                        if (dev != null)
                            break;
                    }

                    if (dev != null) {
                        if (!(usbCtrl.isHasPermission(dev))) {
                            //Log.d("usb调试","请求USB设备权限.");
                            usbCtrl.getPermission(dev);
                        } else {
                            Toast.makeText(getApplicationContext(), "Perm",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    Print_BMP();
                    Print_Ex();
                    Toast.makeText(SuccessActivity.this, "Printed successfully! Thank you!", Toast.LENGTH_SHORT).show();
                    Intent actHome = new Intent(SuccessActivity.this, MainActivity.class);
                    startActivity(actHome);
                }
            }
        });

        cardSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONParser jsonParser=new JSONParser(getApplicationContext());SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String timeStamp = sdf.format(new Date());
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("refId", "");
                JSONObject jsonObject = jsonParser.makeHttpRequest("https://pay4sms.in/sendsms/?token=a77e8bff54b875afc733b50566bfde4c&credit=2&sender=DAYATM&message=Dear Customer, Thanks for using DAYATM. Your transaction of Rs. " + amount + " is done at " + loc + " on " + timeStamp + " and your Transaction ID is " + txnId + "&number="+phone+"&templateid=1707162477970070852", "GET", params);

                //Toast.makeText(SuccessActivity.this, "SMS sent successfully! Thank you!", Toast.LENGTH_SHORT).show();
                Intent actHome=new Intent(SuccessActivity.this,MainActivity.class);
                startActivity(actHome);
            }
        });


    }

    private void dispense(int amount) {



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




    private void Print_BMP(){

        byte[] buffer = PrinterCommand.POS_Set_PrtInit();
        Drawable drawable = getResources().getDrawable(R.drawable.logo1);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        Bitmap mBitmap = bitmap;
        int nMode = 0;
        int nPaperWidth = 576;
        //if(width_58mm.isChecked())
        //	nPaperWidth = 384;
        //else if (width_80.isChecked())
        //	nPaperWidth = 576;
        if(mBitmap != null)
        {
            try {
                byte[] data = PrintPicture.POS_PrintBMP(mBitmap, nPaperWidth, nMode);
                usbCtrl.sendByte(buffer, dev);
                usbCtrl.sendByte(data, dev);
                usbCtrl.sendByte(new byte[]{0x1b, 0x4a, 0x30, 0x1d, 0x56, 0x42, 0x01, 0x0a, 0x1b, 0x40}, dev);
            }
            catch (Exception ex)
            {
                Toast.makeText(this, "Printer not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SimpleDateFormat") private void Print_Ex() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String date = str + "\n\n\n\n\n\n";
        try {
            byte[] qrcode = PrinterCommand.getBarCommand("Zijiang Electronic Thermal Receipt Printer!", 0, 3, 6);//
            Command.ESC_Align[2] = 0x01;
            SendDataByte(Command.ESC_Align);
//            SendDataByte(qrcode);

//            Drawable drawable = getResources().getDrawable(R.drawable.logo);
//            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//
//            byte[] logobyte=getBytesFromBitmap(bitmap);
//
//            usbCtrl.sendByte(logobyte,dev);

            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;
            SendDataByte(Command.GS_ExclamationMark);
//            SendDataByte("\nDAY ATM\n".getBytes("GBK"));
            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            SendDataByte(Command.GS_ExclamationMark);
            if(mode.equals("QR_CODE")) {
                double srvc=0,gst=0,total=0;
                if(amount<=1900)
                {
                    srvc=15;
                    gst=2.70;
                    total=gst+srvc+amount;
                }
                else if(amount>1901)
                {
                    srvc=15;
                    gst=2.70;
                    total=gst+srvc+amount;
                }
                String p1 = "TXN ID: " + txnId +"\nTransaction Mode: QR Code Withdrawal \n \nAmount: " + amount +" Rs\nService Charges: "+srvc+" Rs\nGST:             "+gst+" Rs\nTotal Amount:    "+total+" Rs\n \n \n \n";
                SendDataByte(p1.getBytes("GBK"));
//            SendDataByte("Name    Quantity    price  Money\nShoes   10.00       899     8990\nBall    10.00       1599    15990\n".getBytes("GBK"));
//            SendDataByte("Quantity：             20.00\ntotal：                16889.00\npayment：              17000.00\nKeep the change：      111.00\n".getBytes("GBK"));
//            SendDataByte("company name：NIKE\nSite：www.xxx.xxx\naddress：ShenzhenxxAreaxxnumber\nphone number：0755-11111111\nHelpline：400-xxx-xxxx\n================================\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                String p2="------------------------------\n"+loc+"\n";
                SendDataByte(p2.getBytes("GBK"));

                Command.GS_ExclamationMark[2] = 0x11;
                SendDataByte(Command.GS_ExclamationMark);
            }
            else if(mode.equals("ATM")) {

                String p1 = "TXN ID: " + txnId + "\nTransaction Mode: ATM Withdrawal  \n \nAmount: " + amount+" Rs\n \n \n \n \n";
                SendDataByte(p1.getBytes("GBK"));
//            SendDataByte("Name    Quantity    price  Money\nShoes   10.00       899     8990\nBall    10.00       1599    15990\n".getBytes("GBK"));
//            SendDataByte("Quantity：             20.00\ntotal：                16889.00\npayment：              17000.00\nKeep the change：      111.00\n".getBytes("GBK"));
//            SendDataByte("company name：NIKE\nSite：www.xxx.xxx\naddress：ShenzhenxxAreaxxnumber\nphone number：0755-11111111\nHelpline：400-xxx-xxxx\n================================\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                String p2="------------------------------\n"+loc+"\n";
                SendDataByte(p2.getBytes("GBK"));

                Command.GS_ExclamationMark[2] = 0x11;
                SendDataByte(Command.GS_ExclamationMark);
            }
            else if(mode.equals("WALLET")) {

                String p1 = "TXN ID: " + txnId + "\nTransaction Mode: Wallet Withdrawal  \n \nAmount: " + amount+" Rs\n \n \n \n \n";
                SendDataByte(p1.getBytes("GBK"));
//            SendDataByte("Name    Quantity    price  Money\nShoes   10.00       899     8990\nBall    10.00       1599    15990\n".getBytes("GBK"));
//            SendDataByte("Quantity：             20.00\ntotal：                16889.00\npayment：              17000.00\nKeep the change：      111.00\n".getBytes("GBK"));
//            SendDataByte("company name：NIKE\nSite：www.xxx.xxx\naddress：ShenzhenxxAreaxxnumber\nphone number：0755-11111111\nHelpline：400-xxx-xxxx\n================================\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                String p2="------------------------------\n"+loc+"\n";
                SendDataByte(p2.getBytes("GBK"));

                Command.GS_ExclamationMark[2] = 0x11;
                SendDataByte(Command.GS_ExclamationMark);
            }
            else if(mode.equals("AEPS"))
            {
                String p1 = "TXN ID: " + txnId + "\nTransaction Mode: AEPS Withdrawal  \n \nAmount: " + amount+" Rs\n \n \n \n \n ";
                SendDataByte(p1.getBytes("GBK"));
//            SendDataByte("Name    Quantity    price  Money\nShoes   10.00       899     8990\nBall    10.00       1599    15990\n".getBytes("GBK"));
//            SendDataByte("Quantity：             20.00\ntotal：                16889.00\npayment：              17000.00\nKeep the change：      111.00\n".getBytes("GBK"));
//            SendDataByte("company name：NIKE\nSite：www.xxx.xxx\naddress：ShenzhenxxAreaxxnumber\nphone number：0755-11111111\nHelpline：400-xxx-xxxx\n================================\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                String p2="------------------------------\n"+loc+"\n";
                SendDataByte(p2.getBytes("GBK"));

                Command.GS_ExclamationMark[2] = 0x11;
                SendDataByte(Command.GS_ExclamationMark);
            }
            else if(mode.equals("DMT"))
            {
                double srvc=0,gst=0,total=0;
                if(amount<2000)
                {
                    srvc=10;
                }
                else if(amount>=2000)
                {
                    double commission=(double)(amount/100)*1;
                    srvc=commission;
                }
                total=amount-srvc;
                String p1 = "TXN ID: " + txnId + "\nTransaction Mode: Direct Money Transfer  \n \n  \n  \nAmount: " + amount+" Rs\nService Charges: -"+srvc+" Rs\nTotal Amount:    "+total+" Rs\n \n \n \n \n \n \n \n \n ";
                SendDataByte(p1.getBytes("GBK"));
//            SendDataByte("Name    Quantity    price  Money\nShoes   10.00       899     8990\nBall    10.00       1599    15990\n".getBytes("GBK"));
//            SendDataByte("Quantity：             20.00\ntotal：                16889.00\npayment：              17000.00\nKeep the change：      111.00\n".getBytes("GBK"));
//            SendDataByte("company name：NIKE\nSite：www.xxx.xxx\naddress：ShenzhenxxAreaxxnumber\nphone number：0755-11111111\nHelpline：400-xxx-xxxx\n================================\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                String p2="------------------------------\n"+loc+"\n";
                SendDataByte(p2.getBytes("GBK"));

                Command.GS_ExclamationMark[2] = 0x11;
                SendDataByte(Command.GS_ExclamationMark);
            }

            SendDataByte("Welcome again!\n".getBytes("GBK"));
            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            SendDataByte(Command.GS_ExclamationMark);

            SendDataByte("(This invoice is from Day ATM.)\n".getBytes("GBK"));
            Command.ESC_Align[2] = 0x02;
            SendDataByte(Command.ESC_Align);
            SendDataString(date);
            SendDataByte(Command.GS_i);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void SendDataByte(byte[] data){
        if(data.length>0)
            try {
                usbCtrl.sendByte(data, dev);
            }
            catch (Exception ex)
            {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
    }
    private void SendDataString(String data){
        if(data.length()>0)
            usbCtrl.sendMsg(data, "GBK", dev);
    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    class DispenseSingle extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int _amt = Integer.parseInt(damt);

            String amt = String.valueOf(_amt / 100);
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

//            loc="Day ATM Head Office";
//            sendSMS(amount,txnId,loc);

//            Intent serviceIntent = new Intent(getApplicationContext(),PrintService.class);
//            serviceIntent.putExtra("amount", amount);
//            serviceIntent.putExtra("refId",txnId);
//            startService(serviceIntent);
        }
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
    private MyHandler mHandler1;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler1);
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
    class DispenseDual extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.e("Dispensing",mode);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int amt1=Integer.parseInt(damt);

            int upp_denomination = 500;
            int low_denomination = 100;
            int num2 = amt1 / upp_denomination;
            int upcount1 = num2 / 10;
            int upcount2 = num2 % 10;
            int num3 = amt1 % upp_denomination / low_denomination;
//                    Log.e("NUM1",""+num1);
            Log.e("NUM2",""+num2);
            Log.e("NUM3",""+num3);

            int lowcount1 = num3 / 10;
            int lowcount2 = num3 % 10;
            int checksum = 48 + upcount1 ^ 48 + upcount2 ^ 48 + lowcount1 ^ 48 + lowcount2 ^ 3;
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: this.inp = new byte[10] { (byte) 4, (byte) 80, (byte) 2, (byte) 86, Convert.ToByte(48 + this.upcount1), Convert.ToByte(48 + this.upcount2), Convert.ToByte(48 + this.lowcount1), Convert.ToByte(48 + this.lowcount2), (byte) 3, Convert.ToByte(this.checksum) };

            Log.e("UC1",""+upcount1);
            Log.e("UC2",""+upcount2);
            Log.e("LC1",""+lowcount1);
            Log.e("LC2",""+lowcount2);

            byte[] inp = new byte[]{(byte) 4, (byte) 80, (byte) 2, (byte) 86, (byte) (48 + upcount1), (byte) (48 + upcount2), (byte) (48 + lowcount1), (byte) (48 + lowcount2), (byte) 3, (byte) checksum};


            if (usbService != null) {
                // if UsbService was correctly binded, Send data
                //Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_SHORT).show();
                Log.e("Status","Sending Data...");
                usbService.write(inp);
            }
            else
            {
                //makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                Log.e("Status","Sorry...");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

//            loc="Day ATM Head Office";
//            sendSMS(amount,txnId,loc);

//            Intent serviceIntent = new Intent(getApplicationContext(),PrintService.class);
//            serviceIntent.putExtra("amount", amount);
//            serviceIntent.putExtra("refId",txnId);
//            startService(serviceIntent);
        }
    }

    public class Disp extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int _amt = Integer.parseInt(damt);



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


//            Intent serviceIntent = new Intent(getApplicationContext(),PrintService.class);
//            serviceIntent.putExtra("amount", amount);
//            serviceIntent.putExtra("refId",txnId);
//            startService(serviceIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent acthome=new Intent(SuccessActivity.this,MainActivity.class);
        startActivity(acthome);
        finish();
    }
}