package com.seventv.network.api;

import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface AvgleAPI {

    String BASE_URL = "https://api.avgle.com/";

    AvgleAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(AvgleAPI.class);

    @GET("/v1/search/{keyword}/0?limit=1")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Observable<String> search(@Path(value = "keyword") String keyword);

    @GET("/{path}")
    Observable<String> get(@Path("path") String path);

}
