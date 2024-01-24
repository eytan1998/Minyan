package com.example.minyan.Objects;

import android.media.Image;

import com.example.minyan.Objects.relations.OwnSynagoge;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Gabai {
        public static String GABAI = "GABAI";
        String email;
        String name;

        String phone;

        boolean isVerified;
        Image image;
        String quote;

        public Gabai() {
        }

        public Gabai(String name, String email, String phone) {
                this.name = name;
                this.email = email;
                this.phone = phone;
                this.isVerified = false; //cant acsses gabai rigth without admin verify

        }

        public void addSynagoge(Synagoge synagoge){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Synagoge.SYNAGOGE).document(synagoge.getS_id()).set(synagoge);
                OwnSynagoge ownSynagoge = new OwnSynagoge(synagoge.getS_id(),this.email);
                db.collection(OwnSynagoge.OWN_SYNAGOGE).add(ownSynagoge);

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



