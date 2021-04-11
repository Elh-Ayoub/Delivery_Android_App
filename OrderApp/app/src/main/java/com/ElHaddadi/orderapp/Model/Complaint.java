package com.ElHaddadi.orderapp.Model;

public class Complaint {
    private String name;
    private String phone;
    private String complaint;

    public Complaint() {
    }

    public Complaint(String name, String phone, String complaint) {
        this.name = name;
        this.phone = phone;
        this.complaint = complaint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }
}
