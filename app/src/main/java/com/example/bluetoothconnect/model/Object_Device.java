package com.example.bluetoothconnect.model;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.math.BigInteger;
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
    private int binaryClassLeft = 0;

    private String getName_GUI="";
    private String majorDeviceClass_GUI="";
    private String deviceClass_GUI="";
    private String bluetoothClass_GUI="";
    private String type_GUI="";
    private String bondState_GUI="";

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
        makeSummarySimple();
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
        makeSummarySimple();
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

    private void makeSummarySimple(){

        getName_GUI = getName;
        if ( getName_GUI==null){
            getName_GUI = " "+getName_GUI;
        } else if ( getName_GUI.equals("null")){
            getName_GUI = " "+getName_GUI;
        } else {
            getName_GUI = "<b>"+getName_GUI+"</b>";
        }

        type_GUI = getType+"";
        /*
            public static final int DEVICE_TYPE_CLASSIC = 1;
            public static final int DEVICE_TYPE_DUAL = 3;
            public static final int DEVICE_TYPE_LE = 2;
            public static final int DEVICE_TYPE_UNKNOWN = 0;
         */
        if ( getType == 0){
            type_GUI = "Unknown";
        } else if ( getType == 1){
            type_GUI = "<b>BR/EDR</b>";
        } else if ( getType == 2){
            type_GUI = "<b>LE-only</b>";
        } else if ( getType == 3){
            type_GUI = "<b>BR/EDR/LE</b>";
        }


        String majorDeviceClass_hex = getMajorDeviceClass+"";
        String majorDeviceClass_binary= hexToBin(majorDeviceClass_hex);
        while ( majorDeviceClass_binary.length()<23){
            majorDeviceClass_binary = "0"+majorDeviceClass_binary;
        }
        //Log.i(this.toString(), "getSummarySimple majorDeviceClass_hex="+majorDeviceClass_hex+" majorDeviceClass_binary="+majorDeviceClass_binary+" length="+bluetoothClass_binary.length());
        String MDC_bin_12_8 = majorDeviceClass_binary.substring((majorDeviceClass_binary.length()-(6+6) ) ,(majorDeviceClass_binary.length()-7));
        String MDC_bin_7_2 = majorDeviceClass_binary.substring( (majorDeviceClass_binary.length()-(6+2) ) ,(majorDeviceClass_binary.length()-2));
        //Log.i(this.toString(), "getSummarySimple majorDeviceClass_hex="+majorDeviceClass_hex+" majorDeviceClass_binary="+majorDeviceClass_binary +" MDC_bin_12_8="+MDC_bin_12_8+" MDC_bin_7_2="+MDC_bin_7_2);
        //https://docs.microsoft.com/en-us/windows/security/identity-protection/hello-for-business/hello-feature-dynamic-lock

        //256 - 16 devices
        //512 - 34 devices
        //1024 - 92
        //1280 - 2
        //1536
        //1792 - 2
        //3584 - 2
        //7936 - 3365 devices
        if ( getMajorDeviceClass==256){
            majorDeviceClass_GUI = "<b>Computer</b>";
        } else if ( getMajorDeviceClass==512){
            majorDeviceClass_GUI = "<b>Phone</b>";
        } else if ( getMajorDeviceClass==768){
            majorDeviceClass_GUI = "<b>LAN/Network Access Point</b>";
        } else if ( getMajorDeviceClass==1024){
            majorDeviceClass_GUI = "<b>Audio and video</b>";
        } else if ( getMajorDeviceClass==1280){
            majorDeviceClass_GUI = "<b>Peripheral</b>";
        } else if ( getMajorDeviceClass==1536){
            majorDeviceClass_GUI = "<b>Imaging</b>";
        } else if ( getMajorDeviceClass==1792){
            majorDeviceClass_GUI = "<b>Wearable</b>";
        } else if ( getMajorDeviceClass==2048){
            majorDeviceClass_GUI = "<b>Toy</b>";
        } else if ( getMajorDeviceClass==2303){
            majorDeviceClass_GUI = "<b>Health</b>";
        } else if ( getMajorDeviceClass==3584){
            majorDeviceClass_GUI = "<b>Misc</b>";
        } else if ( getMajorDeviceClass==7936){
            majorDeviceClass_GUI = "Uncategorized";
        }
        //Log.i(this.toString(), "getSummarySimple getMajorDeviceClass="+device.getBluetoothClass().getMajorDeviceClass()+" majorDeviceClass="+majorDeviceClass);


        deviceClass_GUI = getDeviceClass+"";
        //https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device
        //https://docwiki.embarcadero.com/Libraries/Sydney/en/System.Bluetooth.TBluetoothDevice.ClassDevice
        //260 - 3
        //268 - 13
        //516 - 1
        //524 - 33 devices
        //1028 - 1
        //1032 - 2
        //1044 - 1
        //1060 - 4
        //1084 - 84 devices
        //1292 - 1
        //1344 - 1
        //1664
        //1794 - 2
        //3584 - 2
        //7936 - 3365 devices
        if ( getDeviceClass==260){
            deviceClass_GUI = "<b>Desktop computer</b>";
        } else if ( getDeviceClass==268){
            deviceClass_GUI = "<b>Laptop</b>";
        } else if ( getDeviceClass==516){
            deviceClass_GUI = "<b>Feature phone</b>";
        } else if ( getDeviceClass==524){
            deviceClass_GUI = "<b>Smart phone</b>";
        } else if ( getDeviceClass==1028){
            deviceClass_GUI = "<b>Wearable headset/b>";
        } else if ( getDeviceClass ==1032){
            deviceClass_GUI = "<b>Handsfree</b>";
        } else if ( getDeviceClass==1044){
            deviceClass_GUI = "<b>Loudspeaker</b>";
        } else if ( getDeviceClass==1060){
            deviceClass_GUI = "<b>Set-top box</b>";
        } else if ( getDeviceClass==1084){
            deviceClass_GUI = "<b>Video and loudspeaker</b>";
        } else if ( getDeviceClass==1292){
            deviceClass_GUI = "Unknown";
        } else if ( getDeviceClass==1344){
            deviceClass_GUI = "Unknown";
        } else if ( getDeviceClass==1664){
            deviceClass_GUI = "Unknown";
        } else if ( getDeviceClass==1794){
            deviceClass_GUI = "Unknown";
        } else if ( getDeviceClass==1796){
            deviceClass_GUI = "<b>Wrist watch</b>";
        } else if ( getDeviceClass==3584){
            deviceClass_GUI = "Unknown";
        } else if ( getDeviceClass==7936){
            deviceClass_GUI = "Uncategorized";
        } else {
            deviceClass_GUI = "Unknown";
        }
        //Log.i(this.toString(), "getSummarySimple getDeviceClass="+device.getBluetoothClass().getDeviceClass()+" deviceClass="+deviceClass);


        String bluetoothClass_hex = getBluetoothClass+"";
        String bluetoothClass_binary= hexToBin(bluetoothClass_hex);
        while ( bluetoothClass_binary.length()<23){
            bluetoothClass_binary = "0"+bluetoothClass_binary;
        }
        //Log.i(this.toString(), "getSummarySimple bluetoothClass_hex="+bluetoothClass_hex+" bluetoothClass_binary="+bluetoothClass_binary+" length="+bluetoothClass_binary.length());
        String bin_12_8 = bluetoothClass_binary.substring((bluetoothClass_binary.length()-(6+6) ) ,(bluetoothClass_binary.length()-7));
        String bin_7_6 = bluetoothClass_binary.substring( (bluetoothClass_binary.length()-(6+2) ) ,(bluetoothClass_binary.length()-6));
        String bin_7_2 = bluetoothClass_binary.substring( (bluetoothClass_binary.length()-(6+2) ) ,(bluetoothClass_binary.length()-2));
        String bin_5_2 = bluetoothClass_binary.substring( (bluetoothClass_binary.length()-(4+2) ) ,(bluetoothClass_binary.length()-2));
        int fix = 1;
        int dig1 = 22;
        String[] bin1 = new String[bluetoothClass_binary.length()+1];
        while ( fix < 23){
            bin1[dig1] = bluetoothClass_binary.substring(fix-1,fix);
            if ( !bluetoothClass_binary.equals("00000000001111100000000")) {
                //Log.i(this.toString(), "getSummarySimple bluetoothClass_binary=" + bluetoothClass_binary + " fix=" + fix + " dig1="+dig1+" bin1[dig1]=" + bin1[dig1]);
            }
            fix++;
            dig1--;
        }
        //Log.i(this.toString(), "getSummarySimple bluetoothClass_hex="+bluetoothClass_hex+" bluetoothClass_binary="+bluetoothClass_binary +" bin_12_8="+bin_12_8+" bin_7_2="+bin_7_2+" bin_7_6="+bin_7_6+" bin_5_2="+bin_5_2);
        // CoD hex
        //http://domoticx.com/bluetooth-class-of-device-lijst-cod/
        //https://www.ampedrftech.com/datasheets/cod_definition.pdf
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
        if ( bin_12_8.equals("00000")){
            bluetoothClass_GUI = "<b>Misc</b>, ";
        } else if ( bin_12_8.equals("00010")){
            //bluetoothClass_GUI = "<b>Computer</b>, ";
            if ( bin_7_2.equals("00000")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Uncategorized</b>, ";
            } else if ( bin_7_2.equals("00001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Desktop</b>, ";
            } else if ( bin_7_2.equals("00010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Server</b>, ";
            } else if ( bin_7_2.equals("00011")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>laptop</b>, ";
            } else if ( bin_7_2.equals("00100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Handheld clam shell</b>, ";
            } else if ( bin_7_2.equals("00101")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Wearable computer</b>, ";
            } else {
                bluetoothClass_GUI = bluetoothClass_GUI+"?? (bin_7_2="+bin_7_2+"), ";
            }
        } else if ( bin_12_8.equals("00100")){
            //bluetoothClass_GUI = "<b>Phone</b>, ";
            if ( bin_7_2.equals("000000")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Uncategorized</b>, ";
            } else if ( bin_7_2.equals("000001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Cellular</b>, ";
            } else if ( bin_7_2.equals("000010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Cordless</b>, ";
            } else if ( bin_7_2.equals("000011")){
                //bluetoothClass_description = bluetoothClass_description+"<b>Smart phone</b>, ";//treffer denne, for pappas(?) laptop
            } else if ( bin_7_2.equals("000100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Wired modem or voice gateway</b>, ";
            } else if ( bin_7_2.equals("000101")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Common ISDN Access</b>, ";
            } else {
                bluetoothClass_GUI = bluetoothClass_GUI+"?? (bin_7_2="+bin_7_2+"), ";
            }
        } else if ( bin_12_8.equals("00110")){
            //bluetoothClass_GUI = "<b>Network access point</b>, ";
        } else if ( bin_12_8.equals("01000")){
            //bluetoothClass_GUI = "<b>Audio/video</b>, ";
            if ( bin_7_2.equals("000000")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Uncategorized</b>, ";
            } else if ( bin_7_2.equals("000001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Wearable Headset Device</b>, ";
            } else if ( bin_7_2.equals("000010")){
                //bluetoothClass_description = bluetoothClass_description+"<b>Hands-free Device</b>, ";
            } else if ( bin_7_2.equals("000011")){
                bluetoothClass_GUI = bluetoothClass_GUI+"Unknown reserved ("+bin_7_2+"), ";
            } else if ( bin_7_2.equals("000100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Microphone</b>, ";
            } else if ( bin_7_2.equals("000101")){
                //bluetoothClass_description = bluetoothClass_description+"<b>Loudspeaker</b>, ";
            } else if ( bin_7_2.equals("000110")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Headphones</b>, ";
            } else if ( bin_7_2.equals("000111")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Portable Audio</b>, ";
            } else if ( bin_7_2.equals("001000")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Car audio</b>, ";
            } else if ( bin_7_2.equals("001001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Set-top box</b>, ";
            } else if ( bin_7_2.equals("001010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>HiFI Audio Device</b>, ";
            } else if ( bin_7_2.equals("001011")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>VCR</b>, ";
            } else if ( bin_7_2.equals("001100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Video Camera</b>, ";
            } else if ( bin_7_2.equals("001101")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Camcorder</b>, ";
            } else if ( bin_7_2.equals("001110")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Video Monitor</b>, ";
            } else if ( bin_7_2.equals("001111")){
                //bluetoothClass_GUI = bluetoothClass_description+"<b>Video Display and Loudspeaker</b>, ";
            } else if ( bin_7_2.equals("010000")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Video Conferencing</b>, ";
            } else if ( bin_7_2.equals("010001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"??"+bin_7_2+"B, ";
            } else if ( bin_7_2.equals("010010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Gaming/Toy</b>, ";
            } else {
                bluetoothClass_GUI = bluetoothClass_GUI+"?? (bin_7_2="+bin_7_2+") C, "; //treffer her med en huawei telefon
            }
        } else if ( bin_12_8.equals("01010")){
            //bluetoothClass_GUI = "<b>Peripheral</b>, ";
            if ( bin_7_6.equals("00")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Not keyboard or pointing device</b>, ";
            } else if ( bin_7_6.equals("01")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Keyboard</b>, ";
            } else if ( bin_7_6.equals("10")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Pointing device</b>, ";
            } else if ( bin_7_6.equals("11")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Combined keyboard and pointing device</b>, ";
            }
            if ( bin_5_2.equals("0000")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Uncategorized</b>, ";
            } else if ( bin_5_2.equals("0001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Joystick</b>, ";
            } else if ( bin_5_2.equals("0010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Gamepad</b>, ";
            } else if ( bin_5_2.equals("0011")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Remote control</b>, ";
            } else if ( bin_5_2.equals("0100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Sensing device</b>, ";
            } else if ( bin_5_2.equals("0101")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Digitizer tablet</b>, ";
            } else if ( bin_5_2.equals("0110")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Card reader</b>, ";
            } else {
                bluetoothClass_GUI = bluetoothClass_GUI+"?? (bin_7_2="+bin_5_2+"), ";
            }
        } else if ( bin_12_8.equals("01100")){
            //bluetoothClass_GUI = "<b>Imaging</b>, ";
            if ( bin1[4].equals("1")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Display</b>, ";
            }
            if ( bin1[5].equals("1")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Camera</b>, ";
            }
            if ( bin1[6].equals("1")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Scanner</b>, ";
            }
            if ( bin1[7].equals("1")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Printer</b>, ";
            }
        } else if ( bin_12_8.equals("01110")){
            //bluetoothClass_GUI = "<b>Wearable</b>, ";
            if ( bin_7_2.equals("000001")){
                //bluetoothClass_GUI = bluetoothClass_description+"<b>Wrist watch</b>, ";
            } else if ( bin_7_2.equals("000010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Pager</b>, ";
            } else if ( bin_7_2.equals("000011")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Jacket</b>, ";
            } else if ( bin_7_2.equals("000100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Helmet</b>, ";
            } else if ( bin_7_2.equals("000101")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Glasses</b>, ";
            } else {
                bluetoothClass_GUI = bluetoothClass_GUI+"?? ("+bin_7_2+"), ";
            }
        } else if ( bin_12_8.equals("10000")){ //mye virker feil innenfor her. Treffer med bilkamera og TV
            //bluetoothClass_GUI = "<b>Toy</b>, ";
            if ( bin_7_2.equals("000001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Robot</b>, ";
            } else if ( bin_7_2.equals("000010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Vehicle</b>, ";
            } else if ( bin_7_2.equals("000011")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Doll/action figure</b>, ";
            } else if ( bin_7_2.equals("000100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Controller</b>, ";
            } else if ( bin_7_2.equals("000101")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Game</b>, ";
            } else {
                bluetoothClass_GUI = bluetoothClass_GUI+"?? ("+bin_7_2+"), ";
            }
        } else if ( bin_12_8.equals("10010")){
            //bluetoothClass_GUI = "<b>Health</b>, ";
            if ( bin_7_2.equals("000000")){
                bluetoothClass_GUI = bluetoothClass_GUI+"Undefined, ";
            } else if ( bin_7_2.equals("000001")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Blood Pressure Monitor</b>, ";
            } else if ( bin_7_2.equals("000010")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Thermometer</b>, ";
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Weighing Scale</b>, ";
            } else if ( bin_7_2.equals("000011")){
            } else if ( bin_7_2.equals("000100")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Glucose Meter</b>, ";
            } else if ( bin_7_2.equals("000101")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Pulse Oximeter</b>, ";
            } else if ( bin_7_2.equals("000110")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Heart/Pulse Rate Monitor</b>, ";
            } else if ( bin_7_2.equals("000111")){
                bluetoothClass_GUI = bluetoothClass_GUI+"<b>Heart Data Display</b>, ";
            }
        } else if ( bin_12_8.equals("11111")){
            bluetoothClass_GUI = "Unknown, ";
        } else {
            bluetoothClass_GUI = "??, ";
        }

        for (int i=0; i<bin1.length; i++) {
            //Log.i(this.toString(), "getSummarySimple bin1["+i+"]="+bin1[i]);
            if ( i ==13){

            } else if ( i ==14){

            } else if ( i ==15){

            } else if ( i ==16){

            } else if ( i ==17){

            } else if ( i ==18){

            } else if ( i ==19){

            } else if ( i ==20){

            } else if ( i ==21){

            } else if ( i ==22){

            } else if ( i ==23){

            }
        }


        //https://www.ampedrftech.com/cod.htm
        if ( getBluetoothClass.equals("43c")){
            bluetoothClass_GUI = bluetoothClass_GUI+"<b>(Video display and loudspeaker)</b>";
        } else if ( getBluetoothClass.equals("50c")){
            bluetoothClass_GUI = bluetoothClass_GUI+"(Remote control)";
        } else if ( getBluetoothClass.equals("1f00")){
            bluetoothClass_GUI = bluetoothClass_GUI+"(Unknown, no data)";
        } else if ( getBluetoothClass.equals("c043c")){
            bluetoothClass_GUI = bluetoothClass_GUI+"(Rendering capturing with video display and loudspeaker)";
        } else if ( getBluetoothClass.equals("c243c")){
            bluetoothClass_GUI = bluetoothClass_GUI+"(Rendering capturing with video display and loudspeaker)</b>";
        } else if ( getBluetoothClass.equals("8043c")){
            bluetoothClass_GUI = bluetoothClass_GUI+"<b>(Capturing with video display and loudspeaker)</b>";
        } else if ( getBluetoothClass.equals("60680")){
            bluetoothClass_GUI = bluetoothClass_GUI+"(Imagaing, rendering networking, printer)";
        } else if ( getBluetoothClass.equals("200408")){
            bluetoothClass_GUI = bluetoothClass_GUI+"<b>(Audio, Hands-free)</b>";
        } else if ( getBluetoothClass.equals("240414")){
            bluetoothClass_GUI = bluetoothClass_GUI+"<b>(Rendering audio, loudspeaker)</b>";
        } else if ( getBluetoothClass.equals("5a020c")){
            bluetoothClass_GUI = bluetoothClass_GUI+"<b>(Cell phone, networking capturing object transfer telephony, smart phone)</b>";
        } else if ( getBluetoothClass.equals("360408")){
            bluetoothClass_GUI = bluetoothClass_GUI+"(Audio, networking rendering object transfer audio, hands-free)";
        }
        //Log.i(this.toString(), "getSummarySimple bluetoothClass_hex="+bluetoothClass_hex+" bluetoothClass_binary="+bluetoothClass_binary +" bluetoothClass_description="+bluetoothClass_description);

        bondState_GUI = getBondState+"";
        //10
        //12
        if ( getBondState==10){
            bondState_GUI = "No bond";
        } else if ( getBondState==11){
            bondState_GUI = "Bonding";
        } else if ( getBondState==12){
            bondState_GUI = "<b>Bonded</b>";
        }

    }

    public String getSummarySimple() {

        return getName_GUI+": "+ majorDeviceClass_GUI +", "+deviceClass_GUI+", "+bluetoothClass_GUI+", bluetooth: " +type_GUI+". MAC "+MAC+". "+bondState_GUI;
    }

    static String hexToBin(String s) {
        return new BigInteger(s, 16).toString(2);
    }

    public String getMAC() {
        return MAC;
    }
}
