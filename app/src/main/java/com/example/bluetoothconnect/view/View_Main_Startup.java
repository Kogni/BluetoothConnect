package com.example.bluetoothconnect.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bluetoothconnect.R;
import com.example.bluetoothconnect.control.Control_Main;
import com.google.firebase.firestore.util.Util;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class View_Main_Startup extends AppCompatActivity  implements BeaconConsumer {

    private static final String logtag = "View_Main_Startup";
    Control_Main class_Control_Main;

    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayAdapter<String> mArrayAdapter;

    private BeaconManager beaconManager;
    TextView rangeElement;
    org.altbeacon.beacon.Region region;

    //--permissions & requests
    //request codes
    private static final int RC_ENABLE_BT = 0;
    private static final int RC_DISCOVERABLE_BT = 0;
    private static final int RC_EXTERNAL_STORAGE = 1;
    public static final int RC_BLUETOOTH = 1;
    //permissions to request
    private static final String[] PERMISSIONS_bluetooth = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.BLUETOOTH_SCAN,
    };
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String[] PERMISSIONS_GPS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private TextView textview_log;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        class_Control_Main = new Control_Main(this);

        verifyPermissions(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Date currentTime = Calendar.getInstance().getTime();
                String tidspunkt = (currentTime.getYear()+1900)+ "." + (currentTime.getMonth()+1)+"."+currentTime.getDay()+" "+currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds();
                String event = tidspunkt+": "+device.getName()+" ("+device.getClass().getSimpleName()+", "+device.getType()+", "+device.getBluetoothClass()+", "+device.getBluetoothClass().getDeviceClass()+", "+device.getBluetoothClass().getMajorDeviceClass()+", "+device+"), "+device.getBondState()+", "+action;
                textview_log.setText(event);
                class_Control_Main.writeToSDFile(event);
                Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. event som loggføres: "+event);

                //Finding devices
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. BluetoothDevice.ACTION_FOUND");
                } else {
                    Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. --->"+action);
                    if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                        Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. ACTION_ACL_CONNECTED. Device="+device+", "+device.getType()+", "+device.getName());
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. ACTION_DISCOVERY_FINISHED");
                    } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                        Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. ACTION_ACL_DISCONNECT_REQUESTED");
                    } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                        Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. ACTION_ACL_DISCONNECTED. Device="+device+", "+device.getType()+", "+device.getName());
                    } else {
                        Log.i(logtag, "onCreate.BroadcastReceiver mReceiver-b.onReceive. else");
                    }
                }

            }
        };
        registerReceiver(mReceiver, filter); //gjør at events som matcher filtere blir mottatt av receiver

        final TextView out=(TextView)findViewById(R.id.out);
        final Button button_turnon = (Button) findViewById(R.id.button_turnon);
        final Button button_discover = (Button) findViewById(R.id.button_discover);

        if (mBluetoothAdapter == null) {
            out.append("device not supported");
            Log.i(logtag, "onCreate, device not supported");
        } else {
            Log.i(logtag, "onCreate, Egen device' navn: "+mBluetoothAdapter.getName());
        }

        button_discover.setOnClickListener(arg0 -> {
            assert mBluetoothAdapter != null;
            if (!mBluetoothAdapter.isDiscovering()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(enableBtIntent, RC_DISCOVERABLE_BT);
                mBluetoothAdapter.startDiscovery();
            }
        });

        assert mBluetoothAdapter != null;
        mBluetoothAdapter.startDiscovery();
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);


        //beacon stuff
        beaconManager = BeaconManager.getInstanceForApplication(this);

        region = new org.altbeacon.beacon.Region("myBeacons", Identifier.parse("73676723-7400-0000-ffff-0000ffff0005"), null, null);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        textview_log = (TextView) findViewById(R.id.log);
        textview_log.setText("onCreate");
    }



    public static void verifyPermissions(Activity activity) {
       int gotAllPermissions = Math.max(
                Math.max(
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
                ),
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        );
        if (gotAllPermissions != PackageManager.PERMISSION_GRANTED) {
            Log.i(logtag, "verifyPermissions 2a mangler permissions");
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_bluetooth,
                    RC_BLUETOOTH
            );
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    RC_EXTERNAL_STORAGE
            );
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_GPS,
                    0
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //trigges ved aktivisering av discovery
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(logtag, "onActivityResult requestCode="+requestCode+" resultCode="+resultCode+", isdiscovering="+mBluetoothAdapter.isDiscovering());//logges ikke
        if (requestCode == RC_ENABLE_BT) {
            if ((resultCode == RESULT_OK) || (resultCode == 120)) {
                progressToNextCheck();

            }
        }
    }

    private void progressToNextCheck(){ //trigges ved aktivisering av discovery
        Log.i(logtag, "progressToNextCheck, scan mode="+mBluetoothAdapter.getScanMode()); //logges ikke
        Log.i(logtag, "progressToNextCheck. Is discovering: "+mBluetoothAdapter.isDiscovering());
        if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.i(logtag, "progressToNextCheck scan mode is SCAN_MODE_CONNECTABLE_DISCOVERABLE, what to do next?");
        } else {
            Log.i(logtag, "progressToNextCheck, scan not SCAN_MODE_CONNECTABLE_DISCOVERABLE. Asking for scan mode");
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivityForResult(discoverableIntent, RC_DISCOVERABLE_BT);
        }
    }

    private void connectToMAC(String address){
        //Log.i(logtag, "connectToMAC address="+address);
        try {
            //Log.i(logtag, "connectToMAC try 2");
            BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothManager.getAdapter().getRemoteDevice(address);
            //Log.i(logtag, "connectToMAC try 2 completed. Device="+mBluetoothDevice+", "+mBluetoothDevice.getType()+", "+mBluetoothDevice.getName());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBeaconServiceConnect() { //trigges
        Log.i(logtag, "onBeaconServiceConnect 1 start");//blir logget
        beaconManager.removeAllRangeNotifiers();
        Log.i(logtag, "onBeaconServiceConnect 2, før addRangeNotifier");//blir logget

        beaconManager.addRangeNotifier((beacons, region) -> { //ser ikke ut til å kjøre
            Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion"); //logges ikke
            for (Beacon b : beacons) {
                //System.out.println(String.format("%s: %f: %d", b.getBluetoothName(), b.getDistance(), b.getRssi()));
                Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion "+String.format("%s: %f: %d", b.getBluetoothName(), b.getDistance(), b.getRssi()));
            }
            if (beacons.size() > 0) {
                Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion. The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
            }
            try {
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });


        Log.i(logtag, "onBeaconServiceConnect 3, etter addRangeNotifier, før setMonitorNotifier");//blir logget
        beaconManager.setMonitorNotifier(new MonitorNotifier() { //ser ikke ut til å kjøre
            @Override
            public void didEnterRegion(org.altbeacon.beacon.Region region) {
                Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didEnterRegion"); //logges ikke
                try {
                    Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didEnterRegion. Did Enter Region");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
               }
            }

            @Override
            public void didExitRegion(org.altbeacon.beacon.Region region) {
                Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didExitRegion"); //logges ikke
                try {
                    Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didExitRegion. Did Exit Region");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
              }
            }

            @Override
            public void didDetermineStateForRegion(int state, org.altbeacon.beacon.Region region) {
                Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didDetermineStateForRegion"); //logges ikke
            }

        });

        Log.i(logtag, "onBeaconServiceConnect 6, etter setRangeNotifier");//blir logget

        try {
            Log.d(logtag, "onBeaconServiceConnect 7a try start"); //logges IKKE
            beaconManager.startMonitoringBeaconsInRegion(region);
            Log.d(logtag, "onBeaconServiceConnect 7a2 try successful");
            //Log.i(logtag, "onBeaconServiceConnect 6, etter startMonitoringBeaconsInRegion");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(logtag, "onBeaconServiceConnect 8, før getBeaconParsers");//blir logget
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.i(logtag, "onBeaconServiceConnect 9, end");//blir logget
    }


}