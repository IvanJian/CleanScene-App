package inspiringbits.me.cleanscene.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.adapter.MemberDummyAdapter;

public class VolunteeringDetailActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String MAPVIEW_BUNDLE_KEY = "VolunteeringDetail";
    @BindView(R.id.location_map)
    MapView locationMap;
    @BindView(R.id.members)
    GridView members;

    private void initMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        locationMap.onCreate(mapViewBundle);
        locationMap.getMapAsync(VolunteeringDetailActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteering_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initMap(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /**
         * TEST
         */
        members.setAdapter(new MemberDummyAdapter(this));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        locationMap.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationMap.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationMap.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationMap.onStop();
    }

    @Override
    protected void onPause() {
        locationMap.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        locationMap.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        locationMap.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(-37.821202, 144.957946))
                .title("Location of Incident"));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-37.821202, 144.957946)));
    }
}
