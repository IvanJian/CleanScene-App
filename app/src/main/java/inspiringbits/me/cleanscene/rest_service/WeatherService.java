package inspiringbits.me.cleanscene.rest_service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ivan on 2017/9/2.
 */

public interface WeatherService {
    @GET("/data/2.5/forecast/daily")
    Call<String> getWeatherStr(@Query("lat") double lat, @Query("lng") double lng, @Query("cnt") int cnt);
}
