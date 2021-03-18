package ca.josue.meteo.interfaces;

import ca.josue.meteo.model.Temperature;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Weather {

    @GET("weather")
    Call<Temperature> getTemperature(@Query("q") String city_name,
                                     @Query("appid") String apikey);
}
