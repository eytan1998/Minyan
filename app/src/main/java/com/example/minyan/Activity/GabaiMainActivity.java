package com.example.minyan.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.enums.Nosah;
import com.example.minyan.Objects.relations.OwnSynagoge;
import com.example.minyan.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

import java.util.Objects;
import java.util.UUID;


public class GabaiMainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {
    private GoogleMap mMap;
    LatLng centerOfMap;
    Gabai gabai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabai_main);
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        EditText addMarkerEditText = findViewById(R.id.addMarkerEditText);
        Button addMarkerButton = findViewById(R.id.addMarkerButton);

        //set up firebase database -> to save if successes signup
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Gabai.GABAI).document(Gabai.GABAI + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                gabai = task.getResult().toObject(Gabai.class);
            }
            //TODO throw exeption
        });


        addMarkerButton.setOnClickListener(v -> {
            if (mMap != null) {


                MarkerOptions markerOptions = new MarkerOptions()
                        .position(centerOfMap)
                        .title(addMarkerEditText.getText().toString());
                Marker centerMarker = mMap.addMarker(markerOptions);
                //save the synagoge

                Synagoge synagoge = new Synagoge(addMarkerEditText.getText().toString(), 0, null, centerOfMap.latitude, centerOfMap.longitude);
                gabai.addSynagoge(synagoge);
            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Synagoge.SYNAGOGE).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Synagoge s = document.toObject(Synagoge.class);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(s.getLat(),s.getLng()))
                            .title(s.getName());
                    mMap.addMarker(markerOptions);
                }
            }
        });

    }

    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        centerOfMap = cameraPosition.target;
    }
}