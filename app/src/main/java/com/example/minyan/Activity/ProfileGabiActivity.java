package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileGabiActivity extends AppCompatActivity {
    Gabai currentGabai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gabi);

        ImageView imageViewProfileGabiProfile = findViewById(R.id.imageViewProfileGabiProfile);

        TextView textViewProfileGabaiName = findViewById(R.id.textViewProfileGabaiName);
        TextView textViewProfileGabaiQoute = findViewById(R.id.textViewProfileGabaiQoute);

        Button buttonProfileGabiManageSynagoe = findViewById(R.id.buttonProfileGabiManageSynagoe);
        Button buttonProfileGabiMassages = findViewById(R.id.buttonProfileGabiMassages);

        //to get the gabai from user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Gabai.GABAI).document(Gabai.GABAI + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentGabai = task.getResult().toObject(Gabai.class);
                        //todo get image
                        textViewProfileGabaiName.setText(currentGabai.getName());
                        textViewProfileGabaiQoute.setText(currentGabai.getQuote());
                    }
                });

        buttonProfileGabiManageSynagoe.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileGabiActivity.this, ManageSynagogeActivity.class);
            startActivity(intent);
        });
        buttonProfileGabiMassages.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileGabiActivity.this, MassagesActivity.class);
            intent.putExtra("KIND", Gabai.GABAI);
            startActivity(intent);
        });

    }
}