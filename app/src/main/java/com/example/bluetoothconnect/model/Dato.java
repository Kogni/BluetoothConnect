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
        return year+ "." + month+"."+dayOfMonth+" "+hour+":"+minute+":"+second+"."+millisecond;
    }
}
