package com.seventv.network.api;

import com.seventv.network.NetworkBasic;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

//https://api.verystream.com/file/dlticket?file={file}&login={login}&key={key}
//https://api.verystream.com/file/dl?file={file}&ticket={ticket}&captcha_response={captcha_response}

//https://api.verystream.com/file/
public interface VerystreamAPI {

    String BASE_URL = "https://verystream.com/";
    VerystreamAPI INSTANCE = NetworkBasic.getRetrofit(BASE_URL).create(VerystreamAPI.class);

    @GET("stream/{id}")
    Observable<String> getVideo(@Path("id") String id);

    /*
    @GET("dlticket")
    Observable<String> prepareDownload(@Query("file") String file);

    @GET("dl")
    Observable<String> getDownloadLink(@Query("file") String file, @Query("ticket") String ticket, @Query("captcha_response") String captchaResponse);
    */
}
