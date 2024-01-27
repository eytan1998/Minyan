package com.example.minyan.Objects;

import com.example.minyan.Objects.enums.Kind;

import java.util.Date;
import java.util.UUID;

public class Pray {
    public static String PRAY = "PRAY";
    private String pray_id;

    private String name;
    private Kind kind;
    private String time;
    private String moreDetail;

    public Pray() {
    }

    public Pray(String name, String moreDetail, Kind kind, String time) {
        this.pray_id = String.valueOf(UUID.randomUUID());
        this.name = name;
        this.moreDetail = moreDetail;
        this.kind = kind;
        this.time = time;
    }

    public String getPray_id() {
        return pray_id;
    }

    public void setPray_id(String pray_id) {
        this.pray_id = pray_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMoreDetail() {
        return moreDetail;
    }

    public void setMoreDetail(String moreDetail) {
        this.moreDetail = moreDetail;
    }
}
