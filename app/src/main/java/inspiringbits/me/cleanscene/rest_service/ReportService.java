package inspiringbits.me.cleanscene.rest_service;

import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.ReportModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ivan on 2017/8/17.
 */

public interface ReportService {
    @POST("/report/create")
    Call<BasicMessage> createReport(@Body ReportModel report);
}
