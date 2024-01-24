package com.example.minyan.Objects;

import com.example.minyan.Objects.enums.Kind;

import java.util.Date;

public class Pray {
    String name;
    Kind kind;
    Date time;
    String moreDetail;

    public Pray() {
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMoreDetail() {
        return moreDetail;
    }

    public void setMoreDetail(String moreDetail) {
        this.moreDetail = moreDetail;
    }
}
