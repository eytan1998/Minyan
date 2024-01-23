package com.example.minyan.Objects;

import android.media.Image;

public class Gabai {
        public static String GABAI = "GABAI";
        String name;
        String email;

        String phone;

        boolean isVerified;
        Image image;
        String quote;
        //TODO Conversatio
        public Gabai() {
        }

        public Gabai(String name, String email, String phone) {
                this.name = name;
                this.email = email;
                this.phone = phone;
                this.isVerified = false; //cant acsses gabai rigth without admin verify

        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getPhone() {
                return phone;
        }

        public void setPhone(String phone) {
                this.phone = phone;
        }

        public boolean isVerified() {
                return isVerified;
        }

        public void setVerified(boolean verified) {
                isVerified = verified;
        }

        public Image getImage() {
                return image;
        }

        public void setImage(Image image) {
                this.image = image;
        }

        public String getQuote() {
                return quote;
        }

        public void setQuote(String quote) {
                this.quote = quote;
        }
}



