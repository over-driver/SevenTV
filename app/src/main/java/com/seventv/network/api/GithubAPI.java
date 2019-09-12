package com.seventv.network.api;

import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface GithubAPI {

    String BASE_URL = "https://raw.githubusercontent.com/over-driver/SevenTV/master/";
    GithubAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(GithubAPI.class);

    @GET("version.json")
    Observable<String> getVersion();
}
