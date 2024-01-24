package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;

public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //TODO make pretty flat vector art with fancy button
        findViewById(R.id.buttonIntroPrayer).setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            intent.putExtra("KIND", Prayer.PRAYER);
            startActivity(intent);
        });
        findViewById(R.id.buttonIntroGabai).setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            intent.putExtra("KIND", Gabai.GABAI);
            startActivity(intent);
        });

    }

}