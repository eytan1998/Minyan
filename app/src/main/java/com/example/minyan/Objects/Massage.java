package com.example.minyan.Objects;

import com.google.firebase.Timestamp;

import java.util.Calendar;

public class Massage  implements Comparable<Massage> {
    public final static String MASSAGE = "MASSAGE";
    private String from;
    private String to;
    private Timestamp date;
    private String text;
    private boolean isRead;

    public Massage(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.date = new Timestamp(Calendar.getInstance().getTime());
        this.text = text;
        this.isRead = false;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(Massage o) {
        return this.date.compareTo(o.date);
    }

    @Override
    public String toString() {
        return "Massage{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", date=" + date +
                ", text='" + text + '\'' +
                '}';
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
