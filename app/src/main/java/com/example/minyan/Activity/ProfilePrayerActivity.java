package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfilePrayerActivity extends AppCompatActivity {

    Prayer currentPrayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_prayer);

        ImageView imageViewProfilePrayerProfile = findViewById(R.id.imageViewProfilePrayerProfile);

        TextView textViewProfilePrayerName = findViewById(R.id.textViewProfilePrayerName);
        TextView textViewProfilePrayerQoute = findViewById(R.id.textViewProfilePrayerQoute);

        Button buttonProfilePrayerFindSynagoe = findViewById(R.id.buttonProfilePrayerFindSynagoe);
        Button buttonProfilePrayerMassages = findViewById(R.id.buttonProfilePrayerMassages);

        //to get the gabai from user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Prayer.PRAYER).document(Prayer.PRAYER + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentPrayer = task.getResult().toObject(Prayer.class);
                        textViewProfilePrayerName.setText(currentPrayer.getName());
                        textViewProfilePrayerQoute.setText(currentPrayer.getQuote());
                        //todo get image
                    }
                });

        buttonProfilePrayerFindSynagoe.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, FindSynagogueActivity.class);
            startActivity(intent);
        });
        buttonProfilePrayerMassages.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, MassagesActivity.class);
            startActivity(intent);
        });

    }
}