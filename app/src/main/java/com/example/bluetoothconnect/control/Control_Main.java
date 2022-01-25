package com.example.bluetoothconnect.control;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Environment;
import android.util.Log;

import com.example.bluetoothconnect.model.Dato;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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

    /*public void writeToEventFile(String inputSentence){

        try {
            //Log.i(logtag, "saveInputToFile: 3 skal lagres: "+inputSentence);
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/Berits_apper");
            File inputsFile = new File(dir, "BluetoothConnect.txt");
            try {
                dir.mkdirs();
            } catch (Exception ignored){
            }

            FileOutputStream fos = new FileOutputStream (inputsFile.getAbsolutePath(), true); // true will be same as Context.MODE_APPEND

            fos.write(inputSentence.getBytes());
            fos.write(Objects.requireNonNull(System.getProperty("line.separator")).getBytes());
            fos.close();

        } catch (Exception e) {
            Log.i(logtag, "saveInputToFile 4b inputs failed");
            e.printStackTrace();
        }
    }*/

/*    public void readRawEvents(){
        //Log.i(logtag, "readRawEvents start");

        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Berits_apper/"+"BluetoothConnect.txt");
            InputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

            try {
                String test;
                while (true){
                    test = br.readLine();
                    if(test == null) break;

                    class_View_Main.leggInnLagretDevice_fraEvents(test);
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
    }*/

    public void logEvent(String event, Dato dato){
       loggListe_String.put(dato.getDate(), event);
   }

    public StringBuilder setLogText(String source){
        //Log.i(logtag, "setLogText source="+source);
        StringBuilder outputText = new StringBuilder();

        TreeMap<String, String> sorted = new TreeMap<>(Collections.reverseOrder());
        sorted.putAll(loggListe_String);
        for (Map.Entry<String, String> entry : sorted.entrySet()) { //blir ascending sort
            outputText.append("\n").append(((HashMap.Entry) entry).getValue());
        }

        //Log.i(logtag, "setLogText loggListe_dato.size()=" + loggListe_dato.size());
        return outputText;
    }

    public void writeDevicesToFile(HashMap<String, Object_Device> deviceListe) {
        //Log.i(logtag, "writeDevicesToFile");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/Berits_apper");
            File inputsFile = new File(dir, "BluetoothConnect_Devices.txt");
            boolean deleted = inputsFile.delete();
            //Log.i(logtag, "writeDevicesToFile fil slettet="+deleted);
        } catch (Exception e) {
            Log.i(logtag, "writeDevicesToFile 4b inputs failed");
            e.printStackTrace();
        }

        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/Berits_apper");
            File inputsFile = new File(dir, "BluetoothConnect_Devices"+ currentDateandTime+".txt");
            boolean deleted = inputsFile.delete();
            //Log.i(logtag, "writeDevicesToFile fil slettet="+deleted);
        } catch (Exception e) {
            Log.i(logtag, "writeDevicesToFile 4b inputs failed");
            e.printStackTrace();
        }

        try {
            //Log.i(logtag, "writeDevicesToFile: skal lagre devicer");
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/Berits_apper");
            File inputsFile1 = new File(dir, "BluetoothConnect_Devices.txt");
            File inputsFile2 = new File(dir, "BluetoothConnect_Devices"+ currentDateandTime+".txt");
            try {
                dir.mkdirs();
            } catch (Exception ignored){
            }

            FileOutputStream fos1 = new FileOutputStream (inputsFile1.getAbsolutePath(), true); // true will be same as Context.MODE_APPEND
            List<Object_Device> sorterteDevicer1 = new ArrayList<>(deviceListe.values());
            for (Object_Device p : sorterteDevicer1) {
                fos1.write(p.getSummary_raw_withDate("writeDevicesToFile").getBytes());
                fos1.write(Objects.requireNonNull(System.getProperty("line.separator")).getBytes());
            }
            fos1.close();

            FileOutputStream fos2 = new FileOutputStream (inputsFile2.getAbsolutePath(), true); // true will be same as Context.MODE_APPEND

            List<Object_Device> sorterteDevicer2 = new ArrayList<>(deviceListe.values());
            for (Object_Device p : sorterteDevicer2) {
                fos2.write(p.getSummary_raw_withDate("writeDevicesToFile").getBytes());
                fos2.write(Objects.requireNonNull(System.getProperty("line.separator")).getBytes());
            }
            fos2.close();

        } catch (Exception e) {
            Log.i(logtag, "writeDevicesToFile 4b inputs failed");
            e.printStackTrace();
        }
    }


    public void readRawDevices(){
        //Log.i(logtag, "readRawDevices start");

        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Berits_apper/"+"BluetoothConnect_Devices.txt");
            InputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

            try {
                String test;
                while (true){
                    test = br.readLine();
                    if(test == null) break;

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
        Log.i(logtag, " count 1="+class_View_Main.deviceListe.size());


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Berits_apper/"+"BluetoothConnect_Devices"+ currentDateandTime+".txt");
            InputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

            try {
                String test;
                while (true){
                    test = br.readLine();
                    if(test == null) break;

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
        Log.i(logtag, " count 2="+class_View_Main.deviceListe.size());
    }
}