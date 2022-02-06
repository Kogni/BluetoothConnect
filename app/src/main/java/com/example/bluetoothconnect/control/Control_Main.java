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
        //Log.i(logtag, "readRawDevices start");

        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Berits_apper/" + "BluetoothConnect_Devices.txt");
            InputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

            try {
                String test;
                while (true) {
                    test = br.readLine();
                    if (test == null) break;

                    class_View_Main.leggInnLagretDevice_fraDevices(test);
                }
                isr.close();
                is.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(logtag, " count 1=" + class_View_Main.deviceListe.size());

        try {
            String path = Environment.getExternalStorageDirectory().toString() + "/Berits_apper";
            Log.d(logtag, "Directory path: " + path);
            File directory = new File(path);
            File[] files = directory.listFiles();
            Log.d(logtag, "Files in directory: " + files.length);
            for (File value : files) {
                String filename = value.getName();
                Log.d(logtag, "FileName:" + value.getName());
                if (filename.contains("_Devices2")) {
                    File file = new File(path, filename);
                    InputStream is = new FileInputStream(file);

                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

                    String test;
                    while (true) {
                        test = br.readLine();
                        if (test == null) break;

                        class_View_Main.leggInnLagretDevice_fraDevices(test);
                    }
                    isr.close();
                    is.close();
                    br.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(logtag, " Devices saved: " + class_View_Main.deviceListe.size());
        //Log.i(logtag, "readRawDevices TVn min="+class_View_Main.deviceListe.get("8C:79:F5:03:79:29"));
    }
}