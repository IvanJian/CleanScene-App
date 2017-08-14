package inspiringbits.me.cleanscene.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;

public class ViewReportsOnMapActivity extends AppCompatActivity {

    @BindView(R.id.report_map_mapview)
    MapView mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports_on_map);
        ButterKnife.bind(this);
    }
}
