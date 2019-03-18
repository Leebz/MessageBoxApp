package com.whut.androidtest.Bean;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Msgs")
public class MsgDetailBean implements Serializable {
    private static final int TYPE_RECEIVED = 0;
    private static final int TYPE_SENT = 1;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String content;
    private int type;
    private String date;
    private String partner;
    private int state;

    public MsgDetailBean(String content, int type, String date, String partner, int state) {

        this.content = content;
        this.type = type;
        this.date = date;
        this.partner = partner;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
