package com.seventv.model;

import android.util.Log;

import com.seventv.network.NetworkBasic;
import com.seventv.view.SwitchVideoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SevenVideoSourceManager extends SevenVideoSource {

    private OnUrlReadyListener mOnUrlReadyListener;
    private Map<String, Map<String, Map<String, VideoUrl>>> mVideoSourcesResolution;

    public SevenVideoSourceManager(Map<String, List<SevenVideoSource.VideoUrl>> videoSource){
        mVideoSources = videoSource;
        mVideoSourcesResolution = new HashMap<>();
        for(String key: mVideoSources.keySet()){
            List<VideoUrl> videoUrls = mVideoSources.get(key);
            mVideoSourcesResolution.put(key, new HashMap<>());
            for (int i = 0; i < videoUrls.size(); i++){
                mVideoSourcesResolution.get(key).put(Integer.toString(i), new HashMap<>());
            }
        }
    }

    public void setOnUrlReadyListener(OnUrlReadyListener onUrlReadyListener){
        mOnUrlReadyListener = onUrlReadyListener;
    }

    public interface OnUrlReadyListener {
        public void onUrlReady(String source, String part, String resolution);
    }

    public boolean isReady(String source, String part){
        return !mVideoSources.get(source).get(Integer.parseInt(part)).needRedirect;
    }

    public boolean isReady(String source, String part, String resolution){
        resolution = getValidResolution(source, part, resolution);
        return !mVideoSourcesResolution.get(source).get(part).get(resolution).needRedirect;
    }

    public List<SwitchVideoModel> getAvailableSource(){
        List<SwitchVideoModel> availableSource = new ArrayList<>();
        for(String key: mVideoSources.keySet()){
            availableSource.add(new SwitchVideoModel(key, SwitchVideoModel.SOURCE));
        }
        return availableSource;
    }

    public List<SwitchVideoModel> getAvailablePart(String source){
        int n = mVideoSources.get(source).size();
        List<SwitchVideoModel> availablePart = new ArrayList<>(n);
        for (int i = 0; i < n; i++){
            availablePart.add(new SwitchVideoModel(Integer.toString(i), SwitchVideoModel.PART));
        }
        return availablePart;
    }

    public List<SwitchVideoModel> getAvailableResolution(String source, String part){
        List<SwitchVideoModel> availableResolution = new ArrayList<>();
        for (String r: mVideoSourcesResolution.get(source).get(part).keySet()){
            availableResolution.add(new SwitchVideoModel(r, SwitchVideoModel.RESOLUTION));
        }
        return availableResolution;
    }

    public String getValidResolution(String source, String part, String resolution){
        Set<String> keySet = mVideoSourcesResolution.get(source).get(part).keySet();
        if (keySet.contains(resolution)){
            return resolution;
        }
        ArrayList<String> arrayList = new ArrayList<>(keySet);
        if (arrayList.size() > 1){
            Collections.sort(arrayList, (String o1, String o2) ->{
                Integer a1 = Integer.parseInt(o1.substring(0, o1.length()-1));
                Integer a2 = Integer.parseInt(o2.substring(0, o2.length()-1));
                return a1.compareTo(a2);
            });
        }
        return arrayList.get(0);
    }

    public String getUrl(String source, String part, String resolution){
        resolution = getValidResolution(source, part, resolution);
        return mVideoSourcesResolution.get(source).get(part).get(resolution).url;
    }

    public void setUrl(String source, String part, String resolution, String url, boolean needRedirect){
        resolution = getValidResolution(source, part, resolution);
        mVideoSourcesResolution.get(source).get(part).get(resolution).needRedirect = needRedirect;
        mVideoSourcesResolution.get(source).get(part).get(resolution).url = url;
    }

    public void requestVideoUrl(String source, String part, String resolution){

        int partInt = Integer.parseInt(part);

        Observable<String> observable;
        if (isReady(source, part)){
            observable = Observable.just("");
        } else {
            String fileId = mVideoSources.get(source).get(partInt).url;
            fileId = fileId.substring(fileId.lastIndexOf('/') + 1);
            observable = NetworkBasic.getUrlObservable(fileId, source);
        }

        observable.subscribeOn(Schedulers.io()).flatMap((response) -> {
                if (response.length() > 0){
                    mVideoSourcesResolution.get(source).put(part, NetworkBasic.decodeVideoResolution(response, source));
                    mVideoSources.get(source).get(partInt).needRedirect = false;
                }
                if(!isReady(source, part, resolution)){
                    return NetworkBasic.getRedirectsUrl(getUrl(source, part, resolution));
                } else {
                    return Observable.just("");
                }
            }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        if (s.length() > 0){
                            setUrl(source, part, resolution, s, false);
                        }
                        mOnUrlReadyListener.onUrlReady(source, part, getValidResolution(source, part, resolution));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Url Observer", "ERROR: " + e);
                    }

                    @Override
                    public void onComplete() { }
                });
    }

    public String getPreferredSource(){
        Set<String> keySet = mVideoSources.keySet();
        for(String available: AVAILABLE){
            if(keySet.contains(available)){
                return available;
            }
        }
        return null;
    }


}
