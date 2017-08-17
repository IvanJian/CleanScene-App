package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_report)
    ImageView reportBtn;
    @BindView(R.id.main_map)
    ImageView mapBtn;

    static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent intent=new Intent(this,ReportDetailActivity.class);
        intent.putExtra("reportId","13");
        startActivity(intent);
    }

}
