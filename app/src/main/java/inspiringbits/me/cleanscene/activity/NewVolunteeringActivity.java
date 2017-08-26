package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.githang.statusbar.StatusBarCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;

public class NewVolunteeringActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "NewVolunteering";
    @BindView(R.id.mapView)
    MapView mMapView;
    FusedLocationProviderClient mFusedLocationClient;
    Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_volunteering);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E97DC")));
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.status_bar));
        ButterKnife.bind(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-37.821202,144.957946)));
            return;
        }
        map.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    LatLng myLatlng;
                    if (location != null) {
                        myLatlng=new LatLng(location.getLatitude(),location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                        map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                    } else{
                        myLatlng=new LatLng(-37.821202,144.957946);
                        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                        map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                    }
                });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (marker!=null){
                    marker.remove();
                }
                marker=map.addMarker(new MarkerOptions().position(latLng));
                Log.d("location", "onMapLongClick: "+getLocationInformation(latLng));
            }
        });
    }

    private String getLocationInformation(LatLng latLng){
        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.isEmpty()) {
                return "No available address.";
            }
            else {
                if (addresses.size() > 0) {
                    String[] addr=addresses.get(0).getAddressLine(0).split(",");
                    return addr[0]+", "+addr[1];
                    //return addresses.get(0).getFeatureName() + ", " +addresses.get(0).getThoroughfare()+", "+ addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "No available address.";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
