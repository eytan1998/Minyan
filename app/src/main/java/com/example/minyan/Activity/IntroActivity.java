package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.minyan.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        Button button = findViewById(R.id.button);

        button.setOnLongClickListener(v -> {
            myRef.child("root").child("gabai").child("eytan1998@gmail.com").setValue("Long");
            return true;
        });

        button.setOnClickListener(v -> {
//            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
//            startActivity(intent);
            myRef.child("eitan").setValue("Short");


        });


    }

}