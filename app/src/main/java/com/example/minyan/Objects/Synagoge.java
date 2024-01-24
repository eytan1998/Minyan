package com.example.minyan.Objects;

import android.location.Address;

import com.example.minyan.Objects.enums.Nosah;

import java.util.UUID;

public class Synagoge {
    public static String SYNAGOGE = "SYNAGOGE";

    String s_id;
    String name;
    int nosah;
    Address address;
    double lat;
    double lng;
    String pdf_id;
    String more_detail;

    public Synagoge() {
    }

    public Synagoge( String name, int nosah, Address address, double lat, double lng) {
        this.s_id = String.valueOf(UUID.randomUUID());
        this.name = name;
        this.nosah = nosah;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public static String getSYNAGOGE() {
        return SYNAGOGE;
    }

    public static void setSYNAGOGE(String SYNAGOGE) {
        Synagoge.SYNAGOGE = SYNAGOGE;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNosah() {
        return nosah;
    }

    public void setNosah(int nosah) {
        this.nosah = nosah;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPdf_id() {
        return pdf_id;
    }

    public void setPdf_id(String pdf_id) {
        this.pdf_id = pdf_id;
    }

    public String getMore_detail() {
        return more_detail;
    }

    public void setMore_detail(String more_detail) {
        this.more_detail = more_detail;
    }
}

