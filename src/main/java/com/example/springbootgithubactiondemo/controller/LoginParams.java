package com.example.springbootgithubactiondemo.controller;

public class LoginParams {
    private String facility;
    private String cdcNumber;
    public LoginParams(String facility, String cdcnumber) { this.facility = facility; this.cdcNumber = cdcnumber; }

    public String getFacility() { return facility; }
    public void setFacility(String facility) { this.facility = facility; }

    public String getCdcNumber() { return cdcNumber; }
    public void setCdcNumber(String cdcnumber) { this.cdcNumber = cdcnumber; }
}


