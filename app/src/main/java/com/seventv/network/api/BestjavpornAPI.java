package com.seventv.network.api;

import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BestjavpornAPI {

    String BASE_URL = "https://bestjavporn.com/";
    String BASE_URL_VIDEO = "https://video.bestjavporn.com/";

    BestjavpornAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(BestjavpornAPI.class);

    BestjavpornAPI INSTANCE_VIDEO = NetworkBasic.getRetrofit(BASE_URL_VIDEO).create(BestjavpornAPI.class);

    @GET(".")
    Observable<String> searchVideo(@Query("s") String query);

    @GET("video/{name}")
    Observable<String> getVideo(@Path("name") String name);

    @GET("player.php")
    Observable<String> getEmbeddedVideo(@Query("playerid") String query);
}
