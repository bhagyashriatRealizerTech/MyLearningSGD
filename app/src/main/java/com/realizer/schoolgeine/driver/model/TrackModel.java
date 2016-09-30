package com.realizer.schoolgeine.driver.model;

/**
 * Created by Bhagyashri on 9/22/2016.
 */
public class TrackModel {
    public int id=0;
    public String locationTime = null;
    public String lati = null;
    public String langi =null;
    public String hasBroadcast = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLangi() {
        return langi;
    }

    public void setLangi(String langi) {
        this.langi = langi;
    }

    public String getHasBroadcast() {
        return hasBroadcast;
    }

    public void setHasBroadcast(String hasBroadcast) {
        this.hasBroadcast = hasBroadcast;
    }
}
