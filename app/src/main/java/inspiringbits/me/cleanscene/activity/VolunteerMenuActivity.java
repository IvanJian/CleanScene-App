package inspiringbits.me.cleanscene.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.githang.statusbar.StatusBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;

public class VolunteerMenuActivity extends AppCompatActivity {

    @BindView(R.id.new_report)
    ImageView newActivity;
    @BindView(R.id.report_map)
    ImageView findActivity;
    @BindView(R.id.my_report)
    ImageView myActivity;
    @BindView(R.id.volunteer_menu)
    ImageView volunteerCentre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E97DC")));
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.status_bar));
        setContentView(R.layout.activity_volunteer_menu);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.new_report, R.id.report_map, R.id.my_report, R.id.volunteer_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.new_report:
                startActivity(new Intent(this,NewVolunteeringActivity.class));
                break;
            case R.id.report_map:
                startActivity(new Intent(this,FindVolunteeringActivity.class));
                break;
            case R.id.my_report:
                startActivity(new Intent(this,MyVolunteeringActivity.class));
                break;
            case R.id.volunteer_menu:
                startActivity(new Intent(this,VolunteerCentreActivity.class));
                break;
        }
    }
}
