package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    String kindOfAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        EditText editTextLoginPassword = findViewById(R.id.editTextLoginPassword);

        Button buttonLoginSighUp = findViewById(R.id.buttonLoginSighUp);
        Button buttonLoginSighIn = findViewById(R.id.buttonLoginSighIn);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        kindOfAccount = getIntent().getStringExtra("KIND");

        //if press Sign in
        buttonLoginSighIn.setOnClickListener(v -> {

            //TODO add user side if failed, no need to check if input is valid firebse does

            //sign in
            mAuth.signInWithEmailAndPassword(
                            editTextLoginEmail.getText().toString()
                            , editTextLoginPassword.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        //check if verify the email
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        if (user.isEmailVerified()) {
                            //check if exsict in gabai
                            if (kindOfAccount.compareTo(Gabai.GABAI) == 0) {

                                DocumentReference docRef = db.collection(Gabai.GABAI).document(Gabai.GABAI + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                                docRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            if (document.toObject(Gabai.class).isVerified()) {
                                                Intent intent = new Intent(LoginActivity.this, ProfileGabiActivity.class);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(this, "צריך לחכות לאימות טלפוני", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    }
                                });
                                //check if exsict in prayer
                            } else if (kindOfAccount.compareTo(Prayer.PRAYER) == 0) {
                                DocumentReference docRef = db.collection(Prayer.PRAYER).document(Prayer.PRAYER + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                                docRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Intent intent = new Intent(LoginActivity.this, ProfilePrayerActivity.class);
                                            startActivity(intent);
                                        }
                                    }else{
                                        Toast.makeText(this, "אחד הפרטים לא נכונים", Toast.LENGTH_LONG).show();

                                    }
                                });


                            }
                        }
                    });
        });
        //if press Sign up
        buttonLoginSighUp.setOnClickListener(v ->

        {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}