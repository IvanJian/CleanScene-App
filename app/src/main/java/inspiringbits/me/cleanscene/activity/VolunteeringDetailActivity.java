package inspiringbits.me.cleanscene.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.adapter.MemberAdapter;
import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.JsonsRootBean;
import inspiringbits.me.cleanscene.model.VolunteeringActivity;
import inspiringbits.me.cleanscene.model.WeatherList;
import inspiringbits.me.cleanscene.model.WeatherRoot;
import inspiringbits.me.cleanscene.rest_service.VolunteerService;
import inspiringbits.me.cleanscene.rest_service.WeatherService;
import inspiringbits.me.cleanscene.tool.DateTimeTool;
import inspiringbits.me.cleanscene.tool.FacebookTool;
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
    public static final String VOLUNTEERING_ACTIVITY_ID = "vid";
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
    GoogleMap googleMap;
    MemberAdapter adapter;
    @BindView(R.id.anonymousm_member)
    TextView anonymousmMember;
    String aId;
    @BindView(R.id.parentLayout)
    CoordinatorLayout parentLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.content)
    View contentScroll;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.mask)
    ImageView mask;
    @BindView(R.id.hint)
    TextView hint;

    private void setDynamicHeight(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 0;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > 4 ){
            x = items/4;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    private void initMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        locationMap.onCreate(mapViewBundle);
        locationMap.getMapAsync(VolunteeringDetailActivity.this);
    }

    private void loadActivityDetail(String idStr) {

        if ("".equals(idStr)) {
            return;
        }
        Observable.create((ObservableOnSubscribe<VolunteeringActivity>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            VolunteerService service = retrofit.create(VolunteerService.class);
            VolunteeringActivity volunteeringActivity = service.getActivityById(idStr).execute().body();
            /*String eventDate=volunteeringActivity.getActivityDate();
            String[] dates=eventDate.split("-");
            String curDate= DateTimeTool.getCurrentDate();
            String[] curDates=curDate.split("-");
            Integer mon=Integer.parseInt(dates[1]);
            Integer curMon=Integer.parseInt(curDates[1]);
            Integer day=Integer.parseInt(dates[2]);
            Integer curDay=Integer.parseInt(curDates[2]);
            Integer offsetMon=mon-curMon;
            Integer offsetDay=day-curDay;
            if (DateTimeTool.compareDate(eventDate,DateTimeTool.getCurrentDate()).equals(DateTimeTool.BEFORE ) || offsetMon<0 || offsetDay<0 || offsetDay>15){
                //weather.setText("Not Available");
            } else {
                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl("http://api.openweathermap.org")
                        .build();
                WeatherService weatherService=retrofit2.create(WeatherService.class);
                String wList=weatherService.getWeatherStr(volunteeringActivity.getLatitude(),volunteeringActivity.getLongitude(),16).execute().body();
                Log.d("json", "loadActivityDetail: "+wList);
                /*JsonsRootBean wList=service1.getWeather(volunteeringActivity.getLatitude(),volunteeringActivity.getLongitude(),16).execute().body();
                Log.d("json", "loadActivityDetail: "+wList.getCod());
                volunteeringActivity.setWeather(wList.getList().get(offsetDay).getWeather().get(0).getMain());
            }
            Gson gson = new Gson();
            Log.d("ac", "loadActivityDetail: " + gson.toJson(volunteeringActivity));*/
            e.onNext(volunteeringActivity);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VolunteeringActivity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull VolunteeringActivity volunteeringActivity) {
                        if (volunteeringActivity == null) {
                            return;
                        }
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(volunteeringActivity.getLatitude(), volunteeringActivity.getLongitude())));
                        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(volunteeringActivity.getLatitude(), volunteeringActivity.getLongitude())));
                        address.setText(volunteeringActivity.getAddress());
                        date.setText(volunteeringActivity.getActivityDate());
                        time.setText(volunteeringActivity.getFromTime() + " - " + volunteeringActivity.getToTime());
                        status.setText(volunteeringActivity.getStatus());
                        /*if (!"".equals(volunteeringActivity.getWeather())){
                            weather.setText(volunteeringActivity.getWeather());
                        }*/
                        invitationLink.setText(getString(R.string.invitation_link_base_url) + "?activity_id=" + volunteeringActivity.getVolunteeringActivityId());
                        Log.d("member", "onNext: " + volunteeringActivity.getMembers().size());
                        adapter = new MemberAdapter(volunteeringActivity.getMembers(), VolunteeringDetailActivity.this);
                        members.setAdapter(adapter);
                        if (adapter.getMembers().size()!=0){
                            setDynamicHeight(members);
                        }
                        adapter.notifyDataSetChanged();
                        Gson gson = new Gson();
                        Log.d("va", "onNext: " + volunteeringActivity.getAnonymousMember());
                        anonymousmMember.setText("Anonymous Member: " + String.valueOf(volunteeringActivity.getAnonymousMember()));
                        mask.setVisibility(View.GONE);
                        hint.setVisibility(View.GONE);
                        /*progressBar.setVisibility(View.GONE);
                        contentScroll.setVisibility(View.VISIBLE);
                        joinFab.setVisibility(View.VISIBLE);*/
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteering_detail);
        ButterKnife.bind(this);
        //parentLayout.setVisibility(View.GONE);
        //contentScroll.setVisibility(View.GONE);
        toolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        toolbarLayout.setExpandedTitleColor(Color.BLACK);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initMap(savedInstanceState);
        String activityId = null;
        try {
            activityId = getIntent().getData().getQueryParameter("activity_id").toString();
        } catch (Exception e) {
        }
        if (activityId != null) {
            aId = activityId;
            volunteeringActivityId = activityId;
            Log.d("adad", "onCreate: ACACAC");
            loadActivityDetail(activityId);
            return;
        }
        volunteeringActivityId = getIntent().getStringExtra(this.VOLUNTEERING_ACTIVITY_ID);
        loadActivityDetail(volunteeringActivityId);
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
        VolunteeringDetailActivity.this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        aId = getIntent().getStringExtra(this.VOLUNTEERING_ACTIVITY_ID);
        loadActivityDetail(aId);
    }

    @OnClick({R.id.copy_link, R.id.drop_out, R.id.join_fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.copy_link:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Invitation Link", invitationLink.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Invitation link has been copied to clipboard", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drop_out:
                if (FacebookTool.isLoggedIn()) {
                    Observable.create((ObservableOnSubscribe<BasicMessage>) e -> {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(getString(R.string.base_url))
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        VolunteerService volunteerService = retrofit.create(VolunteerService.class);
                        BasicMessage message = volunteerService.dropout(VolunteeringDetailActivity.this.aId, FacebookTool.getUserId(VolunteeringDetailActivity.this)).execute().body();
                        e.onNext(message);
                        e.onComplete();
                    })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<BasicMessage>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull BasicMessage basicMessage) {
                                    if (basicMessage.getStatus()) {
                                        Toast.makeText(VolunteeringDetailActivity.this, "You have droped out from this activity", Toast.LENGTH_SHORT).show();
                                        loadActivityDetail(VolunteeringDetailActivity.this.aId);
                                        return;
                                    } else {
                                        Toast.makeText(VolunteeringDetailActivity.this, basicMessage.getContent(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    e.printStackTrace();
                                    Toast.makeText(VolunteeringDetailActivity.this, "Network error, please try later", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    Toast.makeText(this, "Anonymous Member can't drop out.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.join_fab:
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                VolunteerService volunteerService = retrofit.create(VolunteerService.class);
                if (FacebookTool.isLoggedIn()) {
                    Observable.create((ObservableOnSubscribe<BasicMessage>) e -> {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(VolunteeringDetailActivity.this);
                        String userId = sp.getString(MainActivity_2.USER_ID, "");
                        BasicMessage basicMessage = volunteerService.joinVolunteerActivity(VolunteeringDetailActivity.this.aId, userId).execute().body();
                        e.onNext(basicMessage);
                        e.onComplete();
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<BasicMessage>() {

                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull BasicMessage basicMessage) {
                                    if (basicMessage.getStatus()) {
                                        Toast.makeText(VolunteeringDetailActivity.this, "You have joined this activity!", Toast.LENGTH_LONG).show();
                                        VolunteeringDetailActivity.this.refreshMember();
                                    } else {
                                        Toast.makeText(VolunteeringDetailActivity.this, basicMessage.getContent(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String vListStr = sharedPreferences.getString(MyVolunteeringActivity.VOLUNTEERING_LIST, "");
                    Gson gson = new Gson();
                    List<VolunteeringActivity> vList = gson.fromJson(vListStr, new TypeToken<List<VolunteeringActivity>>() {
                    }.getType());
                    if (vList != null && vList.size() != 0) {
                        for (VolunteeringActivity v : vList) {
                            if (v.getVolunteeringActivityId().toString().equals(VolunteeringDetailActivity.this.aId)) {
                                Toast.makeText(this, "You are already a member of this activity.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    builder.setTitle("Join Activity")
                            .setMessage("Do you want to login?")
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(VolunteeringDetailActivity.this, LoginActivity.class));
                                }
                            })
                            .setNegativeButton("Keep Me Anonymous", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    anonymousJoin();
                                }
                            })
                            .show();
                }
                break;
        }
    }

    private void anonymousJoin() {
        Observable.create((ObservableOnSubscribe<BasicMessage>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            VolunteerService volunteerService = retrofit.create(VolunteerService.class);
            BasicMessage basicMessage = volunteerService.joinVolunteeringActivityAnonymous(Integer.parseInt(VolunteeringDetailActivity.this.aId)).execute().body();
            e.onNext(basicMessage);
            e.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BasicMessage>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BasicMessage basicMessage) {
                        if (basicMessage.getStatus()) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(VolunteeringDetailActivity.this);
                            String vListStr = sharedPreferences.getString(MyVolunteeringActivity.VOLUNTEERING_LIST, "");
                            VolunteeringActivity volunteeringActivity = new VolunteeringActivity();
                            volunteeringActivity.setAddress(address.getText().toString());
                            volunteeringActivity.setVolunteeringActivityId(Integer.parseInt(VolunteeringDetailActivity.this.aId));
                            String[] times = time.getText().toString().split("-");
                            volunteeringActivity.setActivityDate(date.getText().toString());
                            volunteeringActivity.setFromTime(times[0]);
                            volunteeringActivity.setToTime(times[1]);
                            Gson gson = new Gson();
                            List<VolunteeringActivity> vList = gson.fromJson(vListStr, new TypeToken<List<VolunteeringActivity>>() {
                            }.getType());
                            if (vList == null) {
                                vList = new ArrayList<VolunteeringActivity>();
                            }
                            vList.add(volunteeringActivity);
                            vListStr = gson.toJson(vList);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(MyVolunteeringActivity.VOLUNTEERING_LIST, vListStr);
                            editor.apply();
                            Toast.makeText(VolunteeringDetailActivity.this, "You have joined this activity.", Toast.LENGTH_SHORT).show();
                            refreshMember();
                        } else {
                            Toast.makeText(VolunteeringDetailActivity.this, basicMessage.getContent(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        //Toast.makeText(VolunteeringDetailActivity.this, "Network error. Please try later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void refreshMember() {
        /*progressBar.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);
        joinFab.setVisibility(View.GONE);*/
        loadActivityDetail(this.aId);
    }
}
