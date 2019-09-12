package com.seventv.network.api;

import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetflavAPI {

    String BASE_URL = "https://www.netflav.com/";

    NetflavAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(NetflavAPI.class);

    @GET("search?type=title")
    Observable<String> searchVideo(@Query("keyword") String query);

    @GET("video")
    Observable<String> getVideo(@Query("id") String id);
}
