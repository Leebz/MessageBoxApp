package com.whut.androidtest.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    private String id;
    private String username;
    private String password;
    private String phoneNumber;

    public UserBean(String id, String username, String password, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



}

