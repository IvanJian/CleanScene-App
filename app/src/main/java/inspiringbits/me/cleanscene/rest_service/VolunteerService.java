package inspiringbits.me.cleanscene.rest_service;

import java.util.List;

import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.Volunteer;
import inspiringbits.me.cleanscene.model.VolunteeringActivity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Ivan on 2017/8/19.
 */

public interface VolunteerService {
    @GET("/volunteer/all")
    Call<List<Volunteer>> getVolunteerCentre();

    @GET("/volunteer/activity/join/{volunteeringActivityId}/{userId}")
    Call<BasicMessage> joinVolunteerActivity(@Path("volunteeringActivityId")String volunteeringActivityId,@Path("userId") String userId);

    @GET("/volunteer/activity/all")
    Call<List<VolunteeringActivity>> getAllVolunteerActivity();

    @POST("/volunteer/activity/create")
    Call<BasicMessage> createActivity(@Body VolunteeringActivity volunteeringActivity);

    @GET("/volunteer/activity/join/{volunteeringActivityId}/{userId}")
    Call<BasicMessage> joinActivity(@Path("volunteeringActivityId") String volunteeringActivityId, @Path("userId") String userId);

    @GET("/volunteer/activity/{id}")
    Call<VolunteeringActivity> getActivityById(@Path("id") String id);

    @GET("/volunteer/activity/join/{volunteeringActivityId}")
    Call<BasicMessage> joinVolunteeringActivityAnonymous(@Path("volunteeringActivityId") int volunteeringActivityId);

    @GET("/volunteer/activity/user/{id}")
    Call<List<VolunteeringActivity>> getActivityForUser(@Path("id") String id);
}
