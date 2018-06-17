package com.example.hnsang.pingtest.Object;

/**
 * Created by Ho Ngoc Sang on 13/06/2018.
 */
public class MainDataObject {
    private String idDevice;
    private String username;
    private String timeStart;

    public MainDataObject() {
    }

    public MainDataObject(String idDevice, String username, String timeStart) {
        this.idDevice = idDevice;
        this.username = username;
        this.timeStart = timeStart;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }
}
