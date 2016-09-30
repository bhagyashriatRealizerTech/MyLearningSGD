package com.realizer.schoolgeine.driver.model;

/**
 * Created by Bhagyashri on 9/28/2016.
 */
public class DriverInfo {

    int SrNo;
    String DriverUUID;
    String UserID;
    String FName;
    String AlternateNo;
    String MobileNo;
    String Address;
    String VehicleNo;
    String StartTime;
    String EndTime;
    String Schoolode;


    public int getSrNo() {
        return SrNo;
    }

    public void setSrNo(int srNo) {
        SrNo = srNo;
    }

    public String getDriverUUID() {
        return DriverUUID;
    }

    public void setDriverUUID(String driverUUID) {
        DriverUUID = driverUUID;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getAlternateNo() {
        return AlternateNo;
    }

    public void setAlternateNo(String alternateNo) {
        AlternateNo = alternateNo;
    }

    public String getSchoolode() {
        return Schoolode;
    }

    public void setSchoolode(String schoolode) {
        Schoolode = schoolode;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
