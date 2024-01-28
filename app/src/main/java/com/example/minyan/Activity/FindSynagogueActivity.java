package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.minyan.Objects.Synagoge;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FindSynagogueActivity extends AppCompatActivity implements OnMapReadyCallback {

    //so can change map from other function
    private GoogleMap mMap;
    //for get search by distance
    LatLng centerOfMap;

    //so can go to ViewSynagogue
    Synagoge currentSynagoge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_synagoge);

        Button buttonFindSynagogueViewSynagogue = findViewById(R.id.buttonFindSynagogueViewSynagogue);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFindSynagoge);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        buttonFindSynagogueViewSynagogue.setOnClickListener(v -> {
            if (mMap != null) {
                if (currentSynagoge != null) {
                    Intent intent = new Intent(FindSynagogueActivity.this, ViewSynagogeActivity.class);
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
                    currentSynagoge = task.getResult().toObject(Synagoge.class);
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
                    View view = LayoutInflater.from(FindSynagogueActivity.this).inflate(R.layout.custom_info_window, null);
                    //TODO if want make custom info layout
//                    TextView titleTextView = view.findViewById(R.id.titleTextView);
//                    titleTextView.setText(marker.getTitle());

                    return view;
                }
            });

            return false;
        });

        db.collection(Synagoge.SYNAGOGE).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Synagoge s = document.toObject(Synagoge.class);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(s.getLat(), s.getLng()))
                            .title(s.getName())
                            .icon(getCustomMarkerIcon(R.drawable.ic_marker))
                            .snippet(s.getS_id());
                    mMap.addMarker(markerOptions);
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

}