package com.example.bluetoothconnect.control;

import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;

import com.example.bluetoothconnect.model.Object_Device;
import com.example.bluetoothconnect.view.View_Main_Startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Control_Main {

    View_Main_Startup class_View_Main;
    private static final String logtag = "Control_Main";

    //HashMap<Dato, String> loggListe_dato;
    HashMap<String, String> loggListe_String;

    int logsRead = 0;

    public Control_Main(View_Main_Startup view_main) {
        class_View_Main = view_main;

        //loggListe_dato = new HashMap<>();
        loggListe_String = new HashMap<>();
    }

    public void writeDevicesToFile(HashMap<String, Object_Device> deviceListe) {
        //Log.i(logtag, "writeDevicesToFile");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Berits_apper");
            File inputsFile = new File(dir, "BluetoothConnect_Devices.txt");
            inputsFile.delete();
            //Log.i(logtag, "writeDevicesToFile fil slettet="+deleted);
        } catch (Exception e) {
            Log.i(logtag, "writeDevicesToFile 4b inputs failed");
            e.printStackTrace();
        }

        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Berits_apper");
            File inputsFile = new File(dir, "BluetoothConnect_Devices" + currentDateandTime + ".txt");
            inputsFile.delete();
            //Log.i(logtag, "writeDevicesToFile fil slettet="+deleted);
        } catch (Exception e) {
            Log.i(logtag, "writeDevicesToFile 4b inputs failed");
            e.printStackTrace();
        }

        try {
            //Log.i(logtag, "writeDevicesToFile: skal lagre devicer");
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Berits_apper");
            File inputsFile1 = new File(dir, "BluetoothConnect_Devices.txt");
            File inputsFile2 = new File(dir, "BluetoothConnect_Devices" + currentDateandTime + ".txt");
            try {
                dir.mkdirs();
            } catch (Exception ignored) {
            }

            FileOutputStream fos1 = new FileOutputStream(inputsFile1.getAbsolutePath(), true); // true will be same as Context.MODE_APPEND
            List<Object_Device> sorterteDevicer1 = new ArrayList<>(deviceListe.values());
            for (Object_Device p : sorterteDevicer1) {
                //Log.i(logtag, "writeDevicesToFile fil Skriver til fil 1: "+p.getSummary_raw_withDate("writeDevicesToFile"));
                fos1.write(p.getSummary_raw_withDate().getBytes());
                fos1.write(Objects.requireNonNull(System.getProperty("line.separator")).getBytes());
            }
            fos1.close();

            FileOutputStream fos2 = new FileOutputStream(inputsFile2.getAbsolutePath(), true); // true will be same as Context.MODE_APPEND

            List<Object_Device> sorterteDevicer2 = new ArrayList<>(deviceListe.values());
            for (Object_Device p : sorterteDevicer2) {
                //Log.i(logtag, "writeDevicesToFile fil Skriver til fil 2: "+p.getSummary_raw_withDate("writeDevicesToFile"));
                fos2.write(p.getSummary_raw_withDate().getBytes());
                fos2.write(Objects.requireNonNull(System.getProperty("line.separator")).getBytes());
            }
            fos2.close();

        } catch (Exception e) {
            Log.i(logtag, "writeDevicesToFile 4b inputs failed");
            e.printStackTrace();
        }

    }


    public void readRawDevices() {
        logsRead = 0;

        String path = Environment.getExternalStorageDirectory().toString() + "/Berits_apper";
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Berits_apper/" + "BluetoothConnect_Devices.txt");
            InputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size
            logsRead = logsRead+lesFil(path, file.getName());
            Log.d(logtag, "readRawDevices.1 logsRead:" + logsRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(logtag, " count 1=" + class_View_Main.deviceListe.size());
        Log.i(logtag, " Devices saved #1: " + class_View_Main.deviceListe.size());

        try {
            File directory = new File(path);
            File[] files = directory.listFiles();
            Arrays.sort(files, Collections.reverseOrder());
            Log.d(logtag, "Files in directory: " + files.length);
            for (File value : files) {
                if (( logsRead <3 ) && (!value.getName().equals("BluetoothConnect_Devices.txt"))) {
                    logsRead = logsRead+lesFil(path, value.getName());
                    Log.d(logtag, "readRawDevices.2 logsRead:" + logsRead);
                }
            }
            Log.i(logtag, " Devices saved #2: " + class_View_Main.deviceListe.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(logtag, " Devices saved #3: " + class_View_Main.deviceListe.size());
    }

    private int lesFil(String path, String filnavn){
        Log.d(logtag, "lesFil FileName:" + filnavn);
        Log.d(logtag, "lesFil.1 logsRead:" + logsRead);
        try {
                if ( logsRead <3 ) {
                    //Log.d(logtag, "FileName:" + value.getName());
                    if (filnavn.contains("_Devices2")) {
                        File file = new File(path, filnavn);
                        InputStream is = new FileInputStream(file);

                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

                        String test;
                        while (true) {
                            test = br.readLine();
                            if (test == null) break;

                            class_View_Main.leggInnLagretDevice_fraDevices(test);
                            //Log.i(logtag, "lesFil Devices saved: " + class_View_Main.deviceListe.size());
                        }
                        isr.close();
                        is.close();
                        br.close();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        Log.d(logtag, "lesFil.2 logsRead:" + logsRead);
        return 1;
    }
}