package inspiringbits.me.cleanscene.rest_service;

import java.util.List;

import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.ReportModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Ivan on 2017/8/17.
 */

public interface ReportService {
    @POST("/report/create")
    Call<BasicMessage> createReport(@Body ReportModel report);

    @GET("report/{id}")
    Call<ReportModel> findReportById(@Path("id")String id);

    @GET("report/all")
    Call<List<ReportModel>> getAllReort();
}
