package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.githang.statusbar.StatusBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_report)
    ImageView reportBtn;
    @BindView(R.id.main_map)
    ImageView mapBtn;
    @BindView(R.id.main_my_report)
    ImageView myReportBtn;
    @BindView(R.id.main_volunteer)
    ImageView volunteerBtn;

    static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION=1;


    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.status_bar)));
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.status_bar));
        ButterKnife.bind(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

            return;
        }
    }

    @OnClick(R.id.main_report)
    public void newReport(ImageView view){
        startActivity(new Intent(this,NewReportActivity_2.class));
    }

    @OnClick(R.id.main_map)
    public void mapView(ImageView view){
        Intent intent=new Intent(this,ViewReportsOnMapActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_volunteer)
    public void volunteerView(ImageView view){
        Intent intent=new Intent(this,VolunteerCentreActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_my_report)
    public void myReportView(ImageView b){
        Intent intent=new Intent(this,MyReportActivity.class);
        startActivity(intent);
    }

}
