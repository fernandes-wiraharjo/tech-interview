package com.example.checkout.domain;

public class User {

    private String id;
    private String phoneNumber;

    public User() {
    }

    public User(String id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
