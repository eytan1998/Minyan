package com.example.minyan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

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
        CheckBox checkBoxSignUpIsGabai = findViewById(R.id.checkBoxSignUpIsGabai);
        EditText editTextSignUpPhone = findViewById(R.id.editTextSignUpPhone);
        TextView textViewSignUpInfo = findViewById(R.id.textViewSignUpInfo);
        //sign in
        Button buttonSignUpSignUp = findViewById(R.id.buttonSignUpSignUp);

        //set up firebase auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //set up firebase database -> to save if successes signup
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        checkBoxSignUpIsGabai.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextSignUpPhone.setVisibility(View.VISIBLE);
                textViewSignUpInfo.setVisibility(View.VISIBLE);
            } else {
                editTextSignUpPhone.setVisibility(View.INVISIBLE);
                textViewSignUpInfo.setVisibility(View.INVISIBLE);
            }
        });


        //if try to sign in
        buttonSignUpSignUp.setOnClickListener(v -> {
            if (editTextSignInPassword.getText().toString().compareTo(editTextSignInPassword2.getText().toString()) != 0) {
                Toast.makeText(this, "הססמאות אינם זהות", Toast.LENGTH_LONG).show();
            } else {
                //TODO add the gabai option
                //TODO tell user if failure happened and witch
                //TODO check if all ready sign in
                //TODO check if need verify
                //TODo check if bad email
                //TODO check if bad password
                //TODO check if valid phone

                mAuth.createUserWithEmailAndPassword(editTextSignInEmail.getText().toString(),
                                editTextSignInPassword.getText().toString())
                        .addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                //if gabai
                                if (checkBoxSignUpIsGabai.isChecked()) {
                                    //todo maybe make collection of gabai waiting to verify
                                    Gabai gabai = new Gabai(
                                            editTextSignUpName.getText().toString(),
                                            editTextSignInEmail.getText().toString(),
                                            editTextSignUpPhone.getText().toString()
                                    );

                                    db.collection(Gabai.GABAI).document(mAuth.getUid()).set(gabai);


                                }
                                //pray and gabai has prayer account.
                                Prayer prayer = new Prayer(
                                        editTextSignUpName.getText().toString()
                                        , editTextSignInEmail.getText().toString()
                                        );
                                db.collection(Prayer.PRAYER).document(mAuth.getUid()).set(prayer);

                            }
                            //send verification
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailSendTask -> {
                                        //TODO check if worked
                                    });

                            //move to login on success
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                        });
            }
        });
    }
}