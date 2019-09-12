package com.seventv.network.api;

import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JavopenAPI {

    String BASE_URL = "https://javopen.co/";

    JavopenAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(JavopenAPI.class);

    @GET(".")
    Observable<String> searchVideo(@Query("s") String query);

    @GET("video/{name}")
    Observable<String> getVideo(@Path("name") String name);
}
