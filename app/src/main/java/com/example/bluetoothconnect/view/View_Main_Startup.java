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
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    String logMode = "Events";
    public HashMap<String, Object_Device> deviceListe;

    private TextView textview_log;
    private TextView textview_teller;

    int teller_total = 0;
    int teller_non_null = 0;
    int teller_today = 0;

    Timer timer = new Timer();
    long starttime = 0;

    String text_Off = "Paused discovery";
    String text_On = "Discovering...";
    boolean awaitingLogUpdate = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(logtag, "onCreate");

        setContentView(R.layout.activity_main);

        class_Control_Main = new Control_Main(this);

        verifyPermissions(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        deviceListe = new HashMap<>();

        //class_Control_Main.readRawEvents();
        class_Control_Main.readRawDevices();

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                discover_onReceive(intent);
            }
        };
        registerReceiver(mReceiver, filter); //gjør at events som matcher filtere blir mottatt av receiver

        //final Button button_discover = findViewById(R.id.button_discover);
        final Button button_logging = findViewById(R.id.button_displayLogg);
        final Button button_devices = findViewById(R.id.button_displayDevices);
        final Button button_devices_today = findViewById(R.id.button_displayDevices_today);

        Button b = (Button) findViewById(R.id.button_discover);
        b.setText(text_Off);
        b.setOnClickListener(v -> {
            Log.i(logtag, "onClick (timer)");
            Button b1 = (Button) v;
            if (b1.getText().equals(text_On)) {
                timer.cancel();
                timer.purge();
                selfRunningHandler.removeCallbacks(run);
                b1.setText(text_Off);
            } else {
                starttime = System.currentTimeMillis();
                timer = new Timer();
                timer.schedule(new RestartDiscovery(), 0, 60000);
                timer.schedule(new secondTask(), 0, 500);
                selfRunningHandler.postDelayed(run, 0);
                b1.setText(text_On);
            }
        });

        button_logging.setOnClickListener(arg0 -> {
            //Log.i(logtag, "onCreate, button_logging");
            logMode = "Events";
            //setLogText("button_logging");
            awaitingLogUpdate = true;

        });
        button_devices.setOnClickListener(arg0 -> {
            //Log.i(logtag, "onCreate, button_devices");
            logMode = "Devices";
            setDeviceList("button_devices", "-");
            awaitingLogUpdate = true;
        });
        button_devices_today.setOnClickListener(arg0 -> {
            //Log.i(logtag, "onCreate, button_devices_today");
            logMode = "Devices today";
            setDeviceList("button_devices_today", "-");
            awaitingLogUpdate = true;
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
        textview_log.setText("onCreate");

        textview_teller = findViewById(R.id.telling);
        textview_teller.setMovementMethod(new ScrollingMovementMethod());
        textview_teller.setText("onCreate");

        setDeviceCount();

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
        //Log.i(logtag, "verifyPermissions gotAllPermissions="+gotAllPermissions+" PackageManager.PERMISSION_GRANTED="+PackageManager.PERMISSION_GRANTED);
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
        //Log.i(logtag, "discover_onReceive");

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
        //String month_s = String.valueOf(nydato.getMonth());
        //String day_s = String.valueOf(nydato.getDayOfMonth());
        //String hour_s = String.valueOf(nydato.getHour());
        //String minute_s = String.valueOf(nydato.getMinute());
        //String second_s = String.valueOf(nydato.getSecond());
        String millisecond_s = String.valueOf(nydato.getMillisecond());
        //if (month_s.length() < 2) month_s = "0" + month_s;
        //if (day_s.length() < 2) day_s = "0" + day_s;
        //if (hour_s.length() < 2) hour_s = "0" + hour_s;
        //if (minute_s.length() < 2) minute_s = "0" + minute_s;
        //if (second_s.length() < 2) second_s = "0" + second_s;
        while (millisecond_s.length() < 4) millisecond_s = "0" + millisecond_s;
        //String tidspunkt9 = nydato.getYear() + "." + month_s + "." + day_s + " " + hour_s + ":" + minute_s + ":" + second_s + "." + millisecond_s;
        //Log.i(logtag, "discover_onReceive nydato, tekst=" + tidspunkt9);

        Object_Device lagretDevice = deviceListe.get(device.toString());
        if (lagretDevice == null) {
            Object_Device newDevice = new Object_Device(device, nydato);
            deviceListe.put(device.toString(), newDevice);

            if (newDevice.isAnonymous() == false) {
                Toast.makeText(getApplicationContext(), "Found new device: " + newDevice.getName() + ", " + newDevice.getMAC(), Toast.LENGTH_LONG).show();
            }
            if (newDevice.getMAC() != null) {
                setDeviceCount();
                lagretDevice = newDevice;
            }
        } else { //sjekk at det ikke spammes eventer om samme ting

            long diffInMillies = lagretDevice.getSecondsPast(nydato);
            if (diffInMillies < 1) {//spam
                //Log.i(logtag, "discover_onReceive returnerer pga for liten forskjell i millisekunder=" + diffInMillies);
                return;
            }
            //connectToMAC(lagretDevice.getMAC());
        }

        if (!(device.getName() == null)) {
            lagretDevice = deviceListe.get(device.toString());
        }

        if (lagretDevice != null) {
            lagretDevice.setFound(nydato);
            //Log.i(logtag, "discover_onReceive lagretDevice.getLastSeen=" + lagretDevice.getLastSeen());
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
            //Log.i(logtag, "connectToMAC try 2 completed. Device="+mBluetoothDevice+", "+mBluetoothDevice.getType()+", "+mBluetoothDevice.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leggInnLagretDevice_fraEvents(String linje) {
        //Log.i(logtag, "leggInnLagretDevice_fraEvents linje="+linje);

        String MAC = linje.substring(20);
        String[] separated = linje.split("\\|");
        for (String item : separated) {
            if (item.contains(":")) {
                MAC = item;
            }
        }

        Object_Device lagretDevice = deviceListe.get(MAC);
        if (lagretDevice == null) {
            Object_Device newDevice = new Object_Device(linje);
            if (newDevice.getMAC() != null) {
                deviceListe.put(MAC, newDevice);
            }
        }
        awaitingLogUpdate = true;
    }


    private void setLogText(String source) {
        //Log.i(logtag, "setLogText source="+source+" logMode="+ logMode);
        textview_log.setText(class_Control_Main.setLogText(source));
    }

    @SuppressLint("SetTextI18n")
    private void setDeviceCount() {
        //Log.i(logtag, "setDeviceCount");
        teller_total = deviceListe.size();
        textview_teller.setText(teller_total + " devices found all-time");
    }

    @SuppressLint("SetTextI18n")
    private void setDeviceList(String source, String prvSource) {
        //Log.i(logtag, "setDeviceList source="+source+" prev.source="+prvSource );

        StringBuilder outputTextSortertHTML = new StringBuilder();

        Instant instant = Instant.now();
        instant.atOffset(ZoneOffset.UTC);
        ZonedDateTime zdt2 = instant.atZone(ZoneId.of("Europe/Oslo"));

        teller_total = deviceListe.size();
        teller_non_null = 0;
        teller_today = 0;

        List<Object_Device> sorterteDevicer = new ArrayList<>(deviceListe.values());
        sorterteDevicer.sort((a, b) -> {
            return b.getLastSeen().compareTo(a.getLastSeen());
        });


        for (Object_Device p : sorterteDevicer) {
            if (!p.isAnonymous()) {
                teller_non_null++;
                Object_Device devicen = p;
                if (((devicen.getLastSeen_Year() == zdt2.getYear()) && (devicen.getLastSeen_Month() == zdt2.getMonth().getValue()) && (devicen.getLastSeen_Day() == zdt2.getDayOfMonth()))) {
                    teller_today++;
                    outputTextSortertHTML.append("<br><br>").append(devicen.getSummarySimple("setDeviceList", source));
                } else {
                    if (devicen.getLastSeen_Year() == zdt2.getYear()) {
                        if (devicen.getLastSeen_Month() == zdt2.getMonth().getValue()) {
                            devicen.getLastSeen_Day();
                            zdt2.getDayOfMonth();//Log.i(logtag, "setDeviceList "+teller_non_null+ " samme dag, viser uansett");
                        }  //Log.i(logtag, "setDeviceList "+teller_non_null+ " feil mnd: devicen.getLastSeen_Month()="+devicen.getLastSeen_Month()+" "+zdt2.getMonth().getValue());
                    }  //Log.i(logtag, "setDeviceList "+teller_non_null+ " Feil år: devicen.getLastSeen_Year()="+devicen.getLastSeen_Year()+" "+zdt2.getYear());
                    if (!logMode.contains("today")) {
                        //Log.i(logtag, "setDeviceList viser alle uansett");
                        outputTextSortertHTML.append("<br><br>").append(devicen.getSummarySimple("setDeviceList", source));
                    }
                }
            }
        }


        textview_log.setText(Html.fromHtml(outputTextSortertHTML.toString()));
        textview_teller.setText(teller_total + " devices found all-time, of which " + teller_non_null + " are identified. " + teller_today + " of these were last seen today");

        //Log.i(logtag, "setDeviceList outputTextSortertHTML="+outputTextSortertHTML);
    }

    //runs without timer be reposting self
    Handler selfRunningHandler = new Handler();
    Runnable run = new Runnable() {

        public void run() {
            //Log.i(logtag, "Runnable.run");
            //Log.i(logtag, "Runnable.run mBluetoothAdapter.isDiscovering()="+mBluetoothAdapter.isDiscovering());
            if (mBluetoothAdapter.isDiscovering()) {
                ((Button) findViewById(R.id.button_discover)).setText(text_On);
            } else {
                ((Button) findViewById(R.id.button_discover)).setText(text_Off);
            }

            if (awaitingLogUpdate) {
                if (logMode.contains("Devices")) {
                    setDeviceList("Runnable.run", "-");
                } else {
                    setLogText("Runnable.run");
                }
                awaitingLogUpdate = false;
            }

            selfRunningHandler.postDelayed(this, 1000);
        }
    };

    public void leggInnLagretDevice_fraDevices(String test) {
        //Log.i(logtag, "leggInnLagretDevice_fraDevices test="+test);
        int tellerFoer = deviceListe.size();

        String MAC = "";
        String[] separated = test.split("\\|");
        for (String item : separated) {
            if (item.contains(".")) {
            } else if (item.contains(":")&& (item.length()==17)) {
                //Log.i(logtag, "leggInnLagretDevice_fraDevices vil sette MAC="+item);
                MAC = item;
            }
        }
        if (MAC.length()==0){
            return;
        }
        //Log.i(logtag, "leggInnLagretDevice_fraDevices MAC="+MAC);

        Object_Device lagretDevice = deviceListe.get(MAC);
        if (lagretDevice == null) {
            //Log.i(logtag, "leggInnLagretDevice_fraDevices legger inn ny device, MAC="+MAC);
            Object_Device newDevice = new Object_Device(test, "leggInnLagretDevice_fraDevices");
            if ( newDevice.getMAC()==null){

            } else {
                deviceListe.put(MAC, newDevice);
            }
        }

        int tellerEtter = deviceListe.size();
        int diff = tellerEtter - tellerFoer;
        if (diff == 0) {
            //Log.i(logtag, "leggInnLagretDevice_fraDevices 1 ble ikke lagret: MAC=" + MAC);
            if (lagretDevice == null) {
                Log.i(logtag, "leggInnLagretDevice_fraDevices 2a aldri blitt lagret: MAC=" + MAC);
            } else {
                //Log.i(logtag, "leggInnLagretDevice_fraDevices 2b allerede lagret: MAC=" + MAC);
            }
        }
    }

    //tells handler to send a message
    class RestartDiscovery extends TimerTask {

        @Override
        public void run() {

            mBluetoothAdapter.startDiscovery();
            class_Control_Main.writeDevicesToFile(deviceListe);

        }
    }

    //tells activity to run on ui thread
    class secondTask extends TimerTask {

        @Override
        public void run() {
            //Log.i(logtag, "secondTask.run");
            View_Main_Startup.this.runOnUiThread(() -> {
                //Log.i(logtag, "secondTask.run.run");
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(logtag, "onPause");
        timer.cancel();
        timer.purge();
        selfRunningHandler.removeCallbacks(run);
        Button b = (Button) findViewById(R.id.button_discover);
        b.setText(text_Off);
    }


}