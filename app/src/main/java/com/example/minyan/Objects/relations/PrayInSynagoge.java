package com.example.minyan.Objects.relations;

public class PrayInSynagoge {
    public static String PRAY_IN_SYNAGOGE = "PRAY_IN_SYNAGOGE";

    String pray_id;
    String s_id;

    public PrayInSynagoge() {
    }

    public PrayInSynagoge(String pray_id, String s_id) {
        this.pray_id = pray_id;
        this.s_id = s_id;
    }

    public String getPray_id() {
        return pray_id;
    }

    public void setPray_id(String pray_id) {
        this.pray_id = pray_id;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }
}
