package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.VolunteeringActivity;
import inspiringbits.me.cleanscene.model.VolunteeringRecommendation;
import inspiringbits.me.cleanscene.rest_service.VolunteerService;
import inspiringbits.me.cleanscene.tool.DateTimeTool;
import inspiringbits.me.cleanscene.tool.FacebookTool;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewVolunteeringActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "NewVolunteering";
    @BindView(R.id.mapView)
    MapView mMapView;
    FusedLocationProviderClient mFusedLocationClient;
    Marker marker;
    @BindView(R.id.header)
    ConstraintLayout headerLayout;
    @BindView(R.id.address1)
    TextView address1;
    @BindView(R.id.address2)
    TextView address2;
    @BindView(R.id.more_info)
    TextView moreInfo;
    BottomSheetBehavior bsb;
    @BindView(R.id.event_date)
    EditText eventDate;
    @BindView(R.id.start_time)
    EditText startTime;
    @BindView(R.id.end_time)
    EditText endTime;
    @BindView(R.id.is_private)
    CheckBox isPrivate;
    @BindView(R.id.submit)
    Button submmit;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.go)
    Button go;
    @BindView(R.id.recommendation_card)
    CardView recommendationCard;
    private LatLng recommendationLatlng;
    GoogleMap googleMap;

    @OnClick(R.id.more_info)
    void showDetail(View v) {
        if (bsb.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setDateTimePickerDialog() {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat(DateTimeTool.DATE_FORMAT);
            eventDate.setText(sdf.format(myCalendar.getTime()));
        };
        eventDate.setOnClickListener(v -> new DatePickerDialog(NewVolunteeringActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewVolunteeringActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Start Time");
                mTimePicker.show();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewVolunteeringActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("End Time");
                mTimePicker.show();
            }
        });
    }

    private void getRecommendation() {
        Observable.create((ObservableOnSubscribe<VolunteeringRecommendation>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            VolunteerService volunteerService = retrofit.create(VolunteerService.class);
            VolunteeringRecommendation vR = volunteerService.getRecommendation().execute().body();
            e.onNext(vR);
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<VolunteeringRecommendation>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull VolunteeringRecommendation volunteeringRecommendation) {
                        title.setText(volunteeringRecommendation.getTitle());
                        recommendationCard.setVisibility(View.VISIBLE);
                        recommendationLatlng = new LatLng(volunteeringRecommendation.getLatitude(), volunteeringRecommendation.getLongitude());
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        return;
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_volunteering);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E97DC")));
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.status_bar));
        ButterKnife.bind(this);
        setDateTimePickerDialog();
        recommendationCard.setVisibility(View.GONE);
        bsb = BottomSheetBehavior.from(findViewById(R.id.my_design_bottom_sheet));
        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
        ViewTreeObserver observer = headerLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int headerLayoutHeight = headerLayout.getHeight();
                bsb.setPeekHeight(headerLayoutHeight);
                headerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
            }
        });
        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        moreInfo.setText("SHOW MAP");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        moreInfo.setText("CREATE HERE");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

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
        getRecommendation();
        this.googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectLocation(latLng, map);
            }
        });
    }

    private void selectLocation(LatLng latLng, GoogleMap map) {
        if (marker != null) {
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions().position(latLng));
        //Log.d("location", "onMapLongClick: " + getLocationInformation(latLng));
        Observable.create((ObservableOnSubscribe<String>) e -> {
            String address = getLocationInformation(latLng);
            e.onNext(address);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        if (s.equals("Unknown Address")) {
                            address2.setText("Unknown");
                            address1.setText("Unknown");
                        } else {
                            String[] addr = s.split(", ");
                            address1.setText(addr[0]);
                            address2.setText(addr[1]);
                        }
                        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private String getLocationInformation(LatLng latLng) {
        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.isEmpty()) {
                return "Unknown Address";
            } else {
                if (addresses.size() > 0) {
                    return addresses.get(0).getAddressLine(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Address";
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

    @OnClick(R.id.submit)
    public void submit() {
        submmit.setClickable(false);
        submmit.setBackgroundColor(Color.GRAY);
        if (!FacebookTool.isLoggedIn()) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            submmit.setClickable(true);
            submmit.setBackgroundColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(NewVolunteeringActivity.this, R.color.material_blue))));
            return;
        }
        if ("".equals(eventDate.getText().toString()) || "".equals(startTime.getText().toString()) || "".equals(endTime.getText().toString()) ){
            Toast.makeText(this, "Please enter the detail of the activity", Toast.LENGTH_LONG).show();
            submmit.setClickable(true);
            submmit.setBackgroundColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(NewVolunteeringActivity.this, R.color.material_blue))));
            return;
        }
        String dateStr = eventDate.getText().toString();
        String fromTimeStr = startTime.getText().toString() + ":00";
        String endTimeStr = endTime.getText().toString() + ":00";
        try {
            if (DateTimeTool.compareDate(dateStr, DateTimeTool.getCurrentDate()).equals(DateTimeTool.BEFORE) || DateTimeTool.compareDateTime(dateStr, fromTimeStr, dateStr, endTimeStr).equals(DateTimeTool.AFTER)) {
                Toast.makeText(this, "Invalid date and time", Toast.LENGTH_LONG).show();
                submmit.setClickable(true);
                submmit.setBackgroundColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(NewVolunteeringActivity.this, R.color.material_blue))));
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        VolunteeringActivity volunteeringActivity = new VolunteeringActivity();
        volunteeringActivity.setActivityDate(dateStr);
        volunteeringActivity.setFromTime(fromTimeStr);
        volunteeringActivity.setToTime(endTimeStr);
        volunteeringActivity.setLatitude(marker.getPosition().latitude);
        volunteeringActivity.setLongitude(marker.getPosition().longitude);
        volunteeringActivity.setPrivate(isPrivate.isChecked());
        volunteeringActivity.setAddress(address1.getText() + ", " + address2.getText());
        Observable.create((ObservableOnSubscribe<BasicMessage>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            VolunteerService service = retrofit.create(VolunteerService.class);
            BasicMessage basicMessage = service.createActivity(volunteeringActivity).execute().body();
            if (basicMessage.getStatus()) {
                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl(getString(R.string.base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                VolunteerService service2 = retrofit2.create(VolunteerService.class);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewVolunteeringActivity.this);
                String userId = sharedPreferences.getString(MainActivity_2.USER_ID, "");
                Log.d("damn", "submit: " + basicMessage.getContent() + userId);
                BasicMessage message = null;
                try {
                    message = service2.joinActivity(basicMessage.getContent(), userId).execute().body();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.d("damn", "submit: " + message.getCode()+message.getStatus()+message.getCode());
                message.setContent(basicMessage.getContent());
                Log.d("damn", "submit: " + basicMessage.getContent() + userId);
                e.onNext(message);
            } else {
                return;
            }
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BasicMessage>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        //progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull BasicMessage basicMessage) {
                        if (basicMessage.getStatus()) {
                            Intent intent = new Intent(NewVolunteeringActivity.this, VolunteeringDetailActivity.class);
                            intent.putExtra(VolunteeringDetailActivity.VOLUNTEERING_ACTIVITY_ID, basicMessage.getContent());
                            Log.d("fuck", "onNext: "+basicMessage.getContent());
                            startActivity(intent);
                            NewVolunteeringActivity.this.finish();
                        } else {
                            Toast.makeText(NewVolunteeringActivity.this,"Invalid date and time.",Toast.LENGTH_SHORT).show();
                            submmit.setClickable(true);
                            submmit.setBackgroundColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(NewVolunteeringActivity.this, R.color.material_blue))));

                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        submmit.setClickable(true);
                        submmit.setBackgroundColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(NewVolunteeringActivity.this, R.color.material_blue))));
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @OnClick(R.id.go)
    public void onViewClicked() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(recommendationLatlng));
        selectLocation(recommendationLatlng, googleMap);
        recommendationCard.setVisibility(View.GONE);
    }

    @OnClick(R.id.dismiss)
    public void dismiss() {
        recommendationCard.setVisibility(View.GONE);
    }
}
