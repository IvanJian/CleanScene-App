package inspiringbits.me.cleanscene.tool;

import android.content.res.Resources;

import java.util.Objects;

import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.rest_service.UserService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ivan on 2017/8/30.
 */

public class RestServiceTool {
    public static Retrofit getRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Resources.getSystem().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
