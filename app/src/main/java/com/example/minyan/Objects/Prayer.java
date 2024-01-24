package com.example.minyan.Objects;

import android.media.Image;

public class Prayer  {
    public static String PRAYER = "PRAYER";
    String name;
    String email;
    String quote;
    Image image;

    public Prayer(String name, String email) {
        this.name = name;
        this.email = email;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Prayer{" +
                "Email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
