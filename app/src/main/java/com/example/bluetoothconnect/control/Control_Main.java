package com.example.bluetoothconnect.control;

import android.os.Environment;
import android.util.Log;

import com.example.bluetoothconnect.view.View_Main_Startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class Control_Main {

    View_Main_Startup class_View_Main;
    private static final String logtag = "Control_Main";

    public Control_Main(View_Main_Startup view_main) {
        class_View_Main = view_main;

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
                String allText="";
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

}