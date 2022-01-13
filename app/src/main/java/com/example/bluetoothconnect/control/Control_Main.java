package com.example.bluetoothconnect.control;

import android.os.Environment;
import android.util.Log;

import com.example.bluetoothconnect.model.Dato;
import com.example.bluetoothconnect.view.View_Main_Startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Control_Main {

    View_Main_Startup class_View_Main;
    private static final String logtag = "Control_Main";

    HashMap<Dato, String> loggListe_dato;

    public Control_Main(View_Main_Startup view_main) {
        class_View_Main = view_main;

        loggListe_dato = new HashMap<>();
    }

    public void writeToSDFile(String inputSentence){

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
    }

    public void readRaw(){
        Log.i(logtag, "readRaw start");

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

                    class_View_Main.leggInnLagretDevice(test);
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
    }

    public void logEvent(String event, Dato dato){
        //Log.i(logtag, "logEvent start event="+event+" tidspunkt="+tidspunkt);
        //Log.i(logtag, "logEvent start tidspunkt="+dato);
        //Log.i(logtag, "logEvent 1 loggListe.hashCode()="+ loggListe_dato.hashCode()+" loggListe.size()="+ loggListe_dato.size());
        loggListe_dato.put(dato, event);

        loggListe_dato.entrySet();
        //Log.i(logtag, "logEvent 2 loggListe.hashCode()="+ loggListe_dato.hashCode()+" loggListe.size()="+ loggListe_dato.size());
    }

    public StringBuilder setLogText(String source){
        //Log.i(logtag, "setLogText source="+source);
        StringBuilder outputText = new StringBuilder();

        for (Map.Entry<Dato, String> dateStringEntry : loggListe_dato.entrySet()) {
            outputText.append("\n").append(((HashMap.Entry) dateStringEntry).getValue());
        }

        /*SortedSet<Date> keys = new TreeSet<>(loggListe.keySet());
        SortedSet<Date> keys2 = new TreeSet<>(loggListe_long.keySet());
        for (Date key : keys) {
            //loggListe.get(key);
            //Log.i(logtag, "setLogText outputTextSortert="+outputTextSortert);
            loggListe_long.get(key);
            //outputTextSortert.append("\n").append(loggListe.get(key));
            outputTextSortert.append("\n").append(loggListe_long.get(key));
        }*/
        //Log.i(logtag, "setLogText loggListe_dato.size()=" + loggListe_dato.size());
        return outputText;
    }
}