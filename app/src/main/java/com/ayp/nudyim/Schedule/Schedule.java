package com.ayp.nudyim.schedule;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class Schedule {
    private String topic;
    private String detail;
    private String hour;
    private String minute;

    public Schedule(){

    }

    public Schedule(String topic, String detail, String hour, String minute){
        this.topic = topic;
        this.detail = detail;
        this.hour = hour;
        this.minute = minute;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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
}
