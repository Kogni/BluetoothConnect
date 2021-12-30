package com.example.bluetoothconnect.model;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Object_Device {

    //BluetoothDevice device;
    Date LastSeen;

    public String getName;
    public int getType;
    public String getBluetoothClass;
    public int getDeviceClass;
    public int getMajorDeviceClass;
    public String MAC;
    public int getBondState;

    public Object_Device(BluetoothDevice device1, Date currentTime) {
        Log.i(this.toString(), "Object_Device lager device fra BluetoothDevice");
        //device = device1;
        LastSeen = currentTime;
        String nullSjekk = device1.getName();
        getName = device1.getName();
        getType = device1.getType();
        getBluetoothClass = device1.getBluetoothClass().toString();
        getDeviceClass = device1.getBluetoothClass().getDeviceClass();
        getMajorDeviceClass = device1.getBluetoothClass().getMajorDeviceClass();
        MAC = device1.getAddress();
        getBondState = device1.getBondState();
    }

    public Object_Device(String linje) {
        //Log.i(this.toString(), "Object_Device lager device fra logg: "+linje);
        //String nullSjekk = device.getName();
        LastSeen = Calendar.getInstance().getTime();

        String[] separated = linje.split("\\|");
        int x = 0;
        for (String item : separated) {
            x++;
            //Log.i(this.toString(), "Object_Device x="+x+" item=" + item);
            if ( x == 1 ){
                //tidspunkt
            } else if ( x == 2 ){
                getName = item;
            } else if ( x == 3 ){
                getType = Integer.parseInt(item);
            } else if ( x == 4 ){
                getBluetoothClass = item;
            } else if ( x == 5 ){
                getDeviceClass = Integer.parseInt(item);
            } else if ( x == 6 ){
                getMajorDeviceClass = Integer.parseInt(item);
            } else if ( x == 7 ){
                MAC = item;
            } else if ( x == 8 ){
                getBondState = Integer.parseInt(item);
            }
            if ( item.contains(":")){
                MAC = item;
            }
        }

    }

    public void setFound(Date currentTime) {
        LastSeen = currentTime;
    }

    public Date getLastSeen() {
        return LastSeen;
    }

    public String getSummary_raw() {
        //Log.i(this.toString(), "getSummary_raw device="+device);
        //Log.i(this.toString(), "getSummary_raw getName="+getName);
        //return getName+"|"+device.getType()+"|"+device.getBluetoothClass()+"|"+device.getBluetoothClass().getDeviceClass()+"|"+device.getBluetoothClass().getMajorDeviceClass()+"|"+device+"|"+device.getBondState();
        return getName+"|"+getType+"|"+getBluetoothClass+"|"+getDeviceClass+"|"+getMajorDeviceClass+"|"+MAC+"|"+getBondState;
    }
    public String getSummarySimple() {

        String btDeviceName = getName;
        if ( btDeviceName==null){
        } else if ( btDeviceName.equals("null")){
        } else {
            btDeviceName = "<b>"+btDeviceName+"</b>";
        }

        String bttypeDescription = getType+"";
        /*
            public static final int DEVICE_TYPE_CLASSIC = 1;
            public static final int DEVICE_TYPE_DUAL = 3;
            public static final int DEVICE_TYPE_LE = 2;
            public static final int DEVICE_TYPE_UNKNOWN = 0;
         */
        if ( getType == 0){
            bttypeDescription = "Unknown";
        } else if ( getType == 1){
            bttypeDescription = "<b>BR/EDR</b>";
        } else if ( getType == 2){
            bttypeDescription = "<b>LE-only</b>";
        } else if ( getType == 3){
            bttypeDescription = "<b>BR/EDR/LE</b>";
        }

        String btclassDescription = getBluetoothClass+"";
        // CoD hex
        //http://domoticx.com/bluetooth-class-of-device-lijst-cod/
        //43c
        //50c
        //704
        //1f00
        //c043c
        //c243c
        //8043c
        //60680
        //200408
        //240414
        //5a020c
        if ( getBluetoothClass.equals("43c")){
            btclassDescription = "<b>TV</b>";
        } else if ( getBluetoothClass.equals("50c")){
            btclassDescription = "Unknown";
        } else if ( getBluetoothClass.equals("704")){
            btclassDescription = "<b>Wearable</b>";
        } else if ( getBluetoothClass.equals("1f00")){
            btclassDescription = "Unknown, various";
        } else if ( getBluetoothClass.equals("c043c")){
            btclassDescription = "Unknown";
        } else if ( getBluetoothClass.equals("c243c")){
            btclassDescription = "Unknown</b>";
        } else if ( getBluetoothClass.equals("8043c")){
            btclassDescription = "<b>Samsung TV</b>";
        } else if ( getBluetoothClass.equals("60680")){
            btclassDescription = "Unknown";
        } else if ( getBluetoothClass.equals("200408")){
            btclassDescription = "<b>Car device</b>";
        } else if ( getBluetoothClass.equals("240414")){
            btclassDescription = "<b>Speaker</b>";
        } else if ( getBluetoothClass.equals("5a020c")){
            btclassDescription = "<b>Cell phone</b>";
        } else if ( getBluetoothClass.equals("360408")){
            btclassDescription = "Unknown";
        }

        String btDeviceClassDescription = getDeviceClass+"";
        //https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device
        //524
        //1032
        //1044
        //1084
        //1292
        //1664
        //1794
        //7936
        if ( getDeviceClass==524){
            btDeviceClassDescription = "<b>Smart phone</b>";
        } else if ( getDeviceClass ==1032){
            btDeviceClassDescription = "<b>Handsfree</b>";
        } else if ( getDeviceClass==1044){
            btDeviceClassDescription = "<b>Loudspeaker</b>";
        } else if ( getDeviceClass==1084){
            btDeviceClassDescription = "<b>Video and loudspeaker</b>";
        } else if ( getDeviceClass==1292){
            btDeviceClassDescription = "Unknown";
        } else if ( getDeviceClass==1664){
            btDeviceClassDescription = "Unknown";
        } else if ( getDeviceClass==1794){
            btDeviceClassDescription = "Unknown";
        } else if ( getDeviceClass==7936){
            btDeviceClassDescription = "Uncategorized";
        } else {
            btDeviceClassDescription = "Unknown";
        }
        //Log.i(this.toString(), "getSummarySimple getDeviceClass="+device.getBluetoothClass().getDeviceClass()+" btDeviceClassDescription="+btDeviceClassDescription);

        String btMajorDeviceClassDescription = getMajorDeviceClass+"";
        //512
        //1024
        //1280
        //1536
        //1792
        //7936
        if ( getMajorDeviceClass==256){
            btMajorDeviceClassDescription = "<b>Computer</b>";
        } else if ( getMajorDeviceClass==512){
            btMajorDeviceClassDescription = "<b>Phone</b>";
        } else if ( getMajorDeviceClass==1024){
            btMajorDeviceClassDescription = "<b>Audio video</b>";
        } else if ( getMajorDeviceClass==1280){
            btMajorDeviceClassDescription = "<b>Peripheral</b>";
        } else if ( getMajorDeviceClass==1536){
            btMajorDeviceClassDescription = "<b>Imaging</b>";
        } else if ( getMajorDeviceClass==1792){
            btMajorDeviceClassDescription = "<b>Wearable</b>";
        } else if ( getMajorDeviceClass==7936){
            btMajorDeviceClassDescription = "Uncategorized";
        }
        //Log.i(this.toString(), "getSummarySimple getMajorDeviceClass="+device.getBluetoothClass().getMajorDeviceClass()+" btMajorDeviceClassDescription="+btMajorDeviceClassDescription);

        String btBondDescription = getBondState+"";
        //10
        //12
        if ( getBondState==10){
            btBondDescription = "No bond";
        } else if ( getBondState==11){
            btBondDescription = "Bonding";
        } else if ( getBondState==12){
            btBondDescription = "<b>Bonded</b>";
        }

        return btDeviceName+" ( Bluetooth standard: " +bttypeDescription+", Major device class: "+btMajorDeviceClassDescription+", device class: "+btDeviceClassDescription+", class of device: "+btclassDescription+", MAC "+MAC+"), Bond state: "+btBondDescription;
    }
}
