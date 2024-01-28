package com.example.minyan.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ManageSynagogeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    LatLng centerOfMap;

    Gabai currentGabai;
    Synagoge currentSynagoge;
    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_synagogue);


        EditText EditTextManageSynagogeName = findViewById(R.id.EditTextManageSynagogeName);
        Button buttonManageSynagogeSave = findViewById(R.id.buttonManageSynagogeSave);
        Button buttonManageSynagogeDelete = findViewById(R.id.buttonManageSynagogeDelete);
        Button buttonManageSynagogeEdit = findViewById(R.id.buttonManageSynagogeEdit);

        //to get currnet gabai
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Gabai.GABAI).document(Gabai.GABAI + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentGabai = task.getResult().toObject(Gabai.class);

                        // set the map only after get the gabai
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.mapManageSynagoge);
                        assert mapFragment != null;
                        mapFragment.getMapAsync(this);
                    }
                    //TODO throw exeption
                });


        buttonManageSynagogeSave.setOnClickListener(v -> {
            if (mMap != null) {
                //add synagoge
                Synagoge synagoge = new Synagoge(EditTextManageSynagogeName.getText().toString(), Nosah.ALL, centerOfMap.latitude, centerOfMap.longitude, getAddressFromLocation(ManageSynagogeActivity.this, centerOfMap.latitude, centerOfMap.longitude));
                String s_id = currentGabai.addSynagoge(synagoge);

                //add marker
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(centerOfMap)
                        .title(EditTextManageSynagogeName.getText().toString())
                        .snippet(s_id)//the way to connect synagogue to marker
                        .icon(getCustomMarkerIcon(R.drawable.ic_marker));
                mMap.addMarker(markerOptions);

                //so can edit/del this synagogue
                currentSynagoge = synagoge;
            }
        });
        // delete the current synagogue
        buttonManageSynagogeDelete.setOnClickListener(v -> {
            //todo ask with dialog
            if (mMap != null) {
                if (currentSynagoge != null) {
                    currentGabai.delSynagoge(currentSynagoge);
                    currentMarker.remove();
                    EditTextManageSynagogeName.setText("");
                    currentSynagoge = null;
                    currentMarker = null;
                }
            }
        });
        //move the synagogue id to the edit Synagogue so will the synagogue
        buttonManageSynagogeEdit.setOnClickListener(v -> {
            if (mMap != null) {
                if (currentSynagoge != null) {
                    Intent intent = new Intent(ManageSynagogeActivity.this, EditSynagogeActivity.class);
                    intent.putExtra(Synagoge.SYNAGOGE, currentSynagoge.getS_id());
                    startActivity(intent);
                }
            }
        });


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //so can access the map from other
        mMap = googleMap;
        //set SATELLITE with names
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //set start location at jerusalem
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.777980242130955, 35.2352939173555), 10f));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //get the center of map position
        mMap.setOnCameraIdleListener(() -> {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            centerOfMap = cameraPosition.target;
        });

        //so get update current synagogue if click on correspond marker
        mMap.setOnMarkerClickListener(marker -> {
            String s_id = marker.getSnippet();
            db.collection(Synagoge.SYNAGOGE).document(s_id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentMarker = marker;
                    currentSynagoge = task.getResult().toObject(Synagoge.class);
                    ((TextView) findViewById(R.id.EditTextManageSynagogeName)).setText(currentSynagoge.getName());
                }
            });
            //so the default layout wont pop out
            // can custom in futer
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


        //get all the synagogue that gabai have
        db.collection(OwnSynagoge.OWN_SYNAGOGE).whereEqualTo("gabai_email", currentGabai.getEmail())
                .get().addOnCompleteListener(getAllSynagogueOfGabai -> {
                    if (getAllSynagogueOfGabai.isSuccessful()) {
                        QuerySnapshot querySnapshot = getAllSynagogueOfGabai.getResult();
                        if (querySnapshot != null) {
                            ArrayList<String> s_ids = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                s_ids.add(document.getString("s_id"));
                            }
                            if (!s_ids.isEmpty()) {
                                db.collection(Synagoge.SYNAGOGE).whereIn("s_id", s_ids).get().addOnCompleteListener(getAllTheSynagogueTask -> {
                                    if (getAllTheSynagogueTask.isSuccessful()) {
                                        QuerySnapshot querySnapshot1 = getAllTheSynagogueTask.getResult();
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

    //to add custom marker icon
    private BitmapDescriptor getCustomMarkerIcon(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 55, 80, false);
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap);
    }

    //on save location get the address
    private String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0); // You can get other address details as needed

                return fullAddress;
                // Use the obtained address
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}