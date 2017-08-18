package inspiringbits.me.cleanscene.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.ReportModel;
import inspiringbits.me.cleanscene.rest_service.ReportService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportDetailActivity extends AppCompatActivity {

    String reportId;
    @BindView(R.id.report_detial_scrollview)
    ScrollView scrollView;
    @BindView(R.id.report_detail_pbar)
    ProgressBar progressBar;
    @BindView(R.id.report_detail_image_view_2)
    SimpleDraweeView image2;
    @BindView(R.id.report_detail_image_view_3)
    SimpleDraweeView image3;
    @BindView(R.id.report_detail_image_view_1)
    SimpleDraweeView image1;
    @BindView(R.id.report_detail_description)
    TextView description;
    @BindView(R.id.report_detail_rating)
    TextView rating;
    @BindView(R.id.report_detail_source)
    TextView source;
    @BindView(R.id.report_detail_time)
    TextView time;
    @BindView(R.id.report_detail_type)
    TextView type;
    @BindView(R.id.report_detail_report_id)
    TextView reportIdTxt;
    @BindView(R.id.report_detail_image_card_2)
    CardView card2;
    @BindView(R.id.report_detail_image_card_3)
    CardView card3;
    @BindView(R.id.report_detail_image_card_1)
    CardView card1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        ButterKnife.bind(this);
        scrollView.setVisibility(View.GONE);
        reportId=getIntent().getStringExtra("reportId");
        reportIdTxt.setText(reportId);
        GetReportById getReportById=new GetReportById();
        getReportById.execute();
    }

    private class GetReportById extends AsyncTask<Void,Void,ReportModel>{


        @Override
        protected ReportModel doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ReportService reportService=retrofit.create(ReportService.class);
            try {
                ReportModel reportModel=reportService.findReportById(reportId).execute().body();
                return reportModel;
            } catch (IOException e) {
                Log.d("get report", "doInBackground: "+e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReportModel reportModel) {
            if (reportModel==null){
                Toast.makeText(ReportDetailActivity.this,"Failed to get the report from server.",Toast.LENGTH_LONG).show();
                ReportDetailActivity.this.finish();
                return;
            }
            time.setText(reportModel.getDate()+" "+reportModel.getTime());
            if (reportModel.isHasMoreDetail()){
                rating.setText(reportModel.getRating());
                type.setText(reportModel.getType());
                source.setText(reportModel.getSource());
                description.setText(reportModel.getDescription());
            }

            String[] urls=reportModel.getPhoto().split("\\*");
            for (int i=0;i<urls.length;i++){
                if (urls[i]==""){
                    break;
                }
                Uri uri = Uri.parse(ReportDetailActivity.this.getString(R.string.base_url).toString()+"/"+urls[i]);
                if (i==0){
                    card1.setVisibility(View.VISIBLE);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .setProgressiveRenderingEnabled(true)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(image1.getController())
                            .build();
                    image1.setController(controller);
                    image1.setOnClickListener(v->{
                        Intent intent=new Intent(ReportDetailActivity.this,PictureActivity.class);
                        intent.putExtra("uri", uri);
                        startActivity(intent);
                    });
                } else if (i==1){
                    card2.setVisibility(View.VISIBLE);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .setProgressiveRenderingEnabled(true)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(image2.getController())
                            .build();
                    image2.setController(controller);
                    image2.setOnClickListener(v->{
                        Intent intent=new Intent(ReportDetailActivity.this,PictureActivity.class);
                        intent.putExtra("uri", uri);
                        startActivity(intent);
                    });
                } else if (i==2){
                    card3.setVisibility(View.VISIBLE);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .setProgressiveRenderingEnabled(true)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(image3.getController())
                            .build();
                    image3.setController(controller);
                    image3.setOnClickListener(v->{
                        Intent intent=new Intent(ReportDetailActivity.this,PictureActivity.class);
                        intent.putExtra("uri", uri);
                        startActivity(intent);
                    });
                }
            }
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }
}
