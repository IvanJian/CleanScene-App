package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.NestedMapView;
import inspiringbits.me.cleanscene.R;

public class NewReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.new_report_pollution_type_spinner)
    Spinner typeSpinner;
    @BindView(R.id.new_report_rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.rating_label)
    TextView ratingLabel;
    @BindView(R.id.new_report_source)
    Spinner sourceSpinner;
    @BindView(R.id.new_report_location_map)
    NestedMapView locationMap;
    @BindView(R.id.new_report_map_father)
    RelativeLayout mapFather;

    @BindView(R.id.new_report_scrollview)
    ScrollView hsv;

    Marker locationMarker;
    static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);
        ButterKnife.bind(this);
        Bundle mapViewBundle = null;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        locationMap.onCreate(mapViewBundle);
        locationMap.getMapAsync(this);

        //change the rating label when the rating bar is changing
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (rating < 1.0f) {
                ratingBar1.setRating(1.0f);
            }
            if (rating == 1.0f) {
                ratingLabel.setText("Low");
                ratingLabel.setTextColor(Color.parseColor("#66ff4081"));
            }
            if (rating == 2.0f) {
                ratingLabel.setText("Medium");
                ratingLabel.setTextColor(Color.parseColor("#FF6600"));
            }
            if (rating == 3.0f) {
                ratingLabel.setText("High");
                ratingLabel.setTextColor(Color.parseColor("#FF0033"));
            }
        });

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
    public void onMapReady(GoogleMap map) {
        //check the location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(-37.821202,144.957946))
                    .title("Location of Incident")
                    .draggable(true));
            map.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-37.821202,144.957946)));
            Toast.makeText(this, R.string.location_hint,Toast.LENGTH_LONG).show();
            return;
        }
        map.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng myLatlng;
                        if (location != null) {
                            myLatlng=new LatLng(location.getLatitude(),location.getLongitude());
                            locationMarker = map.addMarker(new MarkerOptions()
                                    .position(myLatlng)
                                    .title("Location of Incident")
                                    .draggable(true));
                            map.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
                            map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                        } else{
                            myLatlng=new LatLng(-37.821202,144.957946);
                            locationMarker = map.addMarker(new MarkerOptions()
                                    .position(myLatlng)
                                    .title("Location of Incident")
                                    .draggable(true));
                            map.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
                            map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                        }
                    }
                });
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

}
