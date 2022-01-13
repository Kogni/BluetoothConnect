package com.example.bluetoothconnect.model;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.math.BigInteger;

public class Object_Device {

    private Dato lastSeen_dato;

    public String getName;
    public int getType;
    public String getBluetoothClass;
    public int getDeviceClass;
    public int getMajorDeviceClass;
    public String MAC;
    public int getBondState;

    private String getName_GUI = "";
    private String majorDeviceClass_GUI = "";
    private String deviceClass_GUI = "";
    private String bluetoothClass_GUI = "";
    private String type_GUI = "";
    private String bondState_GUI = "";

    public Object_Device(BluetoothDevice device1, Dato dato) {
        //Log.i(this.toString(), "Object_Device lager device fra BluetoothDevice");
        //device = device1;
        //(currentTime.getYear()+1900)+ "." + (currentTime.getMonth()+1)+"."+currentTime.getDay()+" "+currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds();
        lastSeen_dato = dato;
        //LastSeen = (currentTime.getYear()+1900)+ "." + (currentTime.getMonth()+1)+"."+currentTime.getDay()+" "+currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds();;
        device1.getName();
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

        String[] separated = linje.split("\\|");
        int x = 0;
        for (String item : separated) {
            x++;
//            Log.i(this.toString(), "Object_Device x="+x+" separated[0]="+separated[0]);
//            Log.i(this.toString(), "Object_Device x="+x+" separated[8]="+separated[8]);
            //Log.i(this.toString(), "Object_Device x="+x+" separated[9]="+separated[9]);
            //Log.i(this.toString(), "Object_Device x="+x+" separated[10]="+separated[10]);
            if( (x-1) > (separated.length-1)){
                break;
            }
            item = separated[x-1];
            //Log.i(this.toString(), "Object_Device item="+item+" x="+x+" separated[2]="+separated[2]+" separated[3]="+separated[3]+" separated[4]="+separated[4]+" separated[5]="+separated[5]+" separated[6]="+separated[6]);
            if (x == 1) {
                //tidspunkt

                lastSeen_dato = new Dato();

                String[] separated2 = item.split("\\.");
                int x2 = 0;
                for (String item2 : separated2) {

                    if (x2 == 0) {

                        int aar = Integer.parseInt(item2);
                        if (aar < 2021) {
                            aar = aar + 1900;
                        }
                        lastSeen_dato.setYear(aar);

                    } else if (x2 == 1) {
                        lastSeen_dato.setMonth(Integer.parseInt(item2));
                    } else {
                        int mellomrom = item2.indexOf(" ");
                        if (mellomrom > -1) {
                            //Log.i(this.toString(), "Object_Device mellomrom=" + mellomrom + " item2=" + item2 + " item=" + item);
                            String dag = item2.substring(0, mellomrom);
                            lastSeen_dato.setDayOfMonth(Integer.parseInt(dag));
                        }
                    }
                    x2++;
                }
                String[] separated3 = item.split(":");
                int x3 = 0;
                for (String item3 : separated3) {
                    if (x3 == 1) {
                        lastSeen_dato.setMinute(Integer.parseInt(item3));
                    } else if (x3 == 2) {
                        //Log.i(this.toString(), "Object_Device item3=" + item3 + " item=" + item);
                        int punktum = item3.indexOf(".");
                        if (punktum > -1) {
                            String foerpunktum = item3.substring(0, punktum);
                            lastSeen_dato.setSecond(Integer.parseInt(foerpunktum));
                        } else {
                            lastSeen_dato.setSecond(Integer.parseInt(item3));
                        }
                    } else {
                        int mellomrom = item3.indexOf(" ");
                        String ettermellomrom = item3.substring(mellomrom + 1);
                        lastSeen_dato.setHour(Integer.parseInt(ettermellomrom));
                    }
                    x3++;
                }

            } else if (x == 2) {//navn
                getName = item;
            } else {
                if (x == 3){ //type
                    //Log.i(this.toString(), "Object_Device item="+item+" x="+x+" item.getClass().getSimpleName()="+item.getClass().getSimpleName());
                    try {
                        getType = Integer.parseInt(item);
                    } catch(NumberFormatException e){
                        Log.i(this.toString(), "Object_Device EXCEPTION: linje="+linje);
                        Log.i(this.toString(), "Object_Device EXCEPTION: item="+item+" x="+x+" separated[2]="+separated[2]+" separated[3]="+separated[3]+" separated[4]="+separated[4]+" separated[5]="+separated[5]+" separated[6]="+separated[6]);
                        getType = Integer.parseInt(separated[3]);
                        getName = separated[x]+" "+separated[3];
                        separated[2] = separated[2]+" "+separated[3];
                        separated[3] = separated[4];
                        separated[4] = separated[5];
                        separated[5] = separated[6];
                        separated[6] = separated[7];
                        separated[7] = separated[8];
                        separated[8] = separated[9];
                        //separated[9] = separated[10];
                        Log.i(this.toString(), "Object_Device B: item="+item+" x="+x+" separated[2]="+separated[2]+" separated[3]="+separated[3]+" separated[4]="+separated[4]+" separated[5]="+separated[5]);

                        item = separated[3];
                        x++;
                        Log.i(this.toString(), "Object_Device etter fix: item="+item+" separated[2]="+separated[2]+" x="+x+" separated[3]="+separated[3]+" separated[4]="+separated[4]+" separated[5]="+separated[5]);

                    }
                }

                try {
                    if (x == 3) {
                        getType = Integer.parseInt(item);
                    } else if (x == 4) {
                        getBluetoothClass = item;
                    } else if (x == 5) {
                        getDeviceClass = Integer.parseInt(item);
                    } else if (x == 6) {
                        getMajorDeviceClass = Integer.parseInt(item);
                    } else if (x == 7) {
                        MAC = item;
                    } else if (x == 8) {
                        getBondState = Integer.parseInt(item);
                    } else if (x == 9) {

                    } else {
                        Log.i(this.toString(), "Object_Device item="+item+" x="+x);
                    }
                } catch(NumberFormatException e){
                    Log.i(this.toString(), "Object_Device EXCEPTION feilet igjen");
                    Log.i(this.toString(), "Object_Device item="+item+" x="+x+" separated[2]="+separated[2]+" separated[3]="+separated[3]+" separated[4]="+separated[4]+" separated[5]="+separated[5]);
                    e.printStackTrace();

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (item.contains(":")) {
                MAC = item;
            }
        }
        makeSummarySimple();
    }


    public String getSummary_raw() {
        //Log.i(this.toString(), "getSummary_raw device="+device);
        //Log.i(this.toString(), "getSummary_raw getName="+getName);
        //return getName+"|"+device.getType()+"|"+device.getBluetoothClass()+"|"+device.getBluetoothClass().getDeviceClass()+"|"+device.getBluetoothClass().getMajorDeviceClass()+"|"+device+"|"+device.getBondState();
        return getName + "|" + getType + "|" + getBluetoothClass + "|" + getDeviceClass + "|" + getMajorDeviceClass + "|" + MAC + "|" + getBondState;
    }

    private void makeSummarySimple() {

        getName_GUI = getName;
        if (getName_GUI == null) {
            getName_GUI = " " + null;
        } else if (getName_GUI.equals("null")) {
            getName_GUI = " " + getName_GUI;
        } else {
            getName_GUI = "<b>" + getName_GUI + "</b>";
        }

        type_GUI = getType + "";
        /*
            public static final int DEVICE_TYPE_CLASSIC = 1;
            public static final int DEVICE_TYPE_DUAL = 3;
            public static final int DEVICE_TYPE_LE = 2;
            public static final int DEVICE_TYPE_UNKNOWN = 0;
         */
        if (getType == 0) {
            type_GUI = "Unknown";
        } else if (getType == 1) {
            type_GUI = "<b>BR/EDR</b>";
        } else if (getType == 2) {
            type_GUI = "<b>LE-only</b>";
        } else if (getType == 3) {
            type_GUI = "<b>BR/EDR/LE</b>";
        }


        String majorDeviceClass_hex = getMajorDeviceClass + "";
        StringBuilder majorDeviceClass_binary = new StringBuilder(hexToBin(majorDeviceClass_hex));
        while (majorDeviceClass_binary.length() < 23) {
            majorDeviceClass_binary.insert(0, "0");
        }
        //Log.i(this.toString(), "getSummarySimple majorDeviceClass_hex="+majorDeviceClass_hex+" majorDeviceClass_binary="+majorDeviceClass_binary+" length="+bluetoothClass_binary.length());
        majorDeviceClass_binary.substring((majorDeviceClass_binary.length() - (6 + 6)), (majorDeviceClass_binary.length() - 7));
        majorDeviceClass_binary.substring((majorDeviceClass_binary.length() - (6 + 2)), (majorDeviceClass_binary.length() - 2));
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
        if (getMajorDeviceClass == 256) {
            majorDeviceClass_GUI = "<b>Computer</b>";
        } else if (getMajorDeviceClass == 512) {
            majorDeviceClass_GUI = "<b>Phone</b>";
        } else if (getMajorDeviceClass == 768) {
            majorDeviceClass_GUI = "<b>LAN/Network Access Point</b>";
        } else if (getMajorDeviceClass == 1024) {
            majorDeviceClass_GUI = "<b>Audio and video</b>";
        } else if (getMajorDeviceClass == 1280) {
            majorDeviceClass_GUI = "<b>Peripheral</b>";
        } else if (getMajorDeviceClass == 1536) {
            majorDeviceClass_GUI = "<b>Imaging</b>";
        } else if (getMajorDeviceClass == 1792) {
            majorDeviceClass_GUI = "<b>Wearable</b>";
        } else if (getMajorDeviceClass == 2048) {
            majorDeviceClass_GUI = "<b>Toy</b>";
        } else if (getMajorDeviceClass == 2303) {
            majorDeviceClass_GUI = "<b>Health</b>";
        } else if (getMajorDeviceClass == 3584) {
            majorDeviceClass_GUI = "<b>Misc</b>";
        } else if (getMajorDeviceClass == 7936) {
            majorDeviceClass_GUI = "Uncategorized";
        }
        //Log.i(this.toString(), "getSummarySimple getMajorDeviceClass="+device.getBluetoothClass().getMajorDeviceClass()+" majorDeviceClass="+majorDeviceClass);


        deviceClass_GUI = getDeviceClass + "";
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
        if (getDeviceClass == 260) {
            deviceClass_GUI = "<b>Desktop computer</b>";
        } else if (getDeviceClass == 268) {
            deviceClass_GUI = "<b>Laptop</b>";
        } else if (getDeviceClass == 516) {
            deviceClass_GUI = "<b>Feature phone</b>";
        } else if (getDeviceClass == 524) {
            deviceClass_GUI = "<b>Smart phone</b>";
        } else if (getDeviceClass == 1028) {
            deviceClass_GUI = "<b>Wearable headset/b>";
        } else if (getDeviceClass == 1032) {
            deviceClass_GUI = "<b>Handsfree</b>";
        } else if (getDeviceClass == 1044) {
            deviceClass_GUI = "<b>Loudspeaker</b>";
        } else if (getDeviceClass == 1060) {
            deviceClass_GUI = "<b>Set-top box</b>";
        } else if (getDeviceClass == 1084) {
            deviceClass_GUI = "<b>Video and loudspeaker</b>";
        } else if (getDeviceClass == 1292) {
            deviceClass_GUI = "Unknown";
        } else if (getDeviceClass == 1344) {
            deviceClass_GUI = "Unknown";
        } else if (getDeviceClass == 1664) {
            deviceClass_GUI = "Unknown";
        } else if (getDeviceClass == 1794) {
            deviceClass_GUI = "Unknown";
        } else if (getDeviceClass == 1796) {
            deviceClass_GUI = "<b>Wrist watch</b>";
        } else if (getDeviceClass == 3584) {
            deviceClass_GUI = "Unknown";
        } else if (getDeviceClass == 7936) {
            deviceClass_GUI = "Uncategorized";
        } else {
            deviceClass_GUI = "Unknown";
        }
        //Log.i(this.toString(), "getSummarySimple getDeviceClass="+device.getBluetoothClass().getDeviceClass()+" deviceClass="+deviceClass);


        String bluetoothClass_hex = getBluetoothClass + "";
        StringBuilder bluetoothClass_binary = new StringBuilder(hexToBin(bluetoothClass_hex));
        while (bluetoothClass_binary.length() < 23) {
            bluetoothClass_binary.insert(0, "0");
        }
        //Log.i(this.toString(), "getSummarySimple bluetoothClass_hex="+bluetoothClass_hex+" bluetoothClass_binary="+bluetoothClass_binary+" length="+bluetoothClass_binary.length());
        String bin_12_8 = bluetoothClass_binary.substring((bluetoothClass_binary.length() - (6 + 6)), (bluetoothClass_binary.length() - 7));
        String bin_7_6 = bluetoothClass_binary.substring((bluetoothClass_binary.length() - (6 + 2)), (bluetoothClass_binary.length() - 6));
        String bin_7_2 = bluetoothClass_binary.substring((bluetoothClass_binary.length() - (6 + 2)), (bluetoothClass_binary.length() - 2));
        String bin_5_2 = bluetoothClass_binary.substring((bluetoothClass_binary.length() - (4 + 2)), (bluetoothClass_binary.length() - 2));
        int fix = 1;
        int dig1 = 22;
        String[] bin1 = new String[bluetoothClass_binary.length() + 1];
        while (fix < 23) {
            bin1[dig1] = bluetoothClass_binary.substring(fix - 1, fix);
            //bluetoothClass_binary.toString();//Log.i(this.toString(), "getSummarySimple bluetoothClass_binary=" + bluetoothClass_binary + " fix=" + fix + " dig1="+dig1+" bin1[dig1]=" + bin1[dig1]);
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
        switch (bin_12_8) {
            case "00000":
                bluetoothClass_GUI = "<b>Misc</b>, ";
                break;
            case "00010":
                //bluetoothClass_GUI = "<b>Computer</b>, ";
                switch (bin_7_2) {
                    case "00000":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Uncategorized</b>, ";
                        break;
                    case "00001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Desktop</b>, ";
                        break;
                    case "00010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Server</b>, ";
                        break;
                    case "00011":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>laptop</b>, ";
                        break;
                    case "00100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Handheld clam shell</b>, ";
                        break;
                    case "00101":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Wearable computer</b>, ";
                        break;
                    default:
                        bluetoothClass_GUI = bluetoothClass_GUI + "?? (bin_7_2=" + bin_7_2 + "), ";
                        break;
                }
                break;
            case "00100":
                //bluetoothClass_GUI = "<b>Phone</b>, ";
                switch (bin_7_2) {
                    case "000000":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Uncategorized</b>, ";
                        break;
                    case "000001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Cellular</b>, ";
                        break;
                    case "000010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Cordless</b>, ";
                        break;
                    case "000011":
                        //bluetoothClass_description = bluetoothClass_description+"<b>Smart phone</b>, ";//treffer denne, for pappas(?) laptop
                        break;
                    case "000100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Wired modem or voice gateway</b>, ";
                        break;
                    case "000101":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Common ISDN Access</b>, ";
                        break;
                    default:
                        bluetoothClass_GUI = bluetoothClass_GUI + "?? (bin_7_2=" + bin_7_2 + "), ";
                        break;
                }
                break;
            case "00110":
                //bluetoothClass_GUI = "<b>Network access point</b>, ";
                break;
            case "01000":
                //bluetoothClass_GUI = "<b>Audio/video</b>, ";
                switch (bin_7_2) {
                    case "000000":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Uncategorized</b>, ";
                        break;
                    case "000001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Wearable Headset Device</b>, ";
                        break;
                    case "000010":
                        //bluetoothClass_description = bluetoothClass_description+"<b>Hands-free Device</b>, ";
                        break;
                    case "000011":
                        bluetoothClass_GUI = bluetoothClass_GUI + "Unknown reserved (" + bin_7_2 + "), ";
                        break;
                    case "000100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Microphone</b>, ";
                        break;
                    case "000101":
                        //bluetoothClass_description = bluetoothClass_description+"<b>Loudspeaker</b>, ";
                        break;
                    case "000110":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Headphones</b>, ";
                        break;
                    case "000111":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Portable Audio</b>, ";
                        break;
                    case "001000":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Car audio</b>, ";
                        break;
                    case "001001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Set-top box</b>, ";
                        break;
                    case "001010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>HiFI Audio Device</b>, ";
                        break;
                    case "001011":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>VCR</b>, ";
                        break;
                    case "001100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Video Camera</b>, ";
                        break;
                    case "001101":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Camcorder</b>, ";
                        break;
                    case "001110":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Video Monitor</b>, ";
                        break;
                    case "001111":
                        //bluetoothClass_GUI = bluetoothClass_description+"<b>Video Display and Loudspeaker</b>, ";
                        break;
                    case "010000":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Video Conferencing</b>, ";
                        break;
                    case "010001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "??" + bin_7_2 + "B, ";
                        break;
                    case "010010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Gaming/Toy</b>, ";
                        break;
                    default:
                        bluetoothClass_GUI = bluetoothClass_GUI + "?? (bin_7_2=" + bin_7_2 + ") C, "; //treffer her med en huawei telefon

                        break;
                }
                break;
            case "01010":
                //bluetoothClass_GUI = "<b>Peripheral</b>, ";
                switch (bin_7_6) {
                    case "00":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Not keyboard or pointing device</b>, ";
                        break;
                    case "01":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Keyboard</b>, ";
                        break;
                    case "10":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Pointing device</b>, ";
                        break;
                    case "11":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Combined keyboard and pointing device</b>, ";
                        break;
                }
                switch (bin_5_2) {
                    case "0000":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Uncategorized</b>, ";
                        break;
                    case "0001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Joystick</b>, ";
                        break;
                    case "0010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Gamepad</b>, ";
                        break;
                    case "0011":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Remote control</b>, ";
                        break;
                    case "0100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Sensing device</b>, ";
                        break;
                    case "0101":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Digitizer tablet</b>, ";
                        break;
                    case "0110":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Card reader</b>, ";
                        break;
                    default:
                        bluetoothClass_GUI = bluetoothClass_GUI + "?? (bin_7_2=" + bin_5_2 + "), ";
                        break;
                }
                break;
            case "01100":
                //bluetoothClass_GUI = "<b>Imaging</b>, ";
                if (bin1[4].equals("1")) {
                    bluetoothClass_GUI = bluetoothClass_GUI + "<b>Display</b>, ";
                }
                if (bin1[5].equals("1")) {
                    bluetoothClass_GUI = bluetoothClass_GUI + "<b>Camera</b>, ";
                }
                if (bin1[6].equals("1")) {
                    bluetoothClass_GUI = bluetoothClass_GUI + "<b>Scanner</b>, ";
                }
                if (bin1[7].equals("1")) {
                    bluetoothClass_GUI = bluetoothClass_GUI + "<b>Printer</b>, ";
                }
                break;
            case "01110":
                //bluetoothClass_GUI = "<b>Wearable</b>, ";
                switch (bin_7_2) {
                    case "000001":
                        //bluetoothClass_GUI = bluetoothClass_description+"<b>Wrist watch</b>, ";
                        break;
                    case "000010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Pager</b>, ";
                        break;
                    case "000011":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Jacket</b>, ";
                        break;
                    case "000100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Helmet</b>, ";
                        break;
                    case "000101":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Glasses</b>, ";
                        break;
                    default:
                        bluetoothClass_GUI = bluetoothClass_GUI + "?? (" + bin_7_2 + "), ";
                        break;
                }
                break;
            case "10000":  //mye virker feil innenfor her. Treffer med bilkamera og TV
                //bluetoothClass_GUI = "<b>Toy</b>, ";
                switch (bin_7_2) {
                    case "000001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Robot</b>, ";
                        break;
                    case "000010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Vehicle</b>, ";
                        break;
                    case "000011":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Doll/action figure</b>, ";
                        break;
                    case "000100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Controller</b>, ";
                        break;
                    case "000101":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Game</b>, ";
                        break;
                    default:
                        bluetoothClass_GUI = bluetoothClass_GUI + "?? (" + bin_7_2 + "), ";
                        break;
                }
                break;
            case "10010":
                //bluetoothClass_GUI = "<b>Health</b>, ";
                switch (bin_7_2) {
                    case "000000":
                        bluetoothClass_GUI = bluetoothClass_GUI + "Undefined, ";
                        break;
                    case "000001":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Blood Pressure Monitor</b>, ";
                        break;
                    case "000010":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Thermometer</b>, ";
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Weighing Scale</b>, ";
                        break;
                    case "000011":
                        break;
                    case "000100":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Glucose Meter</b>, ";
                        break;
                    case "000101":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Pulse Oximeter</b>, ";
                        break;
                    case "000110":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Heart/Pulse Rate Monitor</b>, ";
                        break;
                    case "000111":
                        bluetoothClass_GUI = bluetoothClass_GUI + "<b>Heart Data Display</b>, ";
                        break;
                }
                break;
            case "11111":
                bluetoothClass_GUI = "Unknown, ";
                break;
            default:
                bluetoothClass_GUI = "??, ";
                break;
        }


        //https://www.ampedrftech.com/cod.htm
        switch (getBluetoothClass) {
            case "43c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Video display and loudspeaker)</b>";
                break;
            case "50c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Remote control)</b>";
                break;
            case "1f00":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Unknown, no data)</b>";
                break;
            case "c043c":
            case "c243c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Rendering capturing with video display and loudspeaker)</b>";
                break;
            case "8043c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Capturing with video display and loudspeaker)</b>";
                break;
            case "60680":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Imagaing, rendering networking, printer)</b>";
                break;
            case "200408":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio, Hands-free)</b>";
                break;
            case "240414":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Rendering audio, loudspeaker)</b>";
                break;
            case "5a020c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Cell phone, networking capturing object transfer telephony, smart phone)</b>";
                break;
            case "360408":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio, networking rendering object transfer audio, hands-free)</b>";
                break;
        }
        //Log.i(this.toString(), "getSummarySimple bluetoothClass_hex="+bluetoothClass_hex+" bluetoothClass_binary="+bluetoothClass_binary +" bluetoothClass_description="+bluetoothClass_description);

        bondState_GUI = getBondState + "";
        //10
        //12
        if (getBondState == 10) {
            bondState_GUI = "No bond";
        } else if (getBondState == 11) {
            bondState_GUI = "Bonding";
        } else if (getBondState == 12) {
            bondState_GUI = "<b>Bonded</b>";
        }

    }

    public String getSummarySimple() {
        return getLastSeen() + " " + getName_GUI + ": " + majorDeviceClass_GUI + ", " + deviceClass_GUI + ", " + bluetoothClass_GUI + ", bluetooth: " + type_GUI + ". MAC " + MAC + ". " + bondState_GUI;
    }

    public void setFound(Dato dato) {
        lastSeen_dato = dato;
    }

/*    public void setFound(Date nyDato) {
        nyDato.setYear(calendar.get(Calendar.YEAR));
    }*/

    public String getLastSeen() {

        return getLastSeen_Year() + "." + getLastSeen_Month() + "." + getLastSeen_Day() + " " + getLastSeen_Hour() + ":" + getLastSeen_Minute() + ":" + getLastSeen_Second();
    }

    private int getLastSeen_Second() {
        return lastSeen_dato.getSecond();
    }

    private int getLastSeen_Minute() {
        return lastSeen_dato.getMinute();
    }

    private int getLastSeen_Hour() {
        return lastSeen_dato.getHour();
    }

    static String hexToBin(String s) {
        return new BigInteger(s, 16).toString(2);
    }

    public String getMAC() {
        return MAC;
    }

    public int getLastSeen_Year() {
        return lastSeen_dato.getYear();
    }

    public int getLastSeen_Month() {
        return lastSeen_dato.getMonth();
    }

    public int getLastSeen_Day() {
        return lastSeen_dato.getDayOfMonth();
    }

    public long getSecondsPast(Dato dato) {
        int millisekund_lastseen = (lastSeen_dato.getYear() * 12 * 31 * 24 * 60 * 60) + (lastSeen_dato.getMonth() * 31 * 24 * 60 * 60) + (lastSeen_dato.getDayOfMonth() * 24 * 60 * 60) + (lastSeen_dato.getHour() * 60 * 60) + (lastSeen_dato.getMinute() * 60) + lastSeen_dato.getSecond();
        int millisekund_naa = (dato.getYear() * 12 * 31 * 24 * 60 * 60) + (dato.getMonth() * 31 * 24 * 60 * 60) + (dato.getDayOfMonth() * 24 * 60 * 60) + (dato.getHour() * 60 * 60) + (dato.getMinute() * 60) + dato.getSecond();
        return millisekund_naa - millisekund_lastseen;
    }
}
