package inspiringbits.me.cleanscene.rest_service;

import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ivan on 2017/8/30.
 */

public interface UserService {
    @POST("/user/create")
    Call<BasicMessage> loadUser(@Body User user);
}
