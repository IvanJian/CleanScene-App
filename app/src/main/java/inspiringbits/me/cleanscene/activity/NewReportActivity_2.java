package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.ImageUpload;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.AnonymousUserModel;
import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.ReportModel;
import inspiringbits.me.cleanscene.view.NestedMapView;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewReportActivity_2 extends AppCompatActivity implements OnMapReadyCallback {

    static final int RESULT_LOAD_IMG = 1;
    static final int MY_PERMISSIONS_REQUEST_STORAGE = 2;
    static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
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
    @BindView(R.id.new_report_add_photo_img)
    ImageView addPhotoImg;
    @BindView(R.id.new_report_scrollview)
    ScrollView hsv;
    @BindView(R.id.new_report_photo1)
    SimpleDraweeView photo1;
    @BindView(R.id.new_report_photo2)
    SimpleDraweeView photo2;
    @BindView(R.id.new_report_photo3)
    SimpleDraweeView photo3;
    @BindView(R.id.new_report_submit)
    Button submitBtn;
    @BindView(R.id.new_report_description_txt)
    EditText description;
    @BindView(R.id.new_report_more_detail_cb)
    CheckBox moreDetailCb;
    @BindView(R.id.new_report_detail_layout)
    ConstraintLayout detailLayout;
    @BindView(R.id.progressBar2)
    ProgressBar pBar;
    @BindView(R.id.new_report_mask)
    ImageView maskImg;


    Marker locationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    List<Uri> selectedPhotos=new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report_2);
        ButterKnife.bind(this);
        maskImg.setOnClickListener(v->{
            return;
        });
        mapInit(savedInstanceState);
        changeRatingText();
        photoSelection();
        submitReport();
    }

    private BasicMessage uploadPhoto(int index) throws IOException {
        Uri uri=selectedPhotos.get(index);
        final InputStream imageStream = getContentResolver().openInputStream(uri);
        final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        BasicMessage imgMsg=new BasicMessage();
        imgMsg.setContent(encodedImage);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ImageUpload imageUpload=retrofit.create(ImageUpload.class);
        BasicMessage basicMessage=imageUpload.uploadImage(imgMsg).execute().body();
        return basicMessage;
    }

    private void submitReport() {
        moreDetailCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
           if (isChecked){
               detailLayout.setVisibility(View.VISIBLE);
           } else {
               detailLayout.setVisibility(View.GONE);
           }
        });


        submitBtn.setOnClickListener(v -> {
            if (selectedPhotos.size()==0){
                Toast.makeText(this,"Please upload at least one photo.",Toast.LENGTH_LONG).show();
                return;
            }
            maskImg.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.VISIBLE);
            submitBtn.setEnabled(false);
            UploadPhotoAsync upload=new UploadPhotoAsync(selectedPhotos);
            upload.execute();
            /*String photosUrl="";
            try {
                photosUrl=upload.execute().get();
            } catch (InterruptedException e) {
                photosUrl="error";
            } catch (ExecutionException e) {
                photosUrl="error";
            }
            if (photosUrl.equals("error")){
                Toast.makeText(this, R.string.photo_uploading_fail,Toast.LENGTH_LONG).show();
                submitBtn.setEnabled(true);
                return;
            }
            ReportModel reportModel=new ReportModel();
            if (moreDetailCb.isChecked()){
                reportModel.setRating(ratingLabel.getText().toString());
                reportModel.setType(typeSpinner.getSelectedItem().toString());
                reportModel.setSource(sourceSpinner.getSelectedItem().toString());
                reportModel.setLatitude(locationMarker.getPosition().latitude);
                reportModel.setLongitude(locationMarker.getPosition().longitude);
                reportModel.setDescription(description.getText().toString());
                reportModel.setPhoto(photosUrl);
                reportModel.setLocationName("location");
                reportModel.setHasMoreDetail(true);
            }else {
                reportModel.setPhoto(photosUrl);
                reportModel.setLatitude(locationMarker.getPosition().latitude);
                reportModel.setLongitude(locationMarker.getPosition().longitude);
                reportModel.setHasMoreDetail(false);
            }
            Gson gson=new Gson();
            Log.d("reportJson", "onCreate: "+gson.toJson(reportModel));*/

        });
    }



    private void mapInit(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        locationMap.onCreate(mapViewBundle);
        locationMap.getMapAsync(this);
    }

    private void photoSelection() {
        addPhotoImg.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_STORAGE);
                return;
            }
            if (selectedPhotos.size()==3){
                v.setVisibility(View.GONE);
                return;
            }
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });
        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedPhotos.get(0), "image/*");
                startActivity(intent);
            }
        });
        photo2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedPhotos.get(1), "image/*");
            startActivity(intent);
        });
        photo3.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedPhotos.get(2), "image/*");
            startActivity(intent);
        });
    }

    private void changeRatingText() {
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                } else {
                    Toast.makeText(this, R.string.access_storage_permission,Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (reqCode){
                case RESULT_LOAD_IMG:
                    Uri selectedImage = data.getData();
                    if (selectedPhotos.size()==0){
                        selectedPhotos.add(selectedImage);
                        photo1.setVisibility(View.VISIBLE);
                        photo1.setImageURI(selectedImage);
                        break;
                    } else if (selectedPhotos.size()==1){
                        selectedPhotos.add(selectedImage);
                        photo2.setVisibility(View.VISIBLE);
                        photo2.setImageURI(selectedImage);
                        break;
                    } else if (selectedPhotos.size()==2){
                        selectedPhotos.add(selectedImage);
                        photo3.setVisibility(View.VISIBLE);
                        photo3.setImageURI(selectedImage);
                        addPhotoImg.setVisibility(View.GONE);
                        break;
                    } else {
                        Toast.makeText(this,R.string.image_limit,Toast.LENGTH_LONG).show();
                    }
                    break;
            }
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

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewReportActivity_2.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private class UploadPhotoAsync extends AsyncTask<Void,Void,String>{

        List<Uri> uriList;

        public UploadPhotoAsync(List<Uri> uriList) {
            this.uriList = uriList;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String urls="";
                for (int i= 0;i<uriList.size();i++){
                    BasicMessage msg=uploadPhoto(i);
                    if (msg.getStatus()){
                        urls+=msg.getContent()+"*";
                    } else {
                        return "error";
                    }
                }
                return urls;
            } catch (IOException e) {
                Log.d("error", "doInBackground: "+e.getMessage());
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("error")){
                Toast.makeText(NewReportActivity_2.this, R.string.photo_uploading_fail,Toast.LENGTH_LONG).show();
                submitBtn.setEnabled(true);
                return;
            }
            ReportModel reportModel=new ReportModel();
            if (moreDetailCb.isChecked()){
                reportModel.setRating(ratingLabel.getText().toString());
                reportModel.setType(typeSpinner.getSelectedItem().toString());
                reportModel.setSource(sourceSpinner.getSelectedItem().toString());
                reportModel.setLatitude(locationMarker.getPosition().latitude);
                reportModel.setLongitude(locationMarker.getPosition().longitude);
                reportModel.setDescription(description.getText().toString());
                reportModel.setPhoto(s);
                reportModel.setLocationName("location");
                reportModel.setHasMoreDetail(true);
            }else {
                reportModel.setPhoto(s);
                reportModel.setLatitude(locationMarker.getPosition().latitude);
                reportModel.setLongitude(locationMarker.getPosition().longitude);
                reportModel.setHasMoreDetail(false);
            }
            Gson gson=new Gson();
            Log.d("reportJson", "onCreate: "+gson.toJson(reportModel));
        }
    }



}
