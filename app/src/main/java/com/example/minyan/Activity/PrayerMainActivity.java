package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PrayerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_main);

        //to get the user that logged
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //to get user data
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection(Prayer.PRAYER).document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

            }
        });
//        }s -> {
//            Prayer prayer = queryDocumentSnapshots.getDocuments().get(0).toObject(Prayer.class);
//
//            ((TextView) findViewById(R.id.asd)).setText(prayer.toString());
//            ((TextView) findViewById(R.id.textViewMainGreating)).setText("");
//        });

    }
}