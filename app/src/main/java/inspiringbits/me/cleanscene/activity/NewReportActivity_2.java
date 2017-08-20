package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.rest_service.ImageUpload;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.ReportModel;
import inspiringbits.me.cleanscene.rest_service.ReportService;
import inspiringbits.me.cleanscene.view.NestedMapView;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewReportActivity_2 extends AppCompatActivity implements OnMapReadyCallback {

    static final int RESULT_LOAD_IMG = 1;
    static final int RESULT_CAMERA = 0;
    static final int MY_PERMISSIONS_REQUEST_STORAGE = 2;
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    String mCurrentPhotoPath;
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
    static final String MY_REPORT="my_report";
    private Uri file;

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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RESULT_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private BasicMessage uploadPhoto(int index) throws IOException {
        Uri uri=selectedPhotos.get(index);
        final InputStream imageStream = getContentResolver().openInputStream(uri);
        final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
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
    private File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    private void photoSelection() {
        addPhotoImg.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_STORAGE);
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},RESULT_CAMERA);
                return;
            }
            if (selectedPhotos.size()==3){
                v.setVisibility(View.GONE);
                return;
            }

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            CharSequence items[] = new CharSequence[] {"Gallery","Camera" };
            adb.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface d, int n) {
                    if (n==0){
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                    } else if (n==1){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = FileProvider.getUriForFile(NewReportActivity_2.this, "me.inspiringbits", getOutputMediaFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

                        startActivityForResult(intent, RESULT_CAMERA);
                    }
                }

            });
            adb.setNegativeButton("Cancel", null);
            adb.setTitle("Upload Photo");
            adb.show();

            /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);*/
        });
        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewReportActivity_2.this,PictureActivity.class);
                Uri uri=selectedPhotos.get(0);
                intent.putExtra("uri",uri);
                startActivity(intent);
            }
        });
        photo2.setOnClickListener(v -> {
            Intent intent = new Intent(NewReportActivity_2.this,PictureActivity.class);
            Uri uri=selectedPhotos.get(1);
            intent.putExtra("uri",uri);
            startActivity(intent);
        });
        photo3.setOnClickListener(v -> {
            Intent intent = new Intent(NewReportActivity_2.this,PictureActivity.class);
            Uri uri=selectedPhotos.get(2);
            intent.putExtra("uri",uri);
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
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            CharSequence items[] = new CharSequence[] {"Gallery","Camera" };
            adb.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface d, int n) {
                    if (n==0){
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                    } else if (n==1){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = FileProvider.getUriForFile(NewReportActivity_2.this, "me.inspiringbits", getOutputMediaFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

                        startActivityForResult(intent, RESULT_CAMERA);
                    }
                }

            });
            adb.setNegativeButton("Cancel", null);
            adb.setTitle("Upload Photo");
            adb.show();
        } else {
            Toast.makeText(this, R.string.access_storage_permission,Toast.LENGTH_LONG).show();
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
                case RESULT_CAMERA:
                    if (resultCode==RESULT_OK) {
                        Uri cameraImg = file;
                        Log.d("path", "onActivityResult: " + cameraImg.getPath());
                        if (selectedPhotos.size() == 0) {
                            selectedPhotos.add(cameraImg);
                            photo1.setVisibility(View.VISIBLE);
                            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(cameraImg)
                                    .setProgressiveRenderingEnabled(true)
                                    .setResizeOptions(new ResizeOptions(170,170))
                                    .build();
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setImageRequest(request)
                                    .setOldController(photo1.getController())
                                    .build();
                            photo1.setController(controller);
                            break;
                        } else if (selectedPhotos.size() == 1) {
                            selectedPhotos.add(cameraImg);
                            photo2.setVisibility(View.VISIBLE);
                            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(cameraImg)
                                    .setProgressiveRenderingEnabled(true)
                                    .setResizeOptions(new ResizeOptions(170,170))
                                    .build();
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setImageRequest(request)
                                    .setOldController(photo2.getController())
                                    .build();
                            photo2.setController(controller);
                            break;
                        } else if (selectedPhotos.size() == 2) {
                            selectedPhotos.add(cameraImg);
                            photo3.setVisibility(View.VISIBLE);
                            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(cameraImg)
                                    .setProgressiveRenderingEnabled(true)
                                    .setResizeOptions(new ResizeOptions(170,170))
                                    .build();
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setImageRequest(request)
                                    .setOldController(photo3.getController())
                                    .build();
                            photo3.setController(controller);
                            addPhotoImg.setVisibility(View.GONE);
                            break;
                        } else {
                            Toast.makeText(this, R.string.image_limit, Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
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
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                locationMarker.setPosition(marker.getPosition());
            }
        });
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
        ReportModel reportModel;

        public UploadPhotoAsync(List<Uri> uriList) {
            this.uriList = uriList;
        }

        @Override
        protected void onPreExecute() {
            reportModel=new ReportModel();
            if (moreDetailCb.isChecked()){
                reportModel.setRating(ratingLabel.getText().toString());
                reportModel.setType(typeSpinner.getSelectedItem().toString());
                reportModel.setSource(sourceSpinner.getSelectedItem().toString());
                reportModel.setLatitude(locationMarker.getPosition().latitude);
                reportModel.setLongitude(locationMarker.getPosition().longitude);
                reportModel.setDescription(description.getText().toString());
                reportModel.setLocationName("location");
                reportModel.setHasMoreDetail(true);
            }else {
                reportModel.setLatitude(locationMarker.getPosition().latitude);
                reportModel.setLongitude(locationMarker.getPosition().longitude);
                reportModel.setHasMoreDetail(false);
            }
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
                reportModel.setPhoto(urls);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ReportService reportService=retrofit.create(ReportService.class);
                BasicMessage msg=reportService.createReport(reportModel).execute().body();
                if (msg.getStatus()){
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(NewReportActivity_2.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String reportsJson=sharedPref.getString(NewReportActivity_2.MY_REPORT,"");
                    Gson gson=new Gson();
                    List<ReportModel> reportModels=new ArrayList<ReportModel>();
                    if (!"".equals(reportsJson)){
                        reportModels=gson.fromJson(reportsJson,new TypeToken<List<ReportModel>>(){}.getType());
                    }
                    reportModel.setReportId(Integer.parseInt(msg.getContent()));
                    reportModels.add(reportModel);
                    editor.putString(NewReportActivity_2.MY_REPORT,gson.toJson(reportModels));
                    Log.d("report", "doInBackground: "+gson.toJson(reportModels));
                    editor.apply();
                    return msg.getContent();
                }
                Log.d("submit_error", "doInBackground: "+msg.getContent());
                return "error";
            } catch (IOException e) {
                Log.d("error", "doInBackground: "+e.getMessage());
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("error")){
                Toast.makeText(NewReportActivity_2.this, R.string.photo_uploading_fail,Toast.LENGTH_LONG).show();
                maskImg.setVisibility(View.GONE);
                pBar.setVisibility(View.GONE);
                submitBtn.setEnabled(true);
                return;
            } else {
                Intent intent=new Intent(NewReportActivity_2.this,ReportDetailActivity.class);
                intent.putExtra("reportId",s);
                NewReportActivity_2.this.startActivity(intent);
                NewReportActivity_2.this.finish();
            }
        }
    }



}
