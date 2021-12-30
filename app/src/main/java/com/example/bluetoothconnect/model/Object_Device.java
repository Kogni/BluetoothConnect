package com.example.bluetoothconnect.model;

import android.bluetooth.BluetoothDevice;

import java.util.Date;

public class Object_Device {

    BluetoothDevice device;
    Date LastSeen;

    public Object_Device(BluetoothDevice device1, Date currentTime) {
        device = device1;
        LastSeen = currentTime;
    }

    public void setFound(Date currentTime) {
        LastSeen = currentTime;
    }

    public Date getLastSeen() {
        return LastSeen;
    }

    public String getSummary_raw() {
        return device.getName()+"|"+device.getType()+"|"+device.getBluetoothClass()+"|"+device.getBluetoothClass().getDeviceClass()+"|"+device.getBluetoothClass().getMajorDeviceClass()+"|"+device+"|"+device.getBondState();
    }
    public String getSummarySimple() {

        String btDeviceName = device.getName();
        if ( btDeviceName==null){

        } else {
            btDeviceName = "<b>"+btDeviceName+"</b>";
        }

        String bttypeDescription = device.getType()+"";
        /*
            public static final int DEVICE_TYPE_CLASSIC = 1;
            public static final int DEVICE_TYPE_DUAL = 3;
            public static final int DEVICE_TYPE_LE = 2;
            public static final int DEVICE_TYPE_UNKNOWN = 0;
         */
        if ( device.getType() == 0){
            bttypeDescription = "Unknown";
        } else if ( device.getType() == 1){
            bttypeDescription = "<b>BR/EDR</b>";
        } else if ( device.getType() == 2){
            bttypeDescription = "<b>LE-only</b>";
        } else if ( device.getType() == 3){
            bttypeDescription = "<b>BR/EDR/LE</b>";
        }

        String btclassDescription = device.getBluetoothClass()+"";
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
        if ( device.getBluetoothClass().toString().equals("43c")){
            btclassDescription = "<b>TV</b>";
        } else if ( device.getBluetoothClass().toString().equals("50c")){
            btclassDescription = "Unknown";
        } else if ( device.getBluetoothClass().toString().equals("704")){
            btclassDescription = "<b>Wearable</b>";
        } else if ( device.getBluetoothClass().toString().equals("1f00")){
            btclassDescription = "Unknown, various";
        } else if ( device.getBluetoothClass().toString().equals("c043c")){
            btclassDescription = "Unknown";
        } else if ( device.getBluetoothClass().toString().equals("c243c")){
            btclassDescription = "Unknown</b>";
        } else if ( device.getBluetoothClass().toString().equals("8043c")){
            btclassDescription = "<b>Samsung TV</b>";
        } else if ( device.getBluetoothClass().toString().equals("60680")){
            btclassDescription = "Unknown";
        } else if ( device.getBluetoothClass().toString().equals("200408")){
            btclassDescription = "<b>Car device</b>";
        } else if ( device.getBluetoothClass().toString().equals("240414")){
            btclassDescription = "<b>Speaker</b>";
        } else if ( device.getBluetoothClass().toString().equals("5a020c")){
            btclassDescription = "<b>Cell phone</b>";
        } else if ( device.getBluetoothClass().toString().equals("360408")){
            btclassDescription = "Unknown";
        }

        String btDeviceClassDescription = device.getBluetoothClass().getDeviceClass()+"";
        //https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device
        //524
        //1032
        //1044
        //1084
        //1292
        //1664
        //1794
        //7936
        if ( device.getBluetoothClass().toString().equals("524")){
            btDeviceClassDescription = "<b>Smart phone</b>";
        } else if ( device.getBluetoothClass().toString().equals("1032")){
            btDeviceClassDescription = "<b>Handsfree</b>";
        } else if ( device.getBluetoothClass().toString().equals("1044")){
            btDeviceClassDescription = "<b>Loudspeaker</b>";
        } else if ( device.getBluetoothClass().toString().equals("1084")){
            btDeviceClassDescription = "<b>Video and loudspeaker</b>";
        } else if ( device.getBluetoothClass().toString().equals("1292")){
            btDeviceClassDescription = "Unknown";
        } else if ( device.getBluetoothClass().toString().equals("1664")){
            btDeviceClassDescription = "Unknown";
        } else if ( device.getBluetoothClass().toString().equals("1794")){
            btDeviceClassDescription = "Unknown";
        } else if ( device.getBluetoothClass().toString().equals("7936")){
            btDeviceClassDescription = "Uncategorized";
        } else {
            btDeviceClassDescription = "Unknown";
        }
        //Log.i(this.toString(), "getSummarySimple getDeviceClass="+device.getBluetoothClass().getDeviceClass()+" btDeviceClassDescription="+btDeviceClassDescription);

        String btMajorDeviceClassDescription = device.getBluetoothClass().getMajorDeviceClass()+"";
        //512
        //1024
        //1280
        //1536
        //1792
        //7936
        if ( device.getBluetoothClass().getMajorDeviceClass()==512){
            btMajorDeviceClassDescription = "<b>Phone</b>";
        } else if ( device.getBluetoothClass().getMajorDeviceClass()==1024){
            btMajorDeviceClassDescription = "<b>Audio video</b>";
        } else if ( device.getBluetoothClass().getMajorDeviceClass()==1280){
            btMajorDeviceClassDescription = "<b>Peripheral</b>";
        } else if ( device.getBluetoothClass().getMajorDeviceClass()==1536){
            btMajorDeviceClassDescription = "<b>Imaging</b>";
        } else if ( device.getBluetoothClass().getMajorDeviceClass()==1792){
            btMajorDeviceClassDescription = "<b>Wearable</b>";
        } else if ( device.getBluetoothClass().getMajorDeviceClass()==7936){
            btMajorDeviceClassDescription = "Uncategorized";
        }
        //Log.i(this.toString(), "getSummarySimple getMajorDeviceClass="+device.getBluetoothClass().getMajorDeviceClass()+" btMajorDeviceClassDescription="+btMajorDeviceClassDescription);

        String btBondDescription = device.getBondState()+"";
        //10
        //12
        if ( device.getBondState()==10){
            btBondDescription = "No bond";
        } else if ( device.getBondState()==11){
            btBondDescription = "Bonding";
        } else if ( device.getBondState()==12){
            btBondDescription = "<b>Bonded</b>";
        }

        return btDeviceName+" ( Bluetooth standard: " +bttypeDescription+", Major device class: "+btMajorDeviceClassDescription+", device class: "+btDeviceClassDescription+", class of device: "+btclassDescription+", MAC "+device+"), Bond state: "+btBondDescription;
    }
}
