package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.google.android.flexbox.FlexboxLayout;
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
import com.google.gson.Gson;
import com.tr4android.support.extension.widget.FlexibleToolbarLayout;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
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
    @BindView(R.id.new_report)
    Button newReport;
    @BindView(R.id.num_of_report)
    TextView numOfReport;
    @BindView(R.id.search)
    TextView search;
    PopupWindow filterWindow;
    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;

    FlexboxLayout ratingGroup;
    FlexboxLayout typeGroup;
    EditText postcode;
    Button filterBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports_on_map);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E97DC")));
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.status_bar));
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

    private void setTags() {
        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(Color.parseColor("#ddaa00"))
                .checkedTextColor(Color.parseColor("#ffffff"))
                .uncheckedChipColor(Color.parseColor("#efefef"))
                .uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true);
        ChipCloud ratings = new ChipCloud(this,ratingGroup,config);
        String[] ratingArray = {"Low","Medium","High"};
        ratings.addChips(ratingArray);
        ChipCloud types = new ChipCloud(this,typeGroup,config);
        types.addChips(getResources().getStringArray(R.array.pollution_type));
    }

    @Override
    public void onMapReady(GoogleMap map) {
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
                        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                        map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                    } else {
                        myLatlng = new LatLng(-37.821202, 144.957946);
                        map.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                        map.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                    }
                });
        GetReportList getReportList = new GetReportList();
        getReportList.execute();
        this.map = map;
        map.setOnMarkerClickListener(marker -> {
            Intent intent = new Intent(ViewReportsOnMapActivity.this, ReportDetailActivity.class);
            intent.putExtra("reportId", (String) marker.getTag());
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

    @OnClick(R.id.new_report)
    public void onViewClicked() {
        startActivity(new Intent(this, NewReportActivity_2.class));
        this.finish();
    }

    @OnClick(R.id.search)
    public void showFilter() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.report_filter_window,
                (ViewGroup) findViewById(R.id.report_filter));
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        filterWindow = new PopupWindow(layout, width, height, focusable);
        ratingGroup = (FlexboxLayout) layout.findViewById(R.id.rating_group);
        typeGroup = (FlexboxLayout) layout.findViewById(R.id.type_group);
        postcode = (EditText) layout.findViewById(R.id.postcode);
        filterBtn = (Button) layout.findViewById(R.id.apply_filter); 
        filterBtn.setOnClickListener(v->{
            applyFilter();
        });
        setTags();
        filterWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
    }

    private void applyFilter() {
    }

    private class GetReportList extends AsyncTask<Void, Void, List<ReportModel>> {

        @Override
        protected List<ReportModel> doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ReportService reportService = retrofit.create(ReportService.class);
            try {
                List<ReportModel> reports = reportService.getAllReort().execute().body();
                Gson gson = new Gson();
                Log.d("reports", "doInBackground: " + gson.toJson(reports));
                return reports;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ViewReportsOnMapActivity.this, "Network error. Try later", Toast.LENGTH_LONG).show();
                ViewReportsOnMapActivity.this.finish();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ReportModel> reportModels) {
            if (reportModels.size() == 0) {
                mMapView.setVisibility(View.VISIBLE);
                pbar.setVisibility(View.GONE);
                return;
            }
            for (ReportModel report : reportModels) {
                LatLng latlng = new LatLng(report.getLatitude(), report.getLongitude());
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                marker.setTag(report.getReportId().toString());
            }
            numOfReport.setText(reportModels.size()+" Reports Found");
            mMapView.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
        }
    }
}
