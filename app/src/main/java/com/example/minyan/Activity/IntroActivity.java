package com.example.minyan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.minyan.R;

public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //setup animation
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        ((ImageView) findViewById(R.id.imageViewIntroMarker)).startAnimation(anim);


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