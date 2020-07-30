package com.dileep.ecommerce;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mapAPI;
    SupportMapFragment mapFragment;

    private String sellerName;
    private double sellerLatitude, sellerLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);


        sellerLatitude = getIntent().getDoubleExtra("latitude", 0.0);
        sellerLongitude = getIntent().getDoubleExtra("longitude", 0.0);
        sellerName = getIntent().getStringExtra("sellerName");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapAPI = googleMap;

        LatLng SellerLocation = new LatLng(sellerLatitude, sellerLongitude);
        mapAPI.addMarker(new MarkerOptions().position(SellerLocation).title(sellerName));
//        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(Telangana));
        mapAPI.animateCamera(CameraUpdateFactory.newLatLngZoom(SellerLocation,16));
    }
}