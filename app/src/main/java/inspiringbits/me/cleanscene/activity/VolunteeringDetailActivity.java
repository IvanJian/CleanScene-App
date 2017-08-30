package inspiringbits.me.cleanscene.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.adapter.MemberDummyAdapter;
import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.rest_service.UserService;
import inspiringbits.me.cleanscene.rest_service.VolunteerService;
import inspiringbits.me.cleanscene.tool.FacebookTool;
import inspiringbits.me.cleanscene.tool.RestServiceTool;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteeringDetailActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String MAPVIEW_BUNDLE_KEY = "VolunteeringDetail";
    private static final String VOLUNTEERING_ACTIVITY_ID = "vid";
    @BindView(R.id.location_map)
    MapView locationMap;
    @BindView(R.id.members)
    GridView members;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.invitation_link)
    TextView invitationLink;
    @BindView(R.id.copy_link)
    TextView copyLink;
    @BindView(R.id.drop_out)
    Button dropOut;
    @BindView(R.id.join_fab)
    FloatingActionButton joinFab;
    String volunteeringActivityId;

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
        //volunteeringActivityId=getIntent().getStringExtra(this.VOLUNTEERING_ACTIVITY_ID);
        loadVolunteerInformation();
        /**
         * TEST
         */
        members.setAdapter(new MemberDummyAdapter(this));
    }

    private void loadVolunteerInformation() {
        //TODO
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

    @OnClick({R.id.copy_link, R.id.drop_out, R.id.join_fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.copy_link:
                break;
            case R.id.drop_out:
                break;
            case R.id.join_fab:
                VolunteerService volunteerService= RestServiceTool.getRetrofit().create(VolunteerService.class);

                    Observable.create((ObservableOnSubscribe<BasicMessage>) e -> {
                        if (FacebookTool.isLoggedIn()) {
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(VolunteeringDetailActivity.this);
                            String userId = sp.getString(MainActivity_2.USER_ID, "");
                            BasicMessage basicMessage = volunteerService.joinVolunteerActivity(VolunteeringDetailActivity.this.volunteeringActivityId, userId).execute().body();
                            e.onNext(basicMessage);
                            e.onComplete();
                        } else {
                            //TODO
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BasicMessage>() {

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull BasicMessage basicMessage) {
                            if (basicMessage.getStatus()){
                                Toast.makeText(VolunteeringDetailActivity.this,"You have joined this activity!",Toast.LENGTH_LONG).show();
                                VolunteeringDetailActivity.this.refreshMember();
                            } else {
                                Toast.makeText(VolunteeringDetailActivity.this,basicMessage.getContent(),Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                break;
        }
    }

    private void refreshMember() {
        //TODO
    }
}
