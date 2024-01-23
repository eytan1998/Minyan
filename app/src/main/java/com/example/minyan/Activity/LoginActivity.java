package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        EditText editTextLoginPassword = findViewById(R.id.editTextLoginPassword);

        Button buttonLoginSighUp = findViewById(R.id.buttonLoginSighUp);
        Button buttonLoginSighIn = findViewById(R.id.buttonLoginSighIn);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //to get if gabai
        FirebaseFirestore db = FirebaseFirestore.getInstance();


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
                            //check if gabai
                            DocumentReference docRef = db.collection(Gabai.GABAI).document(user.getUid());
                            docRef.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Intent intent = new Intent(LoginActivity.this, AskGabaiActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, PrayerMainActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    // todo Handle the error
//                                    Exception e = task.getException();
//                                    if (e != null) {
//
//                                        // Log or handle the exception
//                                    }
                                }
                            });

                        } else {
                            Toast.makeText(this, "צריך לאמת חשבון", Toast.LENGTH_LONG).show();
                        }
                    });
        });
        //if press Sign up
        buttonLoginSighUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}