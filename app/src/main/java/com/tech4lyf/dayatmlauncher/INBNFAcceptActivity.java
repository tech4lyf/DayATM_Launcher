package com.tech4lyf.dayatmlauncher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import device.itl.sspcoms.BarCodeReader;
import device.itl.sspcoms.DeviceEvent;
import device.itl.sspcoms.ItlCurrency;
import device.itl.sspcoms.SSPDevice;
import device.itl.sspcoms.SSPDeviceType;

public class INBNFAcceptActivity extends AppCompatActivity {

    static INBNFAcceptActivity mainActivity;
    private static INBNFAcceptActivity instance = null;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;
    private static D2xxManager ftD2xx = null;
    private static FT_Device ftDev = null;
    private static SSPDevice sspDevice = null;
    private static ITLDeviceCom deviceCom;
    static TextView tvInsAmount;
    static int amtins=0;
    static Button btnDep;
    TextView tvDepAmount;
    static TextView tvHundred,tvTwoHundred,tvFiveHundred,tvTwoThousand;

    String amt,benId,custId;
    static double dec;
    ProgressDialog progressDialog;
    JSONObject jsonObject;
    JSONParser jsonParser;
    String userid="",phone="",ip="",pin="",loc="";
    static double txnAmt=0;
    static int printAmt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbnfaccept);


        progressDialog=new ProgressDialog(this);
        jsonParser=new JSONParser(this);
        amt="";
        amtins=0;

        amt=getIntent().getStringExtra("amt");
        benId=getIntent().getStringExtra("benId");
        custId=getIntent().getStringExtra("custId");


        tvHundred=(TextView)findViewById(R.id.tvHundredNotes);
        tvTwoHundred=(TextView)findViewById(R.id.tvTwoHundredNotes);
        tvFiveHundred=(TextView)findViewById(R.id.tvFiveHundredNotes);
        tvTwoThousand=(TextView)findViewById(R.id.tvTwoThousandNotes);

        tvDepAmount=(TextView)findViewById(R.id.tvAmounttoDep);
        tvInsAmount=(TextView)findViewById(R.id.tvAmountInserted);
        progressDialog=new ProgressDialog(this);

        mainActivity = this;
        this.instance = this;
        tvDepAmount.setText(amt);
        btnDep=(Button) findViewById(R.id.btnDepositSend);
//        tv1=(TextView) findViewById(R.id.tv1);
        
        btnDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Transfer().execute();
            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(getApplicationContext(), "This app requires access to the downloads directory in order to load download files.", Toast.LENGTH_SHORT).show();


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }

        try {
            ftD2xx = D2xxManager.getInstance(this);
            Log.e("Dev", "Device Found");
        } catch (D2xxManager.D2xxException ex) {
            Log.e("SSP FTmanager", ex.toString());
        }

        openDevice();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        Log.e("hello", "hi");
        this.registerReceiver(mUsbReceiver, filter);
        Log.e("hello", "hi");
        deviceCom = new ITLDeviceCom();


        if (ftDev != null) {
//                    prgConnect.setVisibility(View.VISIBLE);
//                    txtConnect.setVisibility(View.VISIBLE);
//                    fab.setEnabled(false);

            deviceCom.setup(ftDev, 0, false, false, 0);
            deviceCom.start();
        } else {
            Toast.makeText(INBNFAcceptActivity.this, "No USB connection detected!", Toast.LENGTH_SHORT).show();
        }

//        deviceCom.setup(ftDev, 0, false, false, 0);
//        deviceCom.start();


    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // never come here(when attached, go to onNewIntent)
                Log.e("Dev","Opening...");
                openDevice();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                closeDevice();
//                bvDisplay.setVisibility(View.INVISIBLE);
//                fab.setVisibility(View.VISIBLE);
//                fab.setEnabled(true);
            }
        }
    };

    private void openDevice() {

        Log.e("Dev","Opening Device");


        if (ftDev != null) {
            if (ftDev.isOpen()) {
                // if open and run thread is stopped, start thread
                SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
                ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                ftDev.restartInTask();
                return;
            }
        }

        int devCount = 0;

        if (ftD2xx != null) {
            // Get the connected USB FTDI devoces
            devCount = ftD2xx.createDeviceInfoList(this);
        } else {
            return;
        }

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);
        // none connected
        if (devCount <= 0) {
            return;
        }
        if (ftDev == null) {
            ftDev = ftD2xx.openByIndex(this, 0);
        } else {
            synchronized (ftDev) {
                ftDev = ftD2xx.openByIndex(this, 0);
            }
        }
        // run thread
        if (ftDev.isOpen()) {
            SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
            ftDev.restartInTask();
        }
    }


    private static void closeDevice() {

        if (ftDev != null) {
            deviceCom.Stop();
            ftDev.close();
        }
    }

    public static void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (!ftDev.isOpen()) {
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }

    public static void DeviceDisconnected(SSPDevice dev) {

//        eventValues[0] = "DISCONNECTED!!!";
//        eventValues[1] = "";
//        adapterEvents.notifyDataSetChanged();

        Toast.makeText(mainActivity.getApplicationContext(), "Device Disconnected!", Toast.LENGTH_SHORT).show();

    }

    public static void DisplaySetUp(SSPDevice dev)
    {

        sspDevice = dev;



//        fab.setVisibility(View.INVISIBLE);
//        fab.setVisibility(View.INVISIBLE);
//        prgConnect.setVisibility(View.INVISIBLE);
//        txtConnect.setVisibility(View.INVISIBLE);
//        bvDisplay.setVisibility(View.VISIBLE);

        // check for type comapable
        if(dev.type != SSPDeviceType.BillValidator){
            AlertDialog.Builder builder = new AlertDialog.Builder(INBNFAcceptActivity.getInstance());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Connected device is not BNV (" + dev.type.toString() + ")")
                    .setTitle("BNV");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getInstance().finish();
                }
            });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            // 4. Show the dialog
            dialog.show();// show error
            return;

        }

        //downloadFileSelect.setEnabled(true);

        /* device details  */
//        txtFirmware.append(" " + dev.firmwareVersion);
//        txtDevice.append(" " + dev.headerType.toString());
//        txtSerial.append(" " + String.valueOf(dev.serialNumber));
//        txtDataset.append(dev.datasetVersion);

        Log.e("Dev",dev.firmwareVersion+"\n"+dev.headerType.toString()+"\n"+String.valueOf(dev.serialNumber)+"\n"+dev.datasetVersion);

        /* display the channel info */
        //channelValues.clear();
        for (ItlCurrency itlCurrency : dev.currency) {
            String v = itlCurrency.country + " " + String.format("%.2f", itlCurrency.realvalue);
            Log.e("Channel Values",v+";");
            //channelValues.add(v);
        }

        //adapterChannels.notifyDataSetChanged();


        // if device has barcode hardware
        if (dev.barCodeReader.hardWareConfig != SSPDevice.BarCodeStatus.None) {
            // send new configuration
            BarCodeReader cfg = new BarCodeReader();
            cfg.barcodeReadEnabled = true;
            cfg.billReadEnabled = true;
            cfg.numberOfCharacters = 18;
            cfg.format = SSPDevice.BarCodeFormat.Interleaved2of5;
            cfg.enabledConfig = SSPDevice.BarCodeStatus.Both;
            deviceCom.SetBarcocdeConfig(cfg);
        }
    }

    public static INBNFAcceptActivity getInstance(){

        return instance;
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    public static void DisplayEvents(DeviceEvent ev) {

        switch (ev.event) {
            case CommunicationsFailure:

                break;
            case Ready:
                Log.e("Status","Ready");
                break;
            case BillRead:
                Log.e("Status","Reading");
                break;
            case BillEscrow:
                Log.e("Status","Bill Escrow");
                Log.e("Status",ev.currency + " " +String.format("%.2f", ev.value));
                break;
            case BillStacked:

                break;
            case BillReject:
                Log.e("Status","Bill Reject");
                break;
            case BillJammed:
                Log.e("Status","Bill Jammed");
                break;
            case BillFraud:
                Log.e("Status","Bill Fraus");
                Log.e("Status",ev.currency + " " +
                        String.format("%.2f", ev.value));
                break;
            case BillCredit:
                Log.e("Status","Bill Credit");
                Log.e("Status",ev.currency + " " +
                        String.format("%.2f", ev.value));
                checkAmt(ev.value);
                break;
            case Full:
                Log.e("Status","Cash Box full");
                break;
            case Initialising:

                break;
            case Disabled:
                Log.e("Status","Disabled");
                break;
            case SoftwareError:
                Log.e("Status","Software Error");
                break;
            case AllDisabled:
                Log.e("Status","All Channels Disabled");
                break;
            case CashboxRemoved:
                Log.e("Status","Cashbox Removed");
                break;
            case CashboxReplaced:
                Log.e("Status","Cashbox Replaced");
                break;
            case NotePathOpen:
                Log.e("Status","Note path Open");
                break;
            case BarCodeTicketEscrow:
                Log.e("Status","Barcode Ticket Escrow");
                break;
            case BarCodeTicketStacked:
                Log.e("Status","bar ticket Stacked");
                break;
        }
//        adapterEvents.notifyDataSetChanged();


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
            params.put("userid",userid);
            params.put("ipaddress", ip);
            params.put("amount", String.valueOf((int)txnAmt));
            params.put("remark","Amount Added");


            try {
                jsonObject = jsonParser.makeHttpRequest("https://dayatms.com/webapi/ictuser/amountdeposit", "POST", params);

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
            deviceCom.Stop();
            closeDevice();

            String status="";
            String txnStatus="";
            String txnId="";
            try {
                txnStatus=jsonObject.getString("status");
//                txnStatus=jsonObject.getString("trans_status");
                txnId=jsonObject.getString("transid");
                Log.e("WalStatus",txnStatus);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(txnStatus.equals("true"))
            {
                if(txnStatus.equals("true")) {
                    Toast.makeText(INBNFAcceptActivity.this, "Your money added to the wallet successfully!", Toast.LENGTH_SHORT).show();
                    Intent actHome = new Intent(INBNFAcceptActivity.this, SuccessActivity.class);
                    actHome.putExtra("txnId",txnId);
                    Log.e("Amount to Print",""+txnAmt);
                    actHome.putExtra("amt",(int)txnAmt);
                    actHome.putExtra("loc",loc);
                    actHome.putExtra("mode","DMT");

                    startActivity(actHome);
                }
//                else if(txnStatus.equals("")) {
//                    Toast.makeText(INBNFAcceptActivity.this, "Transaction Successful!", Toast.LENGTH_SHORT).show();
//                    Intent actHome = new Intent(INBNFAcceptActivity.this, SuccessActivity.class);
//
//                    startActivity(actHome);
//                }
                else if(txnStatus.equals("false")) {
                    Toast.makeText(INBNFAcceptActivity.this, "Transaction Failed!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(INBNFAcceptActivity.this, "Please contact customer care", Toast.LENGTH_LONG).show();
                    Intent actHome = new Intent(INBNFAcceptActivity.this, HomeActivity.class);
                    startActivity(actHome);
                }
                else{
                    Toast.makeText(INBNFAcceptActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(INBNFAcceptActivity.this, "Please contact customer care", Toast.LENGTH_LONG).show();
                    Intent actHome = new Intent(INBNFAcceptActivity.this, HomeActivity.class);
                    startActivity(actHome);
                }
            }
            else if(txnStatus.equals("false"))
            {
                Toast.makeText(INBNFAcceptActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                Toast.makeText(INBNFAcceptActivity.this, "Please contact customer care", Toast.LENGTH_LONG).show();
                Intent actHome=new Intent(INBNFAcceptActivity.this,HomeActivity.class);
                startActivity(actHome);
            }
            else
            {
                Toast.makeText(INBNFAcceptActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                Intent actHome=new Intent(INBNFAcceptActivity.this,HomeActivity.class);
                startActivity(actHome);
            }


        }
    }

    static void checkAmt(double amt)
    {
        dec=amt;
        Log.e("Amount came",""+amt);
        if(dec==100.00)
        {
            Log.e("Note Detected","Hundred");
            new PutCashActivity.Accept().execute();
            amtins=amtins+100;
            printAmt=printAmt+100;
            int tmp=0;
            int h=Integer.parseInt(tvHundred.getText().toString());
            tmp=h+1;
            tvHundred.setText(""+tmp);
            updateAmt(amtins);
        }
        else if(dec==200.00)
        {
            Log.e("Note Detected","Two Hundred");
            new PutCashActivity.Accept().execute();
            amtins=amtins+200;

            printAmt=printAmt+200;
            int tmp=0;
            int h=Integer.parseInt(tvTwoHundred.getText().toString());
            tmp=h+1;
            tvTwoHundred.setText(""+tmp);

            updateAmt(amtins);
        }
        else if(dec==500.00)
        {
            Log.e("Note Detected","Five Hundred");
            new PutCashActivity.Accept().execute();
            amtins=amtins+500;

            printAmt=printAmt+500;
            int tmp=0;
            int h=Integer.parseInt(tvFiveHundred.getText().toString());
            tmp=h+1;
            tvFiveHundred.setText(""+tmp);
            updateAmt(amtins);
        }
        else if(dec==2000.00)
        {
            Log.e("Note Detected","Two Thousand");
            new PutCashActivity.Accept().execute();
            amtins=amtins+2000;

            printAmt=printAmt+2000;
            int tmp=0;
            int h=Integer.parseInt(tvTwoThousand.getText().toString());
            tmp=h+1;
            tvTwoThousand.setText(""+tmp);
            updateAmt(amtins);
        }
        else if(dec==10.00 || dec==20.00 || dec==50.00)
        {

        }
    }

    static void updateAmt(double amt)
    {

        if(amt<100)
        {

            //amt=amt-10;
            txnAmt=amt;
            tvInsAmount.setText(""+amt);
            btnDep.setText("Deposit ("+amt+")");
        }

        else if(amt<2000 && amt>=100)
        {

            //amt=amt-10;
            txnAmt=amt;
            tvInsAmount.setText(""+amt);
            btnDep.setText("Deposit ("+amt+")");
        }
        else if(amt<=200000 && amt>=2000)
        {
            double commission=(double)(amt/100)*1;
            //amt=amt-commission;
            txnAmt=amt;
            tvInsAmount.setText(""+amt);
            btnDep.setText("Deposit ("+amt+")");

        }



    }

}