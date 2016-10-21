package com.ayp.nudyim.schedule;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class Schedule {
    private String topic;
    private String name;
    private String hour;
    private String time;
    private String minute;

    public Schedule(){

    }

    public Schedule(String topic, String name, String time,String hour, String minute){
        this.topic = topic;
        this.name = name;
        this.time = time;
        this.hour = hour;
        this.minute = minute;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

