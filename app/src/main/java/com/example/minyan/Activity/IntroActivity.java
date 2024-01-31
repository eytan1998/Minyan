package com.example.minyan.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minyan.R;

public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        findViewById(R.id.buttonIntroPrayer).setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            intent.putExtra(getString(R.string.kind), getString(R.string.entry_prayer));
            startActivity(intent);
        });
        findViewById(R.id.buttonIntroGabai).setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            intent.putExtra(getString(R.string.kind), getString(R.string.entry_gabai));
            startActivity(intent);
        });

    }

}