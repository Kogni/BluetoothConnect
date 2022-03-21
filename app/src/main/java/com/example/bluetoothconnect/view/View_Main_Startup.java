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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.bluetoothconnect.R;
import com.example.bluetoothconnect.control.Control_Main;
import com.example.bluetoothconnect.model.Dato;
import com.example.bluetoothconnect.model.Object_Device;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class View_Main_Startup extends AppCompatActivity implements BeaconConsumer {

    private static final String logtag = "View_Main_Startup";
    Control_Main class_Control_Main;

    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayAdapter<String> mArrayAdapter;

    private BeaconManager beaconManager;
    org.altbeacon.beacon.Region region;

    //--permissions & requests
    //request codes
    private static final int RC_EXTERNAL_STORAGE = 1;
    public static final int RC_BLUETOOTH = 1;
    //permissions to request

    @SuppressLint("InlinedApi")
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

    //devices found
    String logMode = "Devices today";
    public HashMap<String, Object_Device> deviceListe;

    private TextView textview_log;
    private TextView textview_teller;
    private TextView textview_stats_producttype;

    int teller_total = 0;
    int teller_identified = 0;
    int teller_today_total = 0;
    int teller_today_identified = 0;

    Timer motherOfTimers = new Timer();
    long starttime = 0;
    int tidSidenForrigeDiscovery = 60 * 1000;
    boolean runningDiscovery = false;

    String text_Off = "Paused discovery";
    String text_interval = "Restarting...";
    String text_On = "Discovering...";
    boolean awaitingLogUpdate = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(logtag, "onCreate");

        int orient = getRequestedOrientation();

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        class_Control_Main = new Control_Main(this);

        verifyPermissions(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        deviceListe = new HashMap<>();

        class_Control_Main.readRawDevices();

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                discover_onReceive(intent);
            }
        };
        registerReceiver(mReceiver, filter); //gjør at events som matcher filtere blir mottatt av receiver

        final Button button_devices = findViewById(R.id.button_displayDevices);
        final Button button_devices_today = findViewById(R.id.button_displayDevices_today);
        final Button button_quit = findViewById(R.id.button_quit);
        final Button button_stats_producttype = findViewById(R.id.button_stats_producttype);

        Button b = findViewById(R.id.button_discover);
        b.setText(text_Off);
        b.setOnClickListener(v -> {
            Log.i(logtag, "onClick (timer, button_discover). mBluetoothAdapter.isEnabled()=" + mBluetoothAdapter.isEnabled());
            mBluetoothAdapter.enable();
            Button b1 = (Button) v;
            if (b1.getText().equals(text_On)) {
                motherOfTimers.cancel();
                motherOfTimers.purge();
                selfRunningHandler.removeCallbacks(run);
                b1.setText(text_Off);
                runningDiscovery = false;
            } else {
                starttime = System.currentTimeMillis();
                motherOfTimers = new Timer();
                motherOfTimers.schedule(new RestartDiscovery(), 0, 1000); //varighet av discovery varierer
                motherOfTimers.schedule(new secondTask(), 0, 1000);
                selfRunningHandler.postDelayed(run, 0);
                b1.setText(text_On);
                runningDiscovery = true;
            }
        });

        button_devices.setOnClickListener(arg0 -> {
            //Log.i(logtag, "onCreate, button_devices");
            logMode = "Devices";
            setDeviceList();
            awaitingLogUpdate = true;
            textview_log.setVisibility(View.VISIBLE);
            textview_stats_producttype.setVisibility(View.INVISIBLE);
        });
        button_devices_today.setOnClickListener(arg0 -> {
            //Log.i(logtag, "onCreate, button_devices_today");
            logMode = "Devices today";
            setDeviceList();
            awaitingLogUpdate = true;
            textview_log.setVisibility(View.VISIBLE);
            textview_stats_producttype.setVisibility(View.INVISIBLE);
        });
        button_stats_producttype.setOnClickListener(arg0 -> {
            Log.i(logtag, "onCreate, button_stats_producttype");
            logMode = "";
            setStatistics_producttype();
            awaitingLogUpdate = true;
            textview_log.setVisibility(View.INVISIBLE);
            textview_stats_producttype.setVisibility(View.VISIBLE);
        });
        button_quit.setOnClickListener(arg0 -> {
            Log.i(logtag, "onCreate, button_quit");
            System.gc();
            this.finishAffinity();
            this.finishAndRemoveTask();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);

            awaitingLogUpdate = false;
        });

        assert mBluetoothAdapter != null;
        mBluetoothAdapter.startDiscovery();
        Log.i(logtag, "onCreate 2 mBluetoothAdapter.isDiscovering()=" + mBluetoothAdapter.isDiscovering());
        String[] values = new String[]{"Android List View",
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

        textview_log = findViewById(R.id.log);
        textview_log.setMovementMethod(new ScrollingMovementMethod());
        textview_log.setText("");

        textview_teller = findViewById(R.id.telling);
        textview_teller.setMovementMethod(new ScrollingMovementMethod());
        textview_teller.setText("");

        textview_stats_producttype = findViewById(R.id.stats_producttype);
        textview_stats_producttype.setMovementMethod(new ScrollingMovementMethod());
        textview_stats_producttype.setText("");

        setDeviceCount();
        setRequestedOrientation(orient);
    }

    private void setStatistics_producttype() {
        Log.i(logtag, "setStatistics_producttype");

        StringBuilder outputTextSortertHTML = new StringBuilder();

        Instant instant = Instant.now();
        instant.atOffset(ZoneOffset.UTC);
        ZonedDateTime zdt2 = instant.atZone(ZoneId.of("Europe/Oslo"));

        HashMap<String, Integer> productType;
        productType = new HashMap<>();
        //iterate alle devicer funnet
        List<Object_Device> sorterteDevicer = new ArrayList<>(deviceListe.values());
        for (Object_Device p : sorterteDevicer) {
            //if (!p.isAnonymous()) {
                //legg til producttype-stats
                if ( productType.get(p.getProductType())== null ) {
                    //Log.i(logtag, "setStatistics_producttype la til "+p.getProductType());
                    productType.put(p.getProductType(), 1);
               } else {
                    productType.put(p.getProductType(), productType.get(p.getProductType()) + 1);
                }
            //}
        }
        //sorter product type stats
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(productType.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o1).getValue().compareTo(
                        ((Map.Entry<String, Integer>) o2).getValue());
            }
        });
        Collections.reverse(list);
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        //skriv ut i HTML
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            outputTextSortertHTML.append("<br>").append(key+": "+value+" devices found");
        }

        textview_stats_producttype.setText(Html.fromHtml(outputTextSortertHTML.toString()));

    }

    public static void verifyPermissions(Activity activity) {
        //Log.i(logtag, "verifyPermissions");
        int gotAllPermissions = Math.max(
                Math.max(
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
                ),
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        );
        if (gotAllPermissions != PackageManager.PERMISSION_GRANTED) {
            Log.i(logtag, "verifyPermissions mangler noe permission");
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
        } else {
            Log.i(logtag, "verifyPermissions har alle permissions");
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
                Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion " + String.format("%s: %f: %d", b.getBluetoothName(), b.getDistance(), b.getRssi()));
            }
            if (beacons.size() > 0) {
                Log.i(logtag, "onBeaconServiceConnect.addRangeNotifier.didRangeBeaconsInRegion. The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(logtag, "onBeaconServiceConnect 8, før getBeaconParsers");//blir logget
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.i(logtag, "onBeaconServiceConnect 9, end");//blir logget
    }

    private void discover_onReceive(Intent intent) {
        //Log.i(logtag, "discover_onReceive. View_Main_Startup.this.hasWindowFocus()="+View_Main_Startup.this.hasWindowFocus());

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        LocalDateTime.now();

        ZonedDateTime.now();

        Date in = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        Instant instant = Instant.now();
        instant.atOffset(ZoneOffset.UTC);
        ZonedDateTime zdt2 = instant.atZone(ZoneId.of("Europe/Oslo"));

        Dato nydato = new Dato();
        nydato.setYear(zdt2.getYear());
        nydato.setMonth(zdt2.getMonth().getValue());
        nydato.setDayOfMonth(zdt2.getDayOfMonth());
        nydato.setHour(zdt2.getHour());
        nydato.setMinute(zdt2.getMinute());
        nydato.setSecond(zdt2.getSecond());
        nydato.setMillisecond(Instant.now().get(ChronoField.MILLI_OF_SECOND));
        StringBuilder millisecond_s = new StringBuilder(String.valueOf(nydato.getMillisecond()));
        while (millisecond_s.length() < 4) millisecond_s.insert(0, "0");

        Object_Device lagretDevice = deviceListe.get(device.toString());
        if (lagretDevice == null) {
            Object_Device newDevice = new Object_Device(device, nydato);
            deviceListe.put(device.toString(), newDevice);

            if (!newDevice.isAnonymous()) {
                Toast.makeText(getApplicationContext(), "Found new device: " + newDevice.getName() + ", " + newDevice.getMAC(), Toast.LENGTH_LONG).show();
            }
            if (newDevice.getMAC() != null) {
                setDeviceCount();
                lagretDevice = newDevice;
            }
        } else { //Gjenoppdaget device. sjekk at det ikke spammes eventer om samme ting

            long diffInSecs = lagretDevice.getSecondsPast(nydato);
            if (diffInSecs < 1) {//spam
                return;
            }
            if (device.getName() == null) {
                lagretDevice.updateAnonymous(device);
            }
        }

        if (!(device.getName() == null)) {
            lagretDevice = deviceListe.get(device.toString());
        }

        if (lagretDevice != null) {
            lagretDevice.setFound(nydato);
        }

        awaitingLogUpdate = true;
    }

    private void connectToMAC(String address) {
        Log.i(logtag, "connectToMAC address=" + address);
        try {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothDevice remoteDevice = bluetoothManager.getAdapter().getRemoteDevice(address);
            Log.i(logtag, "Connected to " + remoteDevice.getName());
            Log.i(logtag, "Bound=" + remoteDevice.getBondState());
            Log.i(logtag, "describeContents=" + remoteDevice.describeContents());
            if (address.equals("8C:79:F5:03:79:29")) {
                remoteDevice.createBond();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDeviceCount() {
        //Log.i(logtag, "setDeviceCount");
        teller_total = deviceListe.size();
        textview_teller.setText(teller_total + " devices found all-time");
    }

    @SuppressLint("SetTextI18n")
    private void setDeviceList() {
        //Log.i(logtag, "setDeviceList source="+source+" prev.source="+prvSource );

        StringBuilder outputTextSortertHTML = new StringBuilder();

        Instant instant = Instant.now();
        instant.atOffset(ZoneOffset.UTC);
        ZonedDateTime zdt2 = instant.atZone(ZoneId.of("Europe/Oslo"));

        teller_total = deviceListe.size();
        teller_identified = 0;
        teller_today_identified = 0;
        teller_today_total = 0;

        List<Object_Device> sorterteDevicer = new ArrayList<>(deviceListe.values());
        sorterteDevicer.sort((a, b) -> b.getLastSeen().compareTo(a.getLastSeen()));

        for (Object_Device p : sorterteDevicer) {
            if (((p.getLastSeen_Year() == zdt2.getYear()) && (p.getLastSeen_Month() == zdt2.getMonth().getValue()) && (p.getLastSeen_Day() == zdt2.getDayOfMonth()))) {
                teller_today_total ++;
            }
            if (!p.isAnonymous()) {
                teller_identified++;
                if (((p.getLastSeen_Year() == zdt2.getYear()) && (p.getLastSeen_Month() == zdt2.getMonth().getValue()) && (p.getLastSeen_Day() == zdt2.getDayOfMonth()))) {
                    teller_today_identified++;
                    outputTextSortertHTML.append("<br><br>").append(p.getSummarySimple());
                } else {

                    if (p.getLastSeen_Year() == zdt2.getYear()) {
                        if (p.getLastSeen_Month() == zdt2.getMonth().getValue()) {
                            p.getLastSeen_Day();
                            zdt2.getDayOfMonth();//Log.i(logtag, "setDeviceList "+teller_non_null+ " samme dag, viser uansett");
                        }
                    }
                    if (!logMode.contains("today")) {

                        outputTextSortertHTML.append("<br><br>").append(p.getSummarySimple());
                    }
                }
            }
        }


        textview_log.setText(Html.fromHtml(outputTextSortertHTML.toString()));
        textview_teller.setText(teller_total + " devices found all-time, of which " + teller_identified + " are (partially) identified. " + teller_today_total+" devices found today, "+ teller_today_identified + " of these were (partially) identified.");

    }

    public void leggInnLagretDevice_fraDevices(String test) {
        //Log.i(logtag, "leggInnLagretDevice_fraDevices test="+test);
        deviceListe.size();

        //sjekk om linjen inneholder flere devicer
        long count = test.chars().filter(ch -> ch == '|').count();
        if (count >= 14) {
            int end = test.indexOf("|202");
            String device1 = test.substring(0, (end + 25));
            String device2 = test.substring((end + 25));
            if (device2.length() == 0) {
            } else {
                leggInnLagretDevice_fraDevices(device1);
                leggInnLagretDevice_fraDevices(device2);
            }
        } else {

            String MAC = "";
            String[] separated = test.split("\\|");
            for (String item : separated) {
                if (item.contains(".")) {
                } else if (item.contains(":")) {
                    if (item.length() == 17) {
                        MAC = item;
                    }
                }
            }
            if (MAC.length() == 0) {
                return;
            }

            Object_Device lagretDevice = deviceListe.get(MAC);
            Object_Device newDevice = new Object_Device(test);
            if (lagretDevice == null) {
                if (newDevice.getMAC() != null) {
                    deviceListe.put(MAC, newDevice);
                }
            } else {
                //oppdater lagret device-objekt med siste sett-tidspunkt (siden flere filer med ulike tidspunkter blir lastet)
                lagretDevice.setFound(newDevice.getDato());
            }

        }
    }


    //runs without timer be reposting self
    Handler selfRunningHandler = new Handler();
    Runnable run = new Runnable() {

        public void run() { //oppdaterer knapp og device list
            //Log.i(logtag, "Runnable.run. mBluetoothAdapter.isDiscovering()="+mBluetoothAdapter.isDiscovering()+" tidSidenForrigeDiscovery="+tidSidenForrigeDiscovery);
            if (mBluetoothAdapter.isDiscovering()) {
                ((Button) findViewById(R.id.button_discover)).setText(text_On);
                tidSidenForrigeDiscovery = 0;
            } else {
                tidSidenForrigeDiscovery = tidSidenForrigeDiscovery + 500;
                if (runningDiscovery) {
                    ((Button) findViewById(R.id.button_discover)).setText(text_interval);
                } else {
                    ((Button) findViewById(R.id.button_discover)).setText(text_Off);
                }
            }

            if (awaitingLogUpdate) {
                if (logMode.contains("Devices")) {
                    setDeviceList();
                }
                awaitingLogUpdate = false;
            }

            selfRunningHandler.postDelayed(this, 500);
        }
    };

    //tells handler to send a message
    class RestartDiscovery extends TimerTask {

        @Override
        public void run() {
            //Log.i(logtag, "RestartDiscovery.run. View_Main_Startup.this.hasWindowFocus()="+View_Main_Startup.this.hasWindowFocus()+" tidSidenForrigeDiscovery="+tidSidenForrigeDiscovery);
            if (View_Main_Startup.this.hasWindowFocus() == true) {
                if (tidSidenForrigeDiscovery > 5000) {
                    class_Control_Main.writeDevicesToFile(deviceListe);
                    mBluetoothAdapter.startDiscovery();
                }
            } else {
                Log.i(logtag, "RestartDiscovery.run. App i dvale, ikke kjør");
            }
        }
    }

    //tells activity to run on ui thread
    class secondTask extends TimerTask {

        @Override
        public void run() {
            //Log.i(logtag, "secondTask.run. View_Main_Startup.this.hasWindowFocus()="+View_Main_Startup.this.hasWindowFocus());
            View_Main_Startup.this.runOnUiThread(() -> {
                //Log.i(logtag, "secondTask.run.run");
            });
        }
    }

    @Override
    public void onPause() { //trigges når app legges i bakgrunnen. Stopper alle timere
        super.onPause();
        //vurder å la kjøring fortsette så lenge mobil er i lademodus
        Log.i(logtag, "onPause");
        motherOfTimers.cancel();
        motherOfTimers.purge();
        selfRunningHandler.removeCallbacks(run);
        Button b = findViewById(R.id.button_discover);
        b.setText(text_Off);
        //mBluetoothAdapter.disable(); //smittestopp-Appen trenger at Bluetooth alltid er på
        Log.i(logtag, "onClick (timer, button_discover). mBluetoothAdapter.isEnabled()=" + mBluetoothAdapter.isEnabled());
    }


}