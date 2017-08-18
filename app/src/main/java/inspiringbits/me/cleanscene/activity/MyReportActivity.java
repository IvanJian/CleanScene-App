package inspiringbits.me.cleanscene.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.adapter.MyReportAdapter;
import inspiringbits.me.cleanscene.model.ReportModel;
import inspiringbits.me.cleanscene.rest_service.ReportService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyReportActivity extends AppCompatActivity {

    @BindView(R.id.my_report_gridview)
    GridView gridView;
    MyReportAdapter adapter;
    List<ReportModel> reportModelList=new ArrayList<ReportModel>();
    @BindView(R.id.my_report_pBar)
    ProgressBar pbar;
    @BindView(R.id.my_report_hint)
    TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);
        ButterKnife.bind(this);

        gridView.setVisibility(View.GONE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String reportsJson=sharedPref.getString("my_report","");
        Gson gson=new Gson();
        List<ReportModel> reportModels=new ArrayList<ReportModel>();
        if (!"".equals(reportsJson)){
            reportModels=gson.fromJson(reportsJson,new TypeToken<List<ReportModel>>(){}.getType());
        } else {
            hint.setVisibility(View.VISIBLE);
        }
        reportModelList=reportModels;
        adapter=new MyReportAdapter(reportModels,this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MyReportActivity.this,ReportDetailActivity.class);
                intent.putExtra("reportId",adapter.getReportModelList().get(position).getReportId().toString());
                startActivity(intent);
            }
        });
        GetReportDetial getReportDetial=new GetReportDetial();
        getReportDetial.execute();
    }

    private class GetReportDetial extends AsyncTask<Void,Void,List<ReportModel>>{

        @Override
        protected List<ReportModel> doInBackground(Void... params) {
            if (MyReportActivity.this.reportModelList.size()==0){
                return null;
            }
            for (ReportModel reportModel:MyReportActivity.this.reportModelList){
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ReportService reportService=retrofit.create(ReportService.class);
                try {
                    ReportModel report=reportService.findReportById(reportModel.getReportId().toString()).execute().body();
                    if (report==null){
                        continue;
                    }
                    reportModel.setDate(report.getDate());
                    reportModel.setTime(report.getTime());
                } catch (IOException e) {
                    Log.d("get report", "doInBackground: "+e.getMessage());
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ReportModel> reportModels) {
            adapter.setReportModelList(MyReportActivity.this.reportModelList);
            adapter.notifyDataSetChanged();
            gridView.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
        }
    }
}
