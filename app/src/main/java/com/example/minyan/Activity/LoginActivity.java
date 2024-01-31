package com.example.minyan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minyan.Objects.Gabai;
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

        kindOfAccount = getIntent().getStringExtra(getString(R.string.kind));

        //if press Sign in
        buttonLoginSighIn.setOnClickListener(v -> {
            //so signInWithEmailAndPassword wont throw
            if (editTextLoginEmail.getText().toString().isEmpty() || editTextLoginPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.one_or_more_is_empty, Toast.LENGTH_LONG).show();
                return;
            }
            //so cant spam click
            buttonLoginSighIn.setClickable(false);
            buttonLoginSighUp.setClickable(false);
            //sign in
            mAuth.signInWithEmailAndPassword(
                            editTextLoginEmail.getText().toString()
                            , editTextLoginPassword.getText().toString())
                    //so we know why cant login
                    .addOnFailureListener(e -> {
                        buttonLoginSighIn.setClickable(true);
                        buttonLoginSighUp.setClickable(true);
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    })
                    .addOnSuccessListener(authResult -> {
                        //check if verify the email
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        if (user.isEmailVerified()) {
                            //check if exist in gabai
                            if (kindOfAccount.compareTo(getString(R.string.entry_gabai)) == 0) {
                                DocumentReference docRef = db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                                docRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //if gabai check if there are and check if phone verified
                                            if (Objects.requireNonNull(document.toObject(Gabai.class)).isVerified()) {
                                                Intent intent = new Intent(LoginActivity.this, ProfileGabiActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(this, R.string.wait_for_phone_auth, Toast.LENGTH_LONG).show();

                                            }
                                        } else {
                                            Toast.makeText(this, R.string.not_gabai, Toast.LENGTH_LONG).show();

                                        }
                                    }
                                    buttonLoginSighIn.setClickable(true);
                                    buttonLoginSighUp.setClickable(true);

                                });
                                //check if exist in prayer
                            } else if (kindOfAccount.compareTo(getString(R.string.entry_prayer)) == 0) {
                                DocumentReference docRef = db.collection(getString(R.string.entry_prayer)).document(getString(R.string.entry_prayer) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                                docRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Intent intent = new Intent(LoginActivity.this, ProfilePrayerActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });


                            }
                        }
                    });
        });
        //if press Sign up
        buttonLoginSighUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.putExtra(getString(R.string.kind), kindOfAccount);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //so if go back the buttons will work
        findViewById(R.id.buttonLoginSighUp).setClickable(true);
        findViewById(R.id.buttonLoginSighIn).setClickable(true);
    }
}