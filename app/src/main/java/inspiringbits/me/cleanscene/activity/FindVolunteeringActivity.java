package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.githang.statusbar.StatusBarCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.VolunteeringActivity;
import inspiringbits.me.cleanscene.rest_service.VolunteerService;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindVolunteeringActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "find_volunteer";
    @BindView(R.id.mapView2)
    MapView mMapView;
    FusedLocationProviderClient mFusedLocationClient;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_volunteering);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E97DC")));
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.status_bar));
        ButterKnife.bind(this);
        mMapView.setVisibility(View.GONE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap map) {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(FindVolunteeringActivity.this, VolunteeringDetailActivity.class);
                VolunteeringActivity v = (VolunteeringActivity) marker.getTag();
                intent.putExtra(VolunteeringDetailActivity.VOLUNTEERING_ACTIVITY_ID, v.getVolunteeringActivityId().toString());
                startActivity(intent);
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-37.821202, 144.957946)));
            return;
        }
        map.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    LatLng myLatlng;
                    if (location != null) {
                        myLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
                        map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                    } else {
                        myLatlng = new LatLng(-37.821202, 144.957946);
                        map.moveCamera(CameraUpdateFactory.zoomTo(12.0f));
                        map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                    }
                });
        Log.d("list", "onMapReady: 1");
        Observable.create((ObservableOnSubscribe<List<VolunteeringActivity>>) e -> {
            Log.d("list", "onMapReady: ");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            VolunteerService volunteerService = retrofit.create(VolunteerService.class);
            List<VolunteeringActivity> volunteeringActivityList = new ArrayList<VolunteeringActivity>();
            try {
                volunteeringActivityList = volunteerService.getAllVolunteerActivity().execute().body();
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
            Log.d("list", "onMapReady: 2");
            if (volunteeringActivityList == null || volunteeringActivityList.size() == 0) {
                e.onComplete();
                return;
            }
            e.onNext(volunteeringActivityList);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VolunteeringActivity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<VolunteeringActivity> volunteeringActivities) {
                        for (VolunteeringActivity activity : volunteeringActivities) {
                            LatLng latLng = new LatLng(activity.getLatitude(), activity.getLongitude());
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                    .title(activity.getActivityDate())
                                    .snippet("From: " + activity.getFromTime() + "\nTo: " + activity.getToTime()));
                            marker.setTag(activity);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        progressBar.setVisibility(View.GONE);
                        mMapView.setVisibility(View.VISIBLE);
                    }
                });
    }

}
