package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
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
            if(isChecked){
                editTextSignUpPhone.setVisibility(View.VISIBLE);
                textViewSignUpInfo.setVisibility(View.VISIBLE);
            }else{
                editTextSignUpPhone.setVisibility(View.INVISIBLE);
                textViewSignUpInfo.setVisibility(View.INVISIBLE);
            }
        });
        //if try to sign in
        buttonSignUpSignUp.setOnClickListener(v -> {
            //TODO compare the two passwords
            //TODO ask name
            //TODO add the gabai option
            //TODO tell user if failure happened
            mAuth.createUserWithEmailAndPassword(editTextSignInEmail.getText().toString(),
                    editTextSignInPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Prayer prayer = new Prayer(editTextSignInEmail.getText().toString(),editTextSignInPassword.getText().toString());
                             myRef.child("root").child("Prayer").child(mAuth.getUid()).setValue(prayer);
                        }else if (task.isCanceled()){
                            Toast.makeText(this,task.getResult().toString(),Toast.LENGTH_LONG).show();
                        }
                    });
        });

    }
}