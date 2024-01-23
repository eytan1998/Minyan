package com.example.minyan.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.minyan.R;

public class AskGabaiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_gabai);

        Button buttonAskGabaiPrayer = findViewById(R.id.buttonAskGabaiPrayer);
        Button buttonAskGabaiGabai = findViewById(R.id.buttonAskGabaiGabai);

        buttonAskGabaiGabai.setOnClickListener(v -> {
            Intent intent = new Intent(AskGabaiActivity.this, GabaiMainActivity.class);
            startActivity(intent);
        });
        buttonAskGabaiPrayer.setOnClickListener(v -> {
            Intent intent = new Intent(AskGabaiActivity.this, PrayerMainActivity.class);
            startActivity(intent);
        });
    }
}