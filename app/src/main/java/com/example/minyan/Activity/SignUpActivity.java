package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


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
                //TODO check if need varify
                //TODo check if bad email
                //TODO check if bad password

                mAuth.createUserWithEmailAndPassword(editTextSignInEmail.getText().toString(),
                                editTextSignInPassword.getText().toString())
                        .addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                Prayer prayer = new Prayer(
                                        editTextSignInEmail.getText().toString()
                                        , editTextSignInPassword.getText().toString()
                                        , editTextSignUpName.getText().toString());
                                myRef.child("root").child("Prayer").child(mAuth.getUid()).setValue(prayer);
                                FirebaseUser user = mAuth.getCurrentUser();

                                assert user != null;
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailSendTask -> {
                                            //TODO check if worked
                                        });

                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else if (authTask.isCanceled()) {
                                Toast.makeText(this, authTask.getResult().toString(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }
}