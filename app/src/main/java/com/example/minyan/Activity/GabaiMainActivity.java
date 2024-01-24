package com.example.minyan.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.relations.OwnSynagoge;
import com.example.minyan.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;


public class GabaiMainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {
    private GoogleMap mMap;
    LatLng centerOfMap;
    Gabai gabai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabai_main);


        EditText addMarkerEditText = findViewById(R.id.addMarkerEditText);
        Button addMarkerButton = findViewById(R.id.addMarkerButton);

        //set up firebase database -> to save if successes signup
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Gabai.GABAI).document(Gabai.GABAI + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                gabai = task.getResult().toObject(Gabai.class);

                // Get the SupportMapFragment and request notification when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert mapFragment != null;
                mapFragment.getMapAsync(this);
            }
            //TODO throw exeption
        });


        addMarkerButton.setOnClickListener(v -> {
            if (mMap != null) {


                MarkerOptions markerOptions = new MarkerOptions()
                        .position(centerOfMap)
                        .title(addMarkerEditText.getText().toString());
                mMap.addMarker(markerOptions);
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.777980242130955, 35.2352939173555), 10f));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(OwnSynagoge.OWN_SYNAGOGE).whereEqualTo("gabai_email", gabai.getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            ArrayList<String> s_ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                s_ids.add(document.getString("s_id"));
                            }
                            if (!s_ids.isEmpty()) {
                                db.collection(Synagoge.SYNAGOGE).whereIn("s_id", s_ids).get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        QuerySnapshot querySnapshot1 = task1.getResult();
                                        if (querySnapshot1 != null) {
                                            for (QueryDocumentSnapshot document : querySnapshot1) {
                                                Synagoge s = document.toObject(Synagoge.class);
                                                MarkerOptions markerOptions = new MarkerOptions()
                                                        .position(new LatLng(s.getLat(), s.getLng()))
                                                        .title(s.getName())
                                                        .icon(getCustomMarkerIcon(R.drawable.ic_marker));
                                                mMap.addMarker(markerOptions);
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    }


                });

    }

    private BitmapDescriptor getCustomMarkerIcon(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,  55,80, false);
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap);
    }
    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        centerOfMap = cameraPosition.target;
    }
}