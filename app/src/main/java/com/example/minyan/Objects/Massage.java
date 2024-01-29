package com.example.minyan.Objects;

import java.util.Calendar;

public class Massage {
    public final static String MASSAGE = "MASSAGE";
    String from;
    String to;
    Calendar date;
    String text;

    public Massage(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.date = Calendar.getInstance();
        this.text = text;
    }

    public Massage() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
