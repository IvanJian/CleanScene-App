package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.ReportModel;
import inspiringbits.me.cleanscene.rest_service.ReportService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewReportsOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "ViewReportOnMap";
    @BindView(R.id.report_map_mapview)
    MapView mMapView;
    @BindView(R.id.report_on_map_pbar)
    ProgressBar pbar;
    GoogleMap map;
    FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports_on_map);
        ButterKnife.bind(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.report_map_mapview);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        mMapView.setVisibility(View.GONE);
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
        GetReportList getReportList=new GetReportList();
        getReportList.execute();
        this.map=map;
        map.setOnMarkerClickListener(marker -> {
            Intent intent=new Intent(ViewReportsOnMapActivity.this,ReportDetailActivity.class);
            intent.putExtra("reportId",(String)marker.getTag());
            startActivity(intent);
            return true;
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

    private class GetReportList extends AsyncTask<Void,Void,List<ReportModel>>{

        @Override
        protected List<ReportModel> doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ReportService reportService=retrofit.create(ReportService.class);
            try {
                List<ReportModel> reports=reportService.getAllReort().execute().body();
                Gson gson=new Gson();
                Log.d("reports", "doInBackground: "+gson.toJson(reports));
                return reports;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ViewReportsOnMapActivity.this,"Network error. Try later",Toast.LENGTH_LONG).show();
                ViewReportsOnMapActivity.this.finish();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ReportModel> reportModels) {
            if (reportModels.size()==0){
                mMapView.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
                return;
            }
            for (ReportModel report:reportModels){
                LatLng latlng=new LatLng(report.getLatitude(),report.getLongitude());
                Marker marker=map.addMarker(new MarkerOptions()
                        .position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                marker.setTag(report.getReportId().toString());
            }
            mMapView.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
        }
    }
}
