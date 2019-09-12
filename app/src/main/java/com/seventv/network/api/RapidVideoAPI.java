package com.seventv.network.api;

import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RapidVideoAPI {

    String BASE_URL = "https://www.rapidvideo.com/";
    RapidVideoAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(RapidVideoAPI.class);

    @GET("d/{fileId}")
    Observable<String> getVideo(@Path("fileId") String fileId);

}
