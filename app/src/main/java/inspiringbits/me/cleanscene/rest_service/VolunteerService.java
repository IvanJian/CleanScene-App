package inspiringbits.me.cleanscene.rest_service;

import java.util.List;

import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.Volunteer;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Ivan on 2017/8/19.
 */

public interface VolunteerService {
    @GET("/volunteer/all")
    Call<List<Volunteer>> getVolunteerCentre();

    @GET("/volunteer/activity/join/{volunteeringActivityId}/{userId}")
    Call<BasicMessage> joinVolunteerActivity(@Path("volunteeringActivityId")String volunteeringActivityId,@Path("userId") String userId);
}
