package com.example.bluetoothconnect.model;

public class Dato {

    int year=2021;
    int month=01;
    int dayOfMonth=01;
    int hour=0;
    int minute=0;
    int second=0;
    int millisecond=0;

    public Dato(){

    }

    public void setYear(int newyear) {
        year=newyear;
    }

    public void setMonth(int newmonth) {
        month=newmonth;
    }

    public void setDayOfMonth(int newDayOfMonth) {
        dayOfMonth = newDayOfMonth;
    }

    public void setHour(int newHour) {
        if ( newHour>24){
            newHour = 0;
        }
        hour = newHour;
    }

    public void setMinute(int newMinute) {
        minute = newMinute;
    }

    public void setSecond(int newSecond) {
        second = newSecond;
    }

    public void setMillisecond(int newMillisecond) {
        millisecond = newMillisecond;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public String getDate() {
        //pad
        String month_s = String.valueOf(month);
        String day_s = String.valueOf(dayOfMonth);
        String hour_s = String.valueOf(hour);
        String minute_s = String.valueOf(minute);
        String second_s = String.valueOf(second);
        String millisecond_s = String.valueOf(millisecond);
        if ( month_s.length()<2) month_s = "0"+month_s;
        if ( day_s.length()<2) day_s = "0"+day_s;
        if ( hour_s.length()<2) hour_s = "0"+hour_s;
        if ( minute_s.length()<2) minute_s = "0"+minute_s;
        if ( second_s.length()<2) second_s = "0"+second_s;
        while ( millisecond_s.length()<4) millisecond_s = "0"+millisecond_s;
        //return year+ "." + month+"."+dayOfMonth+" "+hour+":"+minute+":"+second+"."+millisecond;
        return year+ "." + month_s+"."+day_s+" "+hour_s+":"+minute_s+":"+second_s+"."+millisecond_s;
    }
}
