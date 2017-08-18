package inspiringbits.me.cleanscene.rest_service;

import java.util.List;

import inspiringbits.me.cleanscene.model.Volunteer;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Ivan on 2017/8/19.
 */

public interface VolunteerService {
    @GET("/volunteer/all")
    Call<List<Volunteer>> getVolunteerCentre();
}
