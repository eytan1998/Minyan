package com.example.minyan.Objects.relations;

public class OwnSynagoge {
    public static String OWN_SYNAGOGE = "OWN_SYNAGOGE";

    String s_id;
    String gabai_email;

    public OwnSynagoge() {

    }

    public OwnSynagoge(String s_id, String gabai_email) {
        this.s_id = s_id;
        this.gabai_email = gabai_email;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getGabai_email() {
        return gabai_email;
    }

    public void setGabai_email(String gabai_email) {
        this.gabai_email = gabai_email;
    }
}
