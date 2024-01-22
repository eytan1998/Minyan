package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        EditText editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        EditText editTextLoginPassword = findViewById(R.id.editTextLoginPassword);

        Button buttonLoginSighUp = findViewById(R.id.buttonLoginSighUp);
        Button buttonLoginSighIn = findViewById(R.id.buttonLoginSighIn);


        //if press Sign in
        buttonLoginSighIn.setOnClickListener(v -> {
            //TODO add user side if failed, no need to check if input is valid firebse does
            mAuth.signInWithEmailAndPassword(
                    editTextLoginEmail.getText().toString()
                    ,editTextLoginPassword.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("UID",mAuth.getUid());
                        startActivity(intent);
                    });
        });
        //if press Sign up
        buttonLoginSighUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}