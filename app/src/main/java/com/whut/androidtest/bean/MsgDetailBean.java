package com.whut.androidtest.bean;
import java.io.Serializable;

public class MsgDetailBean implements Serializable {
    private static final int TYPE_RECEIVED = 0;
    private static final int TYPE_SENT = 1;
    private String id;
    private String content;
    private int type;
    private String date;
    private String partner;
    private int state;

    public MsgDetailBean(String id, String content, int type, String date, String partner, int state) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.date = date;
        this.partner = partner;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
