package com.example.bluetoothconnect.model;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.math.BigInteger;
import java.util.Locale;

public class Object_Device {

    private Dato lastSeen_dato;

    private String getName;
    private int getType;
    private String getBluetoothClass;
    private int getDeviceClass;
    private int getMajorDeviceClass;
    private String MAC;
    private int getBondState;

    private String getName_GUI = "";
    private String majorDeviceClass_GUI = "";
    private String deviceClass_GUI = "";
    private String bluetoothClass_GUI = "";
    private String type_GUI = "";
    private String bondState_GUI = "";
    private String manual_producttype = "";
    private String manual_Vendor = "";

    private String deviceHighlightColor = "black";
    private String deviceHightlightText = "";

    String logtag = "Object_Device";

    public Object_Device(BluetoothDevice device1, Dato dato) {
        lastSeen_dato = dato;
        getName = device1.getName();
        getType = device1.getType();
        getBluetoothClass = device1.getBluetoothClass().toString();
        getDeviceClass = device1.getBluetoothClass().getDeviceClass();
        getMajorDeviceClass = device1.getBluetoothClass().getMajorDeviceClass();
        MAC = device1.getAddress();
        getBondState = device1.getBondState();

        if (this.MAC.equals("8C:79:F5:03:79:29")) { //TVn min
            Log.i(logtag, "Object_Device#1 fant "+deviceHightlightText+" ("+MAC+") "+lastSeen_dato.getDate());
        }
        makeSummarySimple2();
    }

    public Object_Device(String test) {
        //Log.i(this.toString(), "Object_Device lager device fra devicelogg: "+test);
        lastSeen_dato = new Dato();

        String[] separated = test.split("\\|");
        int x = 0;
        for (String item : separated) {
            x++;

            if ((x - 1) > (separated.length - 1)) {
                break;
            }
            item = separated[x - 1];
           if (x == 1) {//navn
                getName = item;
            } else {
                if (x == 2) { //type
                   try {
                        getType = Integer.parseInt(item);
                    } catch (NumberFormatException e) {
                       getType = Integer.parseInt(separated[2]);
                        getName = separated[x] + " " + separated[2];
                        separated[1] = separated[1] + " " + separated[2];
                        separated[2] = separated[3];
                        separated[3] = separated[4];
                        separated[4] = separated[5];
                        separated[5] = separated[6];
                        separated[6] = separated[7];
                        separated[7] = separated[8];
                        //separated[9] = separated[10];

                        item = separated[3];
                        x++;

                    }
                }

                try {
                    if (x == 2) {
                        getType = Integer.parseInt(item);
                    } else if (x == 3) {
                        getBluetoothClass = item;
                    } else if (x == 4) {
                        getDeviceClass = Integer.parseInt(item);
                    } else if (x == 5) {
                        getMajorDeviceClass = Integer.parseInt(item);
                    } else if (x == 6) {
                        MAC = item;
                    } else if (x == 7) {
                        getBondState = Integer.parseInt(item);
                    } else if (x == 8) {
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

                                    boolean gyldigDato = true;
                                    for (int i = 0; i < item2.length(); i++) {
                                        char c = item2.charAt(i);
                                        if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')) {
                                        } else {
                                            gyldigDato = false;
                                        }
                                    }
                                    if (gyldigDato) {
                                        String dag = item2.substring(0, mellomrom);
                                        lastSeen_dato.setDayOfMonth(Integer.parseInt(dag));
                                    } else {
                                        Log.i(this.toString(), "Object_Device#3 INVALID datas: " + test);

                                    }
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
                    } else {
                        Log.i(this.toString(), "Object_Device#3 UKJENT item=" + item + ", x=" + x+", linje="+test);
                    }
                } catch (NumberFormatException e) {
                    Log.i(this.toString(), "Object_Device#3 EXCEPTION feilet igjen");
                    Log.i(this.toString(), "Object_Device#3 item=" + item + " x=" + x + " separated[2]=" + separated[2] + " separated[3]=" + separated[3] + " separated[4]=" + separated[4] + " separated[5]=" + separated[5]);
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (item.contains(":") && (!item.contains(" "))) {
                MAC = item;
            }
        }
        if (getMAC() == null) {
            Log.i(this.toString(), "Object_Device#3 BAD MAC=" + MAC);
            Log.i(this.toString(), "Object_Device#3 input=" + test);
        }
        makeSummarySimple2();
    }


    public String getSummary_raw_withDate() {
        //Log.i(this.toString(), "getSummary_raw_withDate source="+source);
        return getName + "|" + getType + "|" + getBluetoothClass + "|" + getDeviceClass + "|" + getMajorDeviceClass + "|" + MAC + "|" + getBondState + "|" + getLastSeen();
    }

    public String getSummarySimple() {
        //Log.i(this.toString(), "getSummarySimple source="+source+", prvSource?"+prvSource);
        return getLastSeen() + "<font color='" + this.deviceHighlightColor + "'> " + getName_GUI + " - Vendor: " + manual_Vendor + ", Product: " + manual_producttype + ", Major class: " + majorDeviceClass_GUI + ", Class: " + deviceClass_GUI + ", " + bluetoothClass_GUI + ", bluetooth: " + type_GUI + ". MAC " + MAC + ". " + bondState_GUI + ""+this.deviceHightlightText+"</font>";
    }

    public void setFound(Dato dato) {
        long diff = getSecondsPast(dato);
        if ( diff>0) {
            lastSeen_dato = dato;
        }
    }

    public String getLastSeen() {
        return lastSeen_dato.getDate();
    }

    static String hexToBin(String s) {
        return new BigInteger(s, 16).toString(2);
    }

    public String getMAC() {
        if (verifyMAC(MAC)) {
            return MAC;
        } else {
            return null;
        }
    }

    public boolean verifyMAC(String sjekkMAC) {
        if (sjekkMAC.contains("2022")) {
            return false;
        } else return (sjekkMAC.contains(":")) && (sjekkMAC.length() == 17);
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
        //Log.i(this.toString(), "getSecondsPast");
        if (lastSeen_dato == null) {
            lastSeen_dato = new Dato();
        }
        int millisekund_lastseen = (lastSeen_dato.getYear() * 12 * 31 * 24 * 60 * 60) + (lastSeen_dato.getMonth() * 31 * 24 * 60 * 60) + (lastSeen_dato.getDayOfMonth() * 24 * 60 * 60) + (lastSeen_dato.getHour() * 60 * 60) + (lastSeen_dato.getMinute() * 60) + lastSeen_dato.getSecond();
        int millisekund_naa = (dato.getYear() * 12 * 31 * 24 * 60 * 60) + (dato.getMonth() * 31 * 24 * 60 * 60) + (dato.getDayOfMonth() * 24 * 60 * 60) + (dato.getHour() * 60 * 60) + (dato.getMinute() * 60) + dato.getSecond();
        return millisekund_naa - millisekund_lastseen;
    }

    private void makeSummarySimple2() {
        //Log.i(this.toString(), "makeSummarySimple2");

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
        //https://www.bluetooth.com/learn-about-bluetooth/key-attributes/range/
        if (getType == 0) {
            type_GUI = "Unknown";
        } else if (getType == 1) {
            type_GUI = "<b>BR/EDR</b> (12-59m)";
        } else if (getType == 2) {
            type_GUI = "<b>LE-only</b> (13-140m)";
        } else if (getType == 3) {
            type_GUI = "<b>BR/EDR/LE</b> (13-140m)";
        }


        String bluetoothClass_hex = getBluetoothClass + "";
        StringBuilder bluetoothClass_binary = new StringBuilder(hexToBin(bluetoothClass_hex));
        while (bluetoothClass_binary.length() < 23) {
            bluetoothClass_binary.insert(0, "0");
        }
        bluetoothClass_binary.substring((bluetoothClass_binary.length() - (6 + 6)), (bluetoothClass_binary.length() - 7));
        bluetoothClass_binary.substring((bluetoothClass_binary.length() - (6 + 2)), (bluetoothClass_binary.length() - 6));
        bluetoothClass_binary.substring((bluetoothClass_binary.length() - (6 + 2)), (bluetoothClass_binary.length() - 2));
        bluetoothClass_binary.substring((bluetoothClass_binary.length() - (4 + 2)), (bluetoothClass_binary.length() - 2));
        int fix = 1;
        int dig1 = 22;
        String[] bin1 = new String[bluetoothClass_binary.length() + 1];
        while (fix < 23) {
            bin1[dig1] = bluetoothClass_binary.substring(fix - 1, fix);
            fix++;
            dig1--;
        }

        //http://domoticx.com/bluetooth-class-of-device-lijst-cod/
        //https://www.ampedrftech.com/datasheets/cod_definition.pdf

        switch (getBluetoothClass) {
            case "2a010c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, laptop, with network, capturing and audio)</b>";
                manual_producttype = "<b>Laptop</b>";
                break;
            case "2a0104":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, desktop, with network, capturing, audio)</b>";
                break;
            case "2c0404":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Wearable, headset, with rendering, capturing, audio)</b>";
                break;
            case "2c0414":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio, loudspeaker, with rendering, capturing, audio)</b>";
                break;
            case "3a0104":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, deskto, with network, capturing, object transfer, audiop)</b>";
                break;
            case "3e010c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, laptop, with netowkr, rendering, capturing, object transfer, audio)</b>";
                break;
            case "3e0104":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, desktio, with network, rendering, capturing, object transfer, audio)</b>";
                break;
            case "4c0104":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, desktop, with rendering, capturing, telephony)</b>";
                break;
            case "5a010c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, laptop, with network, capturing, object transfer, telephony)</b>";
                break;
            case "5a020c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Phone, smart, with network, capturing, object transfer, telephony)</b>";
                manual_producttype = "<b>Smart phone</b>";
                break;
            case "5a0204":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Phone, non-smart cell, with network, capturing, object transfer, telephony)</b>";
                break;
            case "7a020c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Phone, smart, with network, capturing, object transfer, audio, telephony)</b>";
                manual_producttype = "<b>Smart phone</b>";
                break;
            case "43c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, video display and loudspeaker)</b>";
                manual_producttype = "<b>TV</b>";
                break;
            case "50c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Peripheral, remote control)</b>";
                break;
            case "100":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, uncategorized)</b>";
                break;
            case "400":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, uncategorized)</b>";
                break;
            case "420e00":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Misc, with network, telephony)</b>";
                break;
            case "680":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Printer)</b>";
                break;
            case "704":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Wearable, smart watch)</b>";
                manual_producttype = "<b>Smart watch</b>";
                break;
            case "2010c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, laptop, with network)</b>";
                break;
            case "8043c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, video display and loudspeaker, with capturing)</b>";
                manual_producttype = "<b>TV</b>";
                break;
            case "38010c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, laptop, with capturing, object transfer, audio)</b>";
                break;
            case "38043c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, video display and loudspeaker, with capturing, object transfer, audio)</b>";
                manual_producttype = "<b>TV</b>";
                break;
            case "40680":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Printer, with rendering)</b>";
                break;
            case "60680":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Printer, with network, rendering)</b>";
                break;
            case "61021c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Phone, uncategorized, with positioning, audio, telephony)</b>";
                break;
            case "80540":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Peripheral, keyboard, with capturing)</b>";
                break;
            case "100104":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, desktop, with object transfer)</b>";
                break;
            case "200408":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, handsfree, with audio)</b>";
                break;
            case "240404":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, wearable, headset, with rendering, audio)</b>";
                break;
            case "240414":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, loudspeaker, with rendering, audio)</b>";
                break;
            case "240418":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, headphones, with rendering, audio)</b>";
                break;
            case "260408":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, handsfree, with network, rendering, audio)</b>";
                break;
            case "280424":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, set-top box, with capturing, audio)</b>";
                manual_producttype = "<b>Set-top box</b>";
                break;
            case "280704":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Wearable, smart watch, with capturing, audio)</b>";
                break;
            case "340404":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/vicdeo, wearable, headset, with rendering, object transfer, audio)</b>";
                break;
            case "340408":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, handsfree, with rendering, object transfer, audio)</b>";
                break;
            case "340428":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, Hifi, with rendering, object transfer, audio)</b>";
                break;
            case "342408":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, handsfree, with discoverable mode, rendering, object transfer, audio)</b>";
                break;
            case "380104":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, desktop, with capturing, object transfer, audio)</b>";
                break;
            case "760408":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, handsfree, with network, rendering, object transfer, audio, telehpony)</b>";
                break;
            case "a010c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Computer, laptop, with network, capturing)</b>";
                break;
            case "c043c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, video display and loudspeaker, with rendering, capturing)</b>";
                manual_producttype = "<b>TV</b>";
                break;
            case "c243c":
                bluetoothClass_GUI = bluetoothClass_GUI + "<b>(Audio/video, video display and loudspeaker, with discoverable mode, rendering, capturing)</b>";
                break;
        }

        if (bluetoothClass_GUI.length() < 1) {

            String majorDeviceClass_hex = getMajorDeviceClass + "";
            StringBuilder majorDeviceClass_binary = new StringBuilder(hexToBin(majorDeviceClass_hex));
            while (majorDeviceClass_binary.length() < 23) {
                majorDeviceClass_binary.insert(0, "0");
            }
            majorDeviceClass_binary.substring((majorDeviceClass_binary.length() - (6 + 6)), (majorDeviceClass_binary.length() - 7));
            majorDeviceClass_binary.substring((majorDeviceClass_binary.length() - (6 + 2)), (majorDeviceClass_binary.length() - 2));
            //https://docs.microsoft.com/en-us/windows/security/identity-protection/hello-for-business/hello-feature-dynamic-lock

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


            deviceClass_GUI = getDeviceClass + "";
            //https://developer.android.com/reference/android/bluetooth/BluetoothClass.Device
            //https://docwiki.embarcadero.com/Libraries/Sydney/en/System.Bluetooth.TBluetoothDevice.ClassDevice

            if (getDeviceClass == 256) {
                deviceClass_GUI = "<b>Computer, unknown type</b>";
            } else if (getDeviceClass == 260) {
                deviceClass_GUI = "<b>Computer, desktop</b>";
            } else if (getDeviceClass == 268) {
                deviceClass_GUI = "<b>Computer, laptop</b>";
            } else if (getDeviceClass == 516) {
                deviceClass_GUI = "<b>Phone, non-smart cell</b>";
            } else if (getDeviceClass == 524) {
                deviceClass_GUI = "<b>Phone, smart</b>";
            } else if (getDeviceClass == 540) {
                deviceClass_GUI = "<b>GPS tracker</b>";
            } else if (getDeviceClass == 1024) {
                deviceClass_GUI = "<b>Audio, unknown</b>";
            } else if (getDeviceClass == 1028) {
                deviceClass_GUI = "<b>Wearable, headset</b>";
            } else if (getDeviceClass == 1032) {
                deviceClass_GUI = "<b>Car device, handsfree</b>";
            } else if (getDeviceClass == 1044) {
                deviceClass_GUI = "<b>Audio, loudspeaker</b>";
            } else if (getDeviceClass == 1048) {
                deviceClass_GUI = "<b>Audio, headphones</b>";
            } else if (getDeviceClass == 1060) {
                deviceClass_GUI = "<b>Set-top box</b>";
            } else if (getDeviceClass == 1064) {
                deviceClass_GUI = "<b>Audio, HiFi</b>";
            } else if (getDeviceClass == 1084) {
                deviceClass_GUI = "<b>Video and loudspeaker, TV</b>";
            } else if (getDeviceClass == 1292) {
                deviceClass_GUI = "<b>Peripheral, remote controlled</b>";
            } else if (getDeviceClass == 1344) {
                deviceClass_GUI = "<b>Peripheral, capturing with keyboard</b>";
            } else if (getDeviceClass == 1664) {
                deviceClass_GUI = "<b>Printer</b>";
            } else if (getDeviceClass == 1794) {
                deviceClass_GUI = "Unknown";
            } else if (getDeviceClass == 1796) {
                deviceClass_GUI = "<b>Wearable, smart watch</b>";
            } else if (getDeviceClass == 3584) {
                deviceClass_GUI = "?";
            } else if (getDeviceClass == 7936) {
                deviceClass_GUI = "Misc";
            } else {
                deviceClass_GUI = "Unknown";
            }
        }

        bondState_GUI = getBondState + "";
        //10
        //12
        if (getBondState == 10) {
            bondState_GUI = "";
        } else if (getBondState == 11) {
            bondState_GUI = "Bonding";
        } else if (getBondState == 12) {
            bondState_GUI = "<b>Bonded</b>";
        }

        settVendor();

        //fra navn
        if (getName != null) {
            if (getName.toLowerCase(Locale.ROOT).contains("[tv]")) {
                manual_producttype = "<b>TV</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("[lg] webos")) {
                manual_producttype = "<b>TV</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("[signage]")) {
                manual_producttype = "<b>TV, signage</b>";
            }

            if ((getName.toLowerCase(Locale.ROOT).contains("phone")) && (!getName.toLowerCase(Locale.ROOT).contains("headphone"))) {
                manual_producttype = "<b>Phone, smart</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("huawei mate")) {
                manual_producttype = "<b>Phone, smart</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("huawei p20")) {
                manual_producttype = "<b>Phone, smart</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("huawei p30")) {
                manual_producttype = "<b>Phone, smart</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("galaxy")) {
                manual_producttype = "<b>Phone, smart</b>";
            }

            if (getName.toLowerCase(Locale.ROOT).contains("watch")) {
                manual_producttype = "<b>Wearable, smart watch</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("charge")) {
                manual_producttype = "<b>Wearable, smart watch</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("versa")) {
                manual_producttype = "<b>Wearable, smart watch</b>";
            }

            if (getName.toLowerCase(Locale.ROOT).contains("cbu")) {
                manual_producttype = "<b>Smart light, dimmer</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("lamp")) {
                manual_producttype = "<b>Smart light, lamp</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("bulb")) {
                manual_producttype = "<b>Smart light, bulb</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("spotlight")) {
                manual_producttype = "<b>Smart light, bulb</b>";
            }

            if (getName.toLowerCase(Locale.ROOT).contains("laptop")) {
                manual_producttype = "<b>Computer, laptop</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("macbook")) {
                manual_producttype = "<b>Computer, laptop</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("[monitor]")) {
                manual_producttype = "<b>Computer, screen</b>";
            }

            if (getName.toLowerCase(Locale.ROOT).contains("toyota")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("volvo")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("vw")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("audi")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("nextbase")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("car audio")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("tesla")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("skoda")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("drivesmart")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("peugeot")) {
                manual_producttype = "<b>Car device</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("obd")) {
                manual_producttype = "<b>Car device</b>";
            }

            if (getName.toLowerCase(Locale.ROOT).contains("jbl flip")) {
                manual_producttype = "<b>Loudspeaker</b>";
            }

            if (getName.toLowerCase(Locale.ROOT).contains("lime-")) {
                manual_producttype = "<b>Peripheral, scooter</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("scooter")) {
                manual_producttype = "<b>Peripheral, scooter</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("tier")) {
                manual_producttype = "<b>Peripheral, scooter</b>";
            }

            if (getName.toLowerCase(Locale.ROOT).contains("toothbrush")) {
                manual_producttype = "<b>Peripheral, toothbrush</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("refrigerator")) {
                manual_producttype = "<b>Peripheral, refrigerator</b>";
            }
            if (getName.toLowerCase(Locale.ROOT).contains("googlehome")) {
                manual_producttype = "<b>Peripheral, smartbox</b>";
            }
        }

        //TVr, grønt
        //Mobiler, rødt
        //Laptoper, blått
        //Biler, lilla
        //wearables, oransje
        //peripherals, gulbeige
        if (this.MAC.equals("8C:79:F5:03:79:29")) { //TVn min
            this.deviceHighlightColor = "#00e64d";
            deviceHightlightText = ", Min TV";
        } else if (this.MAC.equals("C3:AB:45:C9:D8:54")) { //min fitbit
            this.deviceHighlightColor = "#ff9933";
            deviceHightlightText = ", Min Fitbit";
        } else if (this.MAC.equals("10:63:C8:66:72:5E")) { //pappas laptop
            this.deviceHighlightColor = "#ffc61a";
            deviceHightlightText = ", Pappas laptop";
        } else if (this.MAC.equals("C4:5D:83:D6:B6:D6")) { //pappas tlf
            this.deviceHighlightColor = "#ff1a1a";
            deviceHightlightText = ", Pappas tlf";
        } else if (this.MAC.equals("D0:31:42:AA:0C:3C")) { //pappas amazfit
            this.deviceHighlightColor = "#ff9933";
            deviceHightlightText = ", Pappas Amazfit";
        } else if (this.MAC.equals("84:76:37:39:08:CD")) { //olivers tlf
            this.deviceHighlightColor = "#ff1a1a";
            deviceHightlightText = ", Olivers tlf";
        } else if (this.MAC.equals("14:20:5E:86:22:07")) { //enten Olivers eller Marias nettbrett
            this.deviceHighlightColor = "#ff1a1a";
            deviceHightlightText = ", Oliver eller Marias iPad";

        } else if (bluetoothClass_GUI.toLowerCase(Locale.ROOT).contains("phone")) {
            this.deviceHighlightColor = "#cc0000";
        } else if (manual_producttype.toLowerCase(Locale.ROOT).contains("phone")) {
            this.deviceHighlightColor = "#cc0000";

        } else if (bluetoothClass_GUI.toLowerCase(Locale.ROOT).contains("wearable")) {
            this.deviceHighlightColor = "#b35900";
        } else if (manual_producttype.toLowerCase(Locale.ROOT).contains("wearable")) {
            this.deviceHighlightColor = "#b35900";

        } else if (manual_producttype.contains("TV")) {
            this.deviceHighlightColor = "#006622";


        } else if (bluetoothClass_GUI.toLowerCase().contains("computer")) {
            this.deviceHighlightColor = "#0000ff";
        } else if (manual_producttype.toLowerCase().contains("computer")) {
            this.deviceHighlightColor = "#0000ff";
        } else if (majorDeviceClass_GUI.toLowerCase().contains("computer")) {
            this.deviceHighlightColor = "#0000ff";
        } else if (manual_producttype.contains("Computer, screen")) {
            this.deviceHighlightColor = "#0000ff";

        } else if (manual_producttype.contains("Car device")) {
            this.deviceHighlightColor = "#660066";

        } else if (bluetoothClass_GUI.toLowerCase().contains("peripheral")) {
            this.deviceHighlightColor = "#b38600";
        } else if (manual_producttype.contains("Printer")) {
            this.deviceHighlightColor = "#b38600";
        } else if (bluetoothClass_GUI.contains("Printer")) {
            this.deviceHighlightColor = "#b38600";
        } else if (manual_producttype.toLowerCase(Locale.ROOT).contains("scooter")) {
            this.deviceHighlightColor = "#b38600";

        } else if (manual_producttype.contains("Smart light")) {
            this.deviceHighlightColor = "#b38600";
        }

    }

    public void settVendor() {
        //fra MAC
        if (getMAC() != null) {
            if (getMAC().substring(0, 9).contains("00:03:19:")) {
                manual_Vendor = "<b>Infineon AG</b>";
            }
            if (getMAC().substring(0, 9).contains("00:06:66:")) {
                manual_Vendor = "<b>Roving Networks</b>";
            }
            if (getMAC().substring(0, 9).contains("00:07:4D:")) {
                manual_Vendor = "<b>Zebra Technologies</b>";
            }
            if (getMAC().substring(0, 9).contains("00:09:A7:")) {
                manual_Vendor = "<b>Bang & Olufsen</b>";
            }
            if (getMAC().substring(0, 9).contains("00:0D:6F:")) {
                manual_Vendor = "<b>Ember Corporation</b>";
            }
            if (getMAC().substring(0, 9).contains("00:15:F3:")) {
                manual_Vendor = "<b>Peltor AB</b>";
            }
            if (getMAC().substring(0, 9).contains("00:19:F5:")) {
                manual_Vendor = "<b>Imagination Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("00:1B:66:")) {
                manual_Vendor = "<b>Sennheiser</b>";
            }
            if (getMAC().substring(0, 9).contains("00:1E:42:")) {
                manual_Vendor = "<b>Teltonika</b>";
            }
            if (getMAC().substring(0, 9).contains("00:35:FF:")) {
                manual_Vendor = "<b>Lime</b>";
            }
            if (getMAC().substring(0, 9).contains("00:60:37:")) {
                manual_Vendor = "<b>NXP Semiconductiors</b>";
            }
            if (getMAC().substring(0, 9).contains("00:6F:F2:")) {
                manual_Vendor = "<b>Mitsumi Electric Co</b>";
            }
            if (getMAC().substring(0, 9).contains("00:7C:2D:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("00:A0:50:")) {
                manual_Vendor = "<b>Cypress Semiconductor</b>";
            }
            if (getMAC().substring(0, 9).contains("00:A0:96:")) {
                manual_Vendor = "<b>Mitsumi Electric</b>";
            }
            if (getMAC().substring(0, 9).contains("00:C3:F4:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("01:17:C5:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("04:4B:ED:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("04:52:C7:")) {
                manual_Vendor = "<b>Bose</b>";
            }
            if (getMAC().substring(0, 9).contains("04:EE:03:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("08:66:98:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("0C:2C:54:")) {
                manual_Vendor = "<b>Huawei</b>";
            }
            if (getMAC().substring(0, 9).contains("0C:EC:80:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("0C:F3:EE:")) {
                manual_Vendor = "<b>EM Microelectronic</b>";
            }

            if (getMAC().substring(0, 9).contains("10:2B:41:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("10:4E:89:")) {
                manual_Vendor = "<b>Garmin</b>";
            }
            if (getMAC().substring(0, 9).contains("14:3F:A6:")) {
                manual_Vendor = "<b>Sony/LE</b>";
            }
            if (getMAC().substring(0, 9).contains("14:3F:A6:")) {
                manual_Vendor = "<b>Sony</b>";
            }
            if (getMAC().substring(0, 9).contains("14:99:E2:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("16:CB:19:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("1C:1A:C0:")) {
                manual_Vendor = "<b>Apple</b>";
            }

            if (getMAC().substring(0, 9).contains("24:4B:03:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("24:FC:E5:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("28:11:A5:")) {
                manual_Vendor = "<b>Bose</b>";
            }
            if (getMAC().substring(0, 9).contains("28:A1:83:")) {
                manual_Vendor = "<b>Alpsalpine</b>";
            }
            if (getMAC().substring(0, 9).contains("28:FF:3C:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("2C:4C:C6:")) {
                manual_Vendor = "<b>Murata Manufactoring/GravaStar</b>";
            }
            if (getMAC().substring(0, 9).contains("2C:41:A1:")) {
                manual_Vendor = "<b>Bose</b>";
            }

            if (getMAC().substring(0, 9).contains("30:B1:B5:")) {
                manual_Vendor = "<b>LG</b>";
            }
            if (getMAC().substring(0, 9).contains("30:C3:D9:")) {
                manual_Vendor = "<b>Alpsalpine</b>";
            }
            if (getMAC().substring(0, 9).contains("34:81:F4:")) {
                manual_Vendor = "<b>SST Taiwan</b>";
            }
            if (getMAC().substring(0, 9).contains("34:C9:F0:")) {
                manual_Vendor = "<b>LM Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("34:FD:6A:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("38:18:4C:")) {
                manual_Vendor = "<b>Sony/LE</b>";
            }
            if (getMAC().substring(0, 9).contains("38:68:A4:")) {
                manual_Vendor = "<b>Samsung</b>";
            }

            if (getMAC().substring(0, 9).contains("44:5C:E9:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("44:73:D6:")) {
                manual_Vendor = "<b>Logitech</b>";
            }
            if (getMAC().substring(0, 9).contains("44:A6:E5:")) {
                manual_Vendor = "<b>Thinking Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("44:C6:5D:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("44:D8:78:")) {
                manual_Vendor = "<b>Hui Zhou Gaoshengda Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("48:A4:93:")) {
                manual_Vendor = "<b>Taiyo Yuden Co</b>";
            }
            if (getMAC().substring(0, 9).contains("4A:9E:BD:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("4C:87:5D:")) {
                manual_Vendor = "<b>Bose</b>";
            }

            if (getMAC().substring(0, 9).contains("50:32:37:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("50:51:A9:")) {
                manual_Vendor = "<b>Texas Instruments/Lime</b>";
            }
            if (getMAC().substring(0, 9).contains("50:DE:06:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("52:B7:D9:")) {
                manual_Vendor = "<b>JBL</b>";
            }
            if (getMAC().substring(0, 9).contains("54:3A:D6:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("54:B7:E5:")) {
                manual_Vendor = "<b>Rayson Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("5A:7C:3E:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("5C:B1:3E:")) {
                manual_Vendor = "<b>Sagemcom</b>";
            }
            if (getMAC().substring(0, 9).contains("5C:C1:D7:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("5C:F9:38:")) {
                manual_Vendor = "<b>Apple</b>";
            }

            if (getMAC().substring(0, 9).contains("60:77:71:")) {
                manual_Vendor = "<b>Texas Instruments/Toyota</b>";
            }
            if (getMAC().substring(0, 9).contains("60:AB:D2:")) {
                manual_Vendor = "<b>Bose</b>";
            }
            if (getMAC().substring(0, 9).contains("64:07:F6:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("64:1C:AE:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("64:1C:B0:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("64:E7:D8:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("64:D2:C4:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("68:72:C3:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("6C:4A:85:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("6C:B7:49:")) {
                manual_Vendor = "<b>Huawei Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("6C:BA:B8:")) {
                manual_Vendor = "<b>Sagemcom Broadband/Telia</b>";
            }


            if (getMAC().substring(0, 9).contains("70:2C:1F:")) {
                manual_Vendor = "<b>Wisol</b>";
            }
            if (getMAC().substring(0, 9).contains("70:26:05:")) {
                manual_Vendor = "<b>LE</b>";
            }
            if (getMAC().substring(0, 9).contains("70:B1:3D:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("70:B9:50:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("70:BF:92:")) {
                manual_Vendor = "<b>GN Audio (Jabra)</b>";
            }
            if (getMAC().substring(0, 9).contains("70:B1:3D:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("74:5C:4B:")) {
                manual_Vendor = "<b>Jabra</b>";
            }
            if (getMAC().substring(0, 9).contains("74:D2:85:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("78:05:41:")) {
                manual_Vendor = "<b>Queclink Wireless</b>";
            }
            if (getMAC().substring(0, 9).contains("78:BD:BC:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("78:DB:2F:")) {
                manual_Vendor = "<b>Lime</b>";
            }
            if (getMAC().substring(0, 9).contains("78:DB:2F:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("7C:0A:3F:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("7C:D1:C3:")) {
                manual_Vendor = "<b>Apple</b>";
            }

            if (getMAC().substring(0, 9).contains("80:6F:B0:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("84:EE:03:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("84:2E:14:")) {
                manual_Vendor = "<b>Silicon Laboratories</b>";
            }
            if (getMAC().substring(0, 9).contains("86:2A:FD:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("8C:EA:48:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("8C:85:90:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("8C:EC:80:")) {
                manual_Vendor = "?";
            }


            if (getMAC().substring(0, 9).contains("90:3A:E6:")) {
                manual_Vendor = "<b>Tesla</b>";
            }
            if (getMAC().substring(0, 9).contains("90:DD:5D:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("90:2E:AB:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("90:FD:9F:")) {
                manual_Vendor = "<b>Silicon Laboratories</b>";
            }
            if (getMAC().substring(0, 9).contains("94:DB:56:")) {
                manual_Vendor = "<b>Sony/LE</b>";
            }
            if (getMAC().substring(0, 9).contains("94:DB:56:")) {
                manual_Vendor = "<b>Sony</b>";
            }
            if (getMAC().substring(0, 9).contains("98:04:ED:")) {
                manual_Vendor = "mixed vendors";
            }
            if (getMAC().substring(0, 9).contains("9C:20:7B:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("9C:8C:6E:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("9C:8C:6E:")) {
                manual_Vendor = "<b>Samsung</b>";
            }


            if (getMAC().substring(0, 9).contains("A0:98:66:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("A0:9E:1A:")) {
                manual_Vendor = "<b>Polar</b>";
            }
            if (getMAC().substring(0, 9).contains("A4:04:50:")) {
                manual_Vendor = "<b>nFore Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("A4:30:7A:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("A4:9F:89:")) {
                manual_Vendor = "<b>Shanghai Rui Rui Comm. Tech.</b>";
            }
            if (getMAC().substring(0, 9).contains("A4:C1:38:")) {
                manual_Vendor = "<b>Telink Semiconfuctor (Taipei)</b>";
            }
            if (getMAC().substring(0, 9).contains("A4:DA:32:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("A4:E1:12:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("AC:BC:32:")) {
                manual_Vendor = "<b>Apple</b>";
            }


            if (getMAC().substring(0, 9).contains("B0:B9:50:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("B4:60:77:")) {
                manual_Vendor = "<b>Suchuan Changdong Electric</b>";
            }
            if (getMAC().substring(0, 9).contains("B4:C2:6A:")) {
                manual_Vendor = "<b>Garmin</b>";
            }
            if (getMAC().substring(0, 9).contains("B4:D2:85:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("B8:8A:5E:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("B8:BC:5B:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("B9:31:00:")) {
                manual_Vendor = "?/<b>Tier</b>";
            }
            if (getMAC().substring(0, 9).contains("B9:D7:12:")) {
                manual_Vendor = "?/<b>Tier</b>";
            }
            if (getMAC().substring(0, 9).contains("B9:D7:13:")) {
                manual_Vendor = "<b>Tier</b>";
            }


            if (getMAC().substring(0, 9).contains("C0:28:8D:")) {
                manual_Vendor = "<b>Logitech</b>";
            }
            if (getMAC().substring(0, 9).contains("C0:48:E6:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("C8:69:CD:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("CB:81:F4:")) {
                manual_Vendor = "<b>LE</b>";
            }
            if (getMAC().substring(0, 9).contains("CC:6E:A4:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("CC:98:8B:")) {
                manual_Vendor = "<b>Sony/LE</b>";
            }
            if (getMAC().substring(0, 9).contains("CC:C5:0A:")) {
                manual_Vendor = "<b>Shenzen Dajiahao Tech</b>";
            }


            if (getMAC().substring(0, 9).contains("D0:03:4B:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("D0:2E:AB:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("D0:49:7C:")) {
                manual_Vendor = "<b>Oneplus</b>";
            }
            if (getMAC().substring(0, 9).contains("D0:6E:DE:")) {
                manual_Vendor = "<b>Sagemcom Broadband</b>";
            }
            if (getMAC().substring(0, 9).contains("D0:D0:03:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("D2:90:93:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("D4:9D:C0:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("D4:9D:C0:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("D8:01:00:")) {
                manual_Vendor = "?";
            }
            if (getMAC().substring(0, 9).contains("D8:13:99:")) {
                manual_Vendor = "<b>Hui Zhou Gaoshengda Tech</b>";
            }
            if (getMAC().substring(0, 9).contains("DC:03:98:")) {
                manual_Vendor = "<b>LG Innotek</b>";
            }
            if (getMAC().substring(0, 9).contains("DC:56:E7:")) {
                manual_Vendor = "<b>Apple</b>";
            }


            if (getMAC().substring(0, 9).contains("E0:89:7E:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("EC:81:93:")) {
                manual_Vendor = "<b>Logitech</b>";
            }


            if (getMAC().substring(0, 9).contains("F0:5E:CD:")) {
                manual_Vendor = "<b>Texas Instruments</b>";
            }
            if (getMAC().substring(0, 9).contains("F0:99:19:")) {
                manual_Vendor = "<b>Garmin</b>";
            }
            if (getMAC().substring(0, 9).contains("F0:99:19:")) {
                manual_Vendor = "<b>Garmin</b>";
            }
            if (getMAC().substring(0, 9).contains("F0:B3:EC:")) {
                manual_Vendor = "<b>Apple</b>";
            }
            if (getMAC().substring(0, 9).contains("F8:30:02:")) {
                manual_Vendor = "<b>Texas Instruments/Lime</b>";
            }
            if (getMAC().substring(0, 9).contains("F8:DC:7A:")) {
                manual_Vendor = "<b>Variscite LTD</b>";
            }
            if (getMAC().substring(0, 9).contains("FC:03:9F:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
            if (getMAC().substring(0, 9).contains("FC:8F:90:")) {
                manual_Vendor = "<b>Samsung</b>";
            }
        }
    }

    public String getName() {
        if (getName == null) {
            getName = "";
        }
        return this.getName;
    }

    public boolean isAnonymous() {
        if (manual_Vendor.length() == 0) {
            return (getName == null) || (getName.length() == 4);
        } else {
            if ((getName != null)) {
                getName.length();
            }
        }
        return false;
    }

    public Dato getDato() {
        return this.lastSeen_dato;
    }

    public void updateAnonymous(BluetoothDevice device) {
        //Log.i(logtag, "updateAnonymous");
        getName = device.getName();
        getType = device.getType();
        getBluetoothClass = device.getBluetoothClass().toString();
        getDeviceClass = device.getBluetoothClass().getDeviceClass();
        getMajorDeviceClass = device.getBluetoothClass().getMajorDeviceClass();
        getBondState = device.getBondState();
        makeSummarySimple2();
    }
}
