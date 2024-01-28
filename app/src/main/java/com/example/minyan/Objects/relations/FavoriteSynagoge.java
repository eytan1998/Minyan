package com.example.minyan.Objects.relations;

public class FavoriteSynagoge {
    public static String FAVORITE_SYNAGOGUE = "FAVORITE_SYNAGOGUE";

    String s_id;
    String prayer_email;

    public FavoriteSynagoge() {
    }

    public FavoriteSynagoge(String s_id, String prayer_email) {
        this.s_id = s_id;
        this.prayer_email = prayer_email;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getPrayer_email() {
        return prayer_email;
    }

    public void setPrayer_email(String prayer_email) {
        this.prayer_email = prayer_email;
    }
}
