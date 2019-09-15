package com.seventv.network.api;

import com.seventv.R;
import com.seventv.network.NetworkBasic;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface SevenAPI {

    String ACTION_LIST = "list";
    String ACTION_SEARCH = "search";
    String ACTION_FILTER = "filter";

    String CATEGORY_CENCORED = "censored";
    String CATEGORY_UNCENCORED = "uncensored";
    String CATEGORY_AMATEUR = "amateurjav";
    String CATEGORY_CHINESE = "chinese";

    String FILTER_ISSUER = "issuer";
    String FILTER_MAKER = "makersr";
    String FILTER_DIRECTOR = "director";
    String FILTER_GENRE = "category";
    String FILTER_IDOL = "avperformer";

    Map<Integer, String> FILTERS = new HashMap<Integer, String>(){{
       put(R.string.info_label, FILTER_ISSUER);
       put(R.string.info_studio, FILTER_MAKER);
       put(R.string.info_director, FILTER_DIRECTOR);
       put(R.string.info_genre, FILTER_GENRE);
       put(R.string.info_idol,FILTER_IDOL);
    }};

    String BASE_URL = "https://7mmtv.tv/";
    SevenAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(SevenAPI.class);

    @GET("{language}/{category}_list/all/{page}.html") //zh
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Observable<String> getVideoList(
            @Path("language") String language,
            @Path("category") String category,
            @Path("page") String page);

    @GET("{language}/{category}_{filter}/{query}/{page}.html")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Observable<String> filterVideo(
            @Path("language") String language,
            @Path("category") String category,
            @Path("filter") String filter,
            @Path("query") String query,
            @Path("page") String page);

    @GET("{language}/{category}_search/all/{query}/{page}.html")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Observable<String> searchVideo(
            @Path("language") String language,
            @Path("category") String category,
            @Path("query") String query,
            @Path("page") String page);

    @GET("{url}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Observable<String> getVideoDetail(@Path("url") String url);

    @GET("{language}/{category}_content/{id}/index.html")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Observable<String> getVideoDetail(
            @Path("language") String language,
            @Path("category") String category,
            @Path("id") String id);

}
