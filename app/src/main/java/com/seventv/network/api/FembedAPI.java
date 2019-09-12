package com.seventv.network.api;


import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FembedAPI {

    String BASE_URL = "https://www.fembed.com/api/";
    String BASE_URL_AVPLE = "https://www.avple.video/api/";

    FembedAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(FembedAPI.class);
    FembedAPI INSTANCE_AVPLE = NetworkBasic.getRetrofit(BASE_URL_AVPLE).create(FembedAPI.class);

    @POST("source/{id}")
    Observable<String> getVideo(@Path("id") String id);

}
