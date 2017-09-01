package inspiringbits.me.cleanscene.rest_service;

import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Ivan on 2017/8/30.
 */

public interface UserService {
    @POST("/user/create")
    Call<BasicMessage> loadUser(@Body User user);

    @GET("/user/{id}")
    Call<User> getUserById(@Path("id") int id);
}
