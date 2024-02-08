package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
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


        Button buttonProfilePrayerToGabai = findViewById(R.id.buttonProfilePrayerToGabai);
        Button buttonProfilePrayerFindSynagoe = findViewById(R.id.buttonProfilePrayerFindSynagoe);
        Button buttonProfilePrayerMassages = findViewById(R.id.buttonProfilePrayerMassages);
        Button buttonProfilePrayerFavorite = findViewById(R.id.buttonProfilePrayerFavorite);

        //to get the prayer from user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.entry_prayer)).document(getString(R.string.entry_prayer) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
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

        buttonProfilePrayerToGabai.setOnClickListener(v -> {
            ChaneToGabaiDialog chanetogabaidialog = new ChaneToGabaiDialog();
            chanetogabaidialog.show();
        });

        buttonProfilePrayerMassages.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, MassagesActivity.class);
            intent.putExtra(getString(R.string.kind), getString(R.string.entry_prayer));
            startActivity(intent);
        });
        buttonProfilePrayerFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });


    }

    /**
     * =========================EditPrayDialog==================================
     */
    private class ChaneToGabaiDialog extends Dialog {


        public ChaneToGabaiDialog() {
            super(ProfilePrayerActivity.this);
        }



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_change_to_gabai);

            EditText editTexttToGabaiPhone = findViewById(R.id.editTexttToGabaiPhone);
            Button buttonToGabaiexit = findViewById(R.id.buttonToGabaiexit);
            Button buttonToGabaisend = findViewById(R.id.buttonToGabaisend);


            buttonToGabaisend.setOnClickListener(v -> {
                //get gabai
                if (editTexttToGabaiPhone.getText().toString().isEmpty()) {
                    Toast.makeText(ProfilePrayerActivity.this, R.string.one_or_more_is_empty, Toast.LENGTH_LONG).show();
                }
                    else if (currentPrayer != null) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Gabai gabai = new Gabai(
                            currentPrayer.getName(),
                            currentPrayer.getEmail(),
                            editTexttToGabaiPhone.getText().toString()

                    );
                    db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + Objects.requireNonNull(currentPrayer).getEmail()).set(gabai);


                Toast.makeText(ProfilePrayerActivity.this, "הבקשה נשלחה", Toast.LENGTH_SHORT).show();
                dismiss();
                    }
                else{
                    Toast.makeText(ProfilePrayerActivity.this, "שגיאה", Toast.LENGTH_LONG).show();
                }
            });

            buttonToGabaiexit.setOnClickListener(v -> dismiss());


        }

    }

}