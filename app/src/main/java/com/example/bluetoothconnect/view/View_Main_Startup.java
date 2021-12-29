package com.example.bluetoothconnect.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Region;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetoothconnect.R;
import com.example.bluetoothconnect.control.Control_Main;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;

import java.util.Collection;

public class View_Main_Startup extends AppCompatActivity  implements BeaconConsumer {

    private static final String logtag = "View_Main_Startup";
    Control_Main class_Control_Main;

    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayAdapter<String> mArrayAdapter;

    private BeaconManager beaconManager;
    TextView rangeElement;
    org.altbeacon.beacon.Region region;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVERABLE_BT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(logtag, "onCreate, start");

        setContentView(R.layout.activity_main);

        class_Control_Main = new Control_Main(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        final TextView out=(TextView)findViewById(R.id.out);
        final Button button_turnon = (Button) findViewById(R.id.button_turnon);
        final Button button_discover = (Button) findViewById(R.id.button_discover);
        final Button button_turnoff = (Button) findViewById(R.id.button_turnoff);
        if (mBluetoothAdapter == null) {
            out.append("device not supported");
            Log.i(logtag, "onCreate, device not supported");
        } else {
            Log.i(logtag, "onCreate, Egen device' navn: "+mBluetoothAdapter.getName());
            Log.i(logtag, "onCreate, mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET)="+mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET));
            Log.i(logtag, "onCreate, BluetoothHeadset.STATE_CONNECTED="+BluetoothHeadset.STATE_CONNECTED);
        }
        button_turnon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(logtag, "onCreate button_turnon.onClick");
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });
        button_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i(logtag, "onCreate button_discover.onClick");
                if (!mBluetoothAdapter.isDiscovering()) {
                    //out.append("MAKING YOUR DEVICE DISCOVERABLE");
                    Toast.makeText(getApplicationContext(), "MAKING YOUR DEVICE DISCOVERABLE",
                            Toast.LENGTH_LONG);

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);

                }
            }
        });
        button_turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i(logtag, "onCreate button_turnoff.onClick");
                mBluetoothAdapter.disable();
                //out.append("TURN_OFF BLUETOOTH");
                Toast.makeText(getApplicationContext(), "TURNING_OFF BLUETOOTH", Toast.LENGTH_LONG);

            }
        });

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
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.i(logtag, "onCreate BroadcastReceiver.onReceive");
            }
        };

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        //beacon stuff
        beaconManager = BeaconManager.getInstanceForApplication(this);
        region = new org.altbeacon.beacon.Region("myBeacons", Identifier.parse("73676723-7400-0000-ffff-0000ffff0005"), null, null);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //onScanStart(R.layout.activity_main);
        //rangeElement = (TextView) findViewById(R.id.range);
        beaconManager.bind(this);
        try {
            Log.i(logtag, "onCreate, beaconManager.stopMonitoringBeaconsInRegion try 1");
            beaconManager.stopMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.i(logtag, "onCreate beaconManager.stopMonitoringBeaconsInRegion failed "+e.getCause());
            Log.i(logtag, "onCreate beaconManager.stopMonitoringBeaconsInRegion failed "+e.toString());
            Log.i(logtag, "onCreate beaconManager.stopMonitoringBeaconsInRegion failed "+e.getStackTrace());
        }
        try {
            Log.i(logtag, "onCreate, beaconManager.stopMonitoringBeaconsInRegion try 2");
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.i(logtag, "onCreate beaconManager.startMonitoringBeaconsInRegion failed "+e.getCause());
            Log.i(logtag, "onCreate beaconManager.startMonitoringBeaconsInRegion failed "+e.toString());
            Log.i(logtag, "onCreate beaconManager.startMonitoringBeaconsInRegion failed "+e.getStackTrace());
        }

        Log.i(logtag, "onCreate, ferdig med oppsett");
        //connectToMAC ("A4:6C:F1:06:30:27");
        connectToMAC ("C3:AB:45:C9:D8:54"); //fitbit

        Log.i(logtag, "onCreate, end");
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(logtag, "BroadcastReceiver.onReceive");
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i(logtag, "BroadcastReceiver.onReceive. Device="+device+", "+device.getType()+", "+device.getName());

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i(logtag, "BroadcastReceiver.onReceive ACTION_FOUND");
                // Get the BluetoothDevice object from the Intent
                // Add the name and address to an array adapter to show in a ListView

                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.i(logtag, "BroadcastReceiver.onReceive adding "+device.getName() + "\n" + device.getAddress());
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.i(logtag, "BroadcastReceiver.onReceive ACTION_ACL_CONNECTED. Device="+device+", "+device.getType()+", "+device.getName());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(logtag, "BroadcastReceiver.onReceive ACTION_DISCOVERY_FINISHED");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Log.i(logtag, "BroadcastReceiver.onReceive ACTION_ACL_DISCONNECT_REQUESTED");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.i(logtag, "BroadcastReceiver.onReceive ACTION_ACL_DISCONNECTED. Device="+device+", "+device.getType()+", "+device.getName());
            } else {
                Log.i(logtag, "BroadcastReceiver.onReceive else");
            }

            //Finding devices
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i(logtag, "BroadcastReceiver.onReceive BluetoothDevice.ACTION_FOUND.equals(action)");
            }
        }
    };

    private void connectToMAC(String address){
        Log.i(logtag, "connectToMAC address="+address);
        try {
            Log.i(logtag, "connectToMAC try 2");
            BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothDevice mBluetoothDevice = bluetoothManager.getAdapter() .getRemoteDevice(address);
            Log.i(logtag, "connectToMAC try 2 completed. Device="+mBluetoothDevice+", "+mBluetoothDevice.getType()+", "+mBluetoothDevice.getName());
        } catch (Exception e){
            Log.i(logtag, "connectToMAC try 2 failed "+e.getCause());
            Log.i(logtag, "connectToMAC try 2 failed "+e.toString());
            Log.i(logtag, "connectToMAC try 2 failed "+e.getStackTrace());
        }
    }

    public void onScanStart(View view) {
        Log.i(logtag, "onScanStart ");
        //stopScanButton.setEnabled(true);
        //scanningButton.setEnabled(false);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(logtag, "onBeaconServiceConnect 1 start");
        beaconManager.removeAllRangeNotifiers();
        Log.i(logtag, "onBeaconServiceConnect 2, før addRangeNotifier");
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {
                Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion");
                for (Beacon b : beacons) {
                    //System.out.println(String.format("%s: %f: %d", b.getBluetoothName(), b.getDistance(), b.getRssi()));
                    Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion "+String.format("%s: %f: %d", b.getBluetoothName(), b.getDistance(), b.getRssi()));
                }
                if (beacons.size() > 0) {
                    Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion. The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
                try {
                    //Region region1 = new Region("abc123", null, null, null);
                    //beaconManager.startRangingBeaconsInRegion(region1);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion failed", Toast.LENGTH_LONG);
                    Log.i(logtag, "onBeaconServiceConnect.didRangeBeaconsInRegion failed "+e.getCause());
                    Log.i(logtag, "onBeaconServiceConnect.didRangeBeaconsInRegion failed "+e.toString());
                    Log.i(logtag, "onBeaconServiceConnect.didRangeBeaconsInRegion failed "+e.getStackTrace());
                }
            }

        });
        Log.i(logtag, "onBeaconServiceConnect 3, etter addRangeNotifier, før setMonitorNotifier");
        beaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(org.altbeacon.beacon.Region region) {
                Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didEnterRegion");
                try {
                    Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didEnterRegion. Did Enter Region");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didEnterRegion failed "+e.getCause());
                    Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didEnterRegion failed "+e.toString());
                    Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didEnterRegion failed "+e.getStackTrace());
                }
            }

            @Override
            public void didExitRegion(org.altbeacon.beacon.Region region) {
                Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didExitRegion");
                try {
                    Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didExitRegion. Did Exit Region");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didExitRegion failed "+e.getCause());
                    Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didExitRegion failed "+e.toString());
                    Log.i(logtag, "onBeaconServiceConnect.setMonitorNotifier.didExitRegion failed "+e.getStackTrace());
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, org.altbeacon.beacon.Region region) {
                Log.d(logtag, "onBeaconServiceConnect.setMonitorNotifier.didDetermineStateForRegion");
            }

        });
        Log.i(logtag, "onBeaconServiceConnect 4, etter setMonitorNotifier, før setRangeNotifier");
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            //Log out welche beacons in der Nähe sind
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {
                Log.d(logtag, "onBeaconServiceConnect.setRangeNotifier.didRangeBeaconsInRegion");
                for(final Beacon oneBeacon : beacons) {
                    Log.d(logtag, "distance: " + oneBeacon.getDistance() + "id: " + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(logtag, "onBeaconServiceConnect.setRangeNotifier.didRangeBeaconsInRegion.run");
                            // Change the text label in the UI
                            rangeElement.setText(String.valueOf(oneBeacon.getDistance()));
                        }
                    });
                }
            }
        });
        Log.i(logtag, "onBeaconServiceConnect 5, etter setRangeNotifier, før startMonitoringBeaconsInRegion");

        try {
            Log.d(logtag, "onBeaconServiceConnect try start");
            beaconManager.startMonitoringBeaconsInRegion(region);
            Log.d(logtag, "onBeaconServiceConnect try successful");
        } catch (RemoteException e) {
            Log.i(logtag, "onBeaconServiceConnect failed "+e.getCause());
            Log.i(logtag, "onBeaconServiceConnect failed "+e.toString());
            Log.i(logtag, "onBeaconServiceConnect failed "+e.getStackTrace());
        }
        Log.i(logtag, "onBeaconServiceConnect 6, etter startMonitoringBeaconsInRegion");
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.i(logtag, "onBeaconServiceConnect 7, end");
    }

}