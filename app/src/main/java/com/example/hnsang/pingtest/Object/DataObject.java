package com.example.hnsang.pingtest.Object;

/**
 * Created by Ho Ngoc Sang on 13/06/2018.
 */
public class DataObject {
    private String idDevice;
    private String username;
    private String idCustomer;
    private String total;
    private String success;
    private String ping;
    private String timeStart;

    public DataObject() {
    }

    public DataObject(String idDevice, String username, String idCustomer, String total, String success, String ping, String timeStart) {
        this.idDevice = idDevice;
        this.username = username;
        this.idCustomer = idCustomer;
        this.total = total;
        this.success = success;
        this.ping = ping;
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

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getPing() {
        return ping;
    }

    public void setPing(String ping) {
        this.ping = ping;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }
}
