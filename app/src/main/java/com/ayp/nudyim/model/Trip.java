package com.ayp.nudyim.model;

/**
 * Created by Chaiwat on 10/11/2016.
 */

public class Trip {
    private String topic;
    private String startdate;
    private String enddate;
    private String location;
    private String details;
    private String photoUrl;

    public Trip() {
        // Default constructor Trip
    }

    public Trip(String topic,
                String startdate,
                String enddate,
                String location,
                String details,
                String photoUrl) {
        this.topic = topic;
        this.startdate = startdate;
        this.enddate = enddate;
        this.location = location;
        this.details = details;
        this.photoUrl = photoUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
