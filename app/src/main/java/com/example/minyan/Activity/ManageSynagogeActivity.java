package com.example.minyan.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.enums.Nosah;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class ManageSynagogeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    LatLng centerOfMap;
    Gabai gabai;
    Synagoge currentSynagoge;
    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabai_main);


        EditText addMarkerEditText = findViewById(R.id.addMarkerEditText);
        Button addMarkerButton = findViewById(R.id.addMarkerButton);
        Button buttonDEl = findViewById(R.id.buttonDEl);
        Button buttonEdit = findViewById(R.id.buttonEdit);

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

                Synagoge synagoge = new Synagoge(addMarkerEditText.getText().toString(), Nosah.ALL, centerOfMap.latitude, centerOfMap.longitude);
                String s_id = gabai.addSynagoge(synagoge);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(centerOfMap)
                        .title(addMarkerEditText.getText().toString())
                        .snippet(s_id)
                        .icon(getCustomMarkerIcon(R.drawable.ic_marker));
                mMap.addMarker(markerOptions);
                currentSynagoge = synagoge;
            }
        });
        buttonDEl.setOnClickListener(v -> {
            //todo ask with dialog
            if (mMap != null) {
                if (currentSynagoge != null) {
                    gabai.delSynagoge(currentSynagoge);
                    currentMarker.remove();
                    addMarkerEditText.setText("");
                    currentSynagoge = null;
                    currentMarker = null;
                }
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    if (currentSynagoge != null) {
                        Intent intent = new Intent(ManageSynagogeActivity.this, EditSynagogeActivity.class);
                        intent.putExtra(Synagoge.SYNAGOGE, currentSynagoge.getS_id());
                        startActivity(intent);
                    }
                }
            }
        });


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mMap = googleMap;
        mMap.setOnCameraIdleListener(() -> {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            centerOfMap = cameraPosition.target;
        });

        mMap.setOnMarkerClickListener(marker -> {
            String s_id = marker.getSnippet();
            db.collection(Synagoge.SYNAGOGE).document(s_id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentMarker = marker;
                    currentSynagoge = task.getResult().toObject(Synagoge.class);
                    ((TextView) findViewById(R.id.addMarkerEditText)).setText(currentSynagoge.getName());
                }
            });
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Nullable
                @Override
                public View getInfoContents(@NonNull Marker marker) {
                    return null;
                }

                @Nullable
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    View view = LayoutInflater.from(ManageSynagogeActivity.this).inflate(R.layout.custom_info_window, null);
                    //TODO if want make custom info layout
//                    TextView titleTextView = view.findViewById(R.id.titleTextView);
//                    titleTextView.setText(marker.getTitle());

                    return view;
                }
            });

            return false;
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.777980242130955, 35.2352939173555), 10f));

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
                                                        .icon(getCustomMarkerIcon(R.drawable.ic_marker))
                                                        .snippet(s.getS_id());
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
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 55, 80, false);
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap);
    }


}