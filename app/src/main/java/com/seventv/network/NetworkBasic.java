package com.seventv.network;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.utils.FileBasic;
import com.seventv.model.SevenVideoSource;
import com.seventv.network.api.AvgleAPI;
import com.seventv.network.api.BestjavpornAPI;
import com.seventv.network.api.FembedAPI;
import com.seventv.network.api.GithubAPI;
import com.seventv.network.api.RapidVideoAPI;
import com.seventv.network.api.VerystreamAPI;
import com.seventv.network.parser.AvgleParser;
import com.seventv.network.parser.BestjavpornParser;
import com.seventv.network.parser.FembedParser;
import com.seventv.network.parser.RapidvideoParser;
import com.seventv.network.parser.VerystreamParser;
import com.seventv.network.parser.item.VersionInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkBasic {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3100.0 Safari/537.36";

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .url(original.url())
                    .header("User-Agent", USER_AGENT)
                    .build();
            return chain.proceed(request);
        }
    })
            .cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    public static Observable<String> getRedirectsUrl(String originUrl) {

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String realUrl = null;
                if (!TextUtils.isEmpty(originUrl)) {
                    try {
                        if (originUrl.startsWith("https")) {
                            // Create a trust manager that does not validate certificate chains
                            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }

                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }};
                            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
                                public boolean verify(String s, SSLSession sslsession) {
                                    return true;
                                }
                            };
                            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
                            SSLContext sc = SSLContext.getInstance("TLS");
                            sc.init(null, trustAllCerts, new SecureRandom());
                            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                        }
                        URL url = new URL(originUrl);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setUseCaches(false);
                        urlConnection.setInstanceFollowRedirects(false);
                        urlConnection.setRequestProperty("Authorization", "Authorization..."); // 请求头
                        urlConnection.setConnectTimeout(6000);
                        urlConnection.setReadTimeout(6000);
                        urlConnection.connect();
                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode == 302 || responseCode == 301) {
                            String url302 = urlConnection.getHeaderField("Location");
                            if (TextUtils.isEmpty(url302)) {
                                url302 = urlConnection.getHeaderField("location"); //临时重定向和永久重定向location的大小写有区分
                            }
                            if (!(url302.startsWith("http://") || url302.startsWith("https://"))) { //某些时候会省略host，只返回后面的path，所以需要补全url
                                URL originalUrl = new URL(originUrl);
                                url302 = originalUrl.getProtocol() + "://" + originalUrl.getHost() + ":" + originalUrl.getPort() + url302;
                            }

                            realUrl = url302;
                        } else if (responseCode == 200) {
                            realUrl = originUrl;
                        }
                        urlConnection.disconnect();
                    } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("TEST_NETWORK", "real url: " + realUrl);
                emitter.onNext(realUrl);
                emitter.onComplete();
            }
        });
    }

    public static Retrofit getRetrofit(String baseUrl){
        return new Retrofit.Builder()
                .client(HTTP_CLIENT)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public static Observable<String> getUrlObservable(String id, String source){
        Observable<String> observable = null;
        switch (source){
            case SevenVideoSource.AVGLE:
                observable = AvgleAPI.INSTANCE.search(id);
                break;
            case SevenVideoSource.FEMBED:
                if (id.endsWith(" ")){
                    observable = FembedAPI.INSTANCE_AVPLE.getVideo(id.trim());
                } else {
                    observable = FembedAPI.INSTANCE.getVideo(id);
                }
                break;
            case SevenVideoSource.VERYSTREAM:
                observable = VerystreamAPI.INSTANCE.getVideo(id);
                break;
            case SevenVideoSource.RAPIDVIDEO:
                observable = RapidVideoAPI.INSTANCE.getVideo(id);
                break;
            case SevenVideoSource.BESTJAVPORN:
                observable = BestjavpornAPI.INSTANCE_VIDEO.getEmbeddedVideo(id);
                break;
        }
        return observable;
    }

    public static Map<String, SevenVideoSource.VideoUrl> decodeVideoResolution(String response, String source){
        Map<String, SevenVideoSource.VideoUrl> map = new HashMap<>();
        try {
            switch (source) {
                case SevenVideoSource.AVGLE:
                    AvgleParser.parseVideo(response, map);
                    break;
                case SevenVideoSource.RAPIDVIDEO:
                    RapidvideoParser.parseVideo(response, map);
                    break;
                case SevenVideoSource.FEMBED:
                    FembedParser.parseVideo(response, map);
                    break;
                case SevenVideoSource.VERYSTREAM:
                    VerystreamParser.parseVideo(response, map);
                    break;
                case SevenVideoSource.BESTJAVPORN:
                    BestjavpornParser.parseVideo(response, map);
            }
        }catch (Exception e){
            Log.e("decodeVideoResolution", "Error: " + e);
        }
        return map;
    }

    public static void checkUpdate(Context context, boolean notify){
        GithubAPI.INSTANCE.getVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            VersionInfo newVersion = new ObjectMapper().readValue(s, VersionInfo.class);
                            long versionCode = SevenTVApplication.getVersionCode(context);
                            if(newVersion.versionCode > versionCode){
                                new AlertDialog.Builder(context)
                                        .setTitle(context.getString(R.string.update))
                                        .setMessage(context.getString(R.string.ask_update))
                                        .setPositiveButton(context.getString(R.string.update_now), (dialog, which) -> {
                                            FileBasic.downloadInstallApk(context, newVersion);
                                        })
                                        .setNegativeButton(context.getString(R.string.update_later), null)
                                        .show();
                            } else if (notify) {
                                Toast.makeText(context, context.getString(R.string.newest_version), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.e("GITHUB", e.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) { Log.e("GITHUB", e.toString());}

                    @Override
                    public void onComplete() {}
                });
    }

    public static String getSync(String urlString){
        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            int code = urlConnection.getResponseCode();

            if(code==200){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                if (in != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                }
                in.close();
            }

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            urlConnection.disconnect();
        }
        return result;
    }

}
