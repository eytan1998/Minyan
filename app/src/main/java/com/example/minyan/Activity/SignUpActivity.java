package com.example.minyan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private String kindOfAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //name
        EditText editTextSignUpName = findViewById(R.id.editTextSignUpName);
        //email
        EditText editTextSignInEmail = findViewById(R.id.editTextSignUpEmail);
        //password
        EditText editTextSignInPassword = findViewById(R.id.editTextSignUpPassword);
        EditText editTextSignInPassword2 = findViewById(R.id.editTextSignUpPassword2);
        //gabai
        EditText editTextSignUpPhone = findViewById(R.id.editTextSignUpPhone);
        TextView textViewSignUpInfo = findViewById(R.id.textViewSignUpInfo);
        //sign in
        Button buttonSignUpSignUp = findViewById(R.id.buttonSignUpSignUp);

        //set up firebase auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //set up firebase database -> to save if successes signup
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        kindOfAccount = getIntent().getStringExtra(getString(R.string.kind));

        assert kindOfAccount != null;
        if (kindOfAccount.compareTo(getString(R.string.entry_gabai)) == 0) {
            editTextSignUpPhone.setVisibility(View.VISIBLE);
            textViewSignUpInfo.setVisibility(View.VISIBLE);
        } else {
            editTextSignUpPhone.setVisibility(View.INVISIBLE);
            textViewSignUpInfo.setVisibility(View.INVISIBLE);
        }


        //if try to sign in
        buttonSignUpSignUp.setOnClickListener(v -> {
            if (editTextSignInPassword.getText().toString().compareTo(editTextSignInPassword2.getText().toString()) != 0) {
                Toast.makeText(this, R.string.password_not_match, Toast.LENGTH_LONG).show();
            } else {
                //so signInWithEmailAndPassword wont throw
                if (editTextSignUpName.getText().toString().isEmpty() || editTextSignInEmail.getText().toString().isEmpty()) {
                    Toast.makeText(this, R.string.one_or_more_is_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(editTextSignInEmail.getText().toString(),
                                editTextSignInPassword.getText().toString())
                        .addOnFailureListener(e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show())
                        .addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                //if gabai
                                if (kindOfAccount.compareTo(getString(R.string.entry_gabai)) == 0) {
                                    Gabai gabai = new Gabai(
                                            editTextSignUpName.getText().toString(),
                                            editTextSignInEmail.getText().toString(),
                                            editTextSignUpPhone.getText().toString()
                                    );
                                    db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).set(gabai);

                                }
                                //prayer and gabai has prayer account.
                                Prayer prayer = new Prayer(
                                        editTextSignUpName.getText().toString()
                                        , editTextSignInEmail.getText().toString()
                                );
                                db.collection(getString(R.string.entry_prayer)).document(getString(R.string.entry_prayer) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).set(prayer);

                            }
                            //send verification
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            user.sendEmailVerification();

                            //move to login on success
                            Intent intent = new Intent(SignUpActivity.this, IntroActivity.class);
                            startActivity(intent);
                        });
            }
        });
    }
}