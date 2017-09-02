package inspiringbits.me.cleanscene.rest_service;

import java.util.List;

import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.JsonsRootBean;
import inspiringbits.me.cleanscene.model.Volunteer;
import inspiringbits.me.cleanscene.model.VolunteeringActivity;
import inspiringbits.me.cleanscene.model.VolunteeringRecommendation;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("/volunteer/activity/dropout/{volunteeringActivityId}/{userId}")
    Call<BasicMessage> dropout(@Path("volunteeringActivityId") String volunteeringActivityId,@Path("userId") String userId);

    @GET("/volunteer/activity/recommend")
    Call<VolunteeringRecommendation> getRecommendation();

    @GET("daily")
    Call<JsonsRootBean> getWeather(@Query("lat") double lat, @Query("lng") double lng, @Query("cnt") int cnt);

    @GET("daily")
    Call<String> getWeatherStr(@Query("lat") double lat, @Query("lng") double lng, @Query("cnt") int cnt);
}
