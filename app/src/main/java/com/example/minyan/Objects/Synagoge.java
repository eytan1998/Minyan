package com.example.minyan.Objects;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.minyan.Objects.enums.Nosah;
import com.example.minyan.Objects.relations.OwnSynagoge;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Synagoge {
    public static String SYNAGOGE = "SYNAGOGE";

    private String s_id;
    private String name;
    private Nosah nosah;
    private String  address;
    private double lat;
    private double lng;
    private String more_detail;


    public Synagoge() {
    }

    public Synagoge(String name, Nosah nosah, double latitude, double longitude,String address) {
        this.s_id = String.valueOf(UUID.randomUUID());
        this.name = name;
        this.nosah = nosah;
        this.lat = latitude;
        this.lng = longitude;
        this.address = address;
    }

    public String addPray(Pray pray){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Pray.PRAY).document(pray.getPray_id()).set(pray);
        PrayInSynagoge prayInSynagoge = new PrayInSynagoge(pray.getPray_id(), this.s_id);
        db.collection(PrayInSynagoge.PRAY_IN_SYNAGOGE).add(prayInSynagoge);
        return pray.getPray_id();
    }
    public void updatePray(Pray pray){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Pray.PRAY).document(pray.getPray_id()).set(pray);
    }
    public void delPray(Pray pray){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Pray.PRAY).document(pray.getPray_id()).delete();
        db.collection(PrayInSynagoge.PRAY_IN_SYNAGOGE).whereEqualTo("pray_id", pray.getPray_id())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID and delete the document
                            String documentId = document.getId();
                            db.collection(PrayInSynagoge.PRAY_IN_SYNAGOGE)
                                    .document(documentId)
                                    .delete();
                        }
                    }
                });
    }
    public static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
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

    public Nosah getNosah() {
        return nosah;
    }

    public void setNosah(Nosah nosah) {
        this.nosah = nosah;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
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

    public String getMore_detail() {
        return more_detail;
    }

    public void setMore_detail(String more_detail) {
        this.more_detail = more_detail;
    }

    @Override
    public String toString() {
        return this.name+"\n\t"+this.address;
    }
}

