package com.mrdo.nudyim.model;

import java.util.List;

/**
 * Created by Chaiwat on 10/11/2016.
 */

public class Trip {
    private String topic;
    private String startDate;
    private String endDate;
    private String location;
    private String details;

    public Trip() {
        // Default constructor Trip
    }

    public Trip(String topic,
                String startDate,
                String endDate,
                String location,
                String details) {
        this.topic = topic;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.details = details;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
}
