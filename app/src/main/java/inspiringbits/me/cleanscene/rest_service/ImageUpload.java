package inspiringbits.me.cleanscene.rest_service;

import inspiringbits.me.cleanscene.model.BasicMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ivan on 2017/8/16.
 */

public interface ImageUpload {
    @POST("/upload/image")
    Call<BasicMessage> uploadImage(@Body BasicMessage encodedImage);
}
