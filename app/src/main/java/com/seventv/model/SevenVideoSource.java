package com.seventv.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SevenVideoSource{

    public final static String FEMBED = "21";
    public final static String AVGLE = "17";
    public final static String VERYSTREAM = "23";
    public final static String OPENLOAD = "3";
    public final static String RAPIDVIDEO = "18";
    public final static String VSHARE = "9";
    public final static String GDRIVE = "13";
    public final static String THISAV = "22";
    public final static String BITPORNO = "11";
    public final static String STREAMCHERRY = "19";
    public final static String BESTJAVPORN = "1000";

    public final static List<String> AVAILABLE =  new ArrayList<>(Arrays.asList(FEMBED, RAPIDVIDEO, VERYSTREAM, BESTJAVPORN, AVGLE));
    public final static Map<String, String> SOURCE_NAME = new HashMap<String, String>(){{
        put(FEMBED, "Fembed");
        put(AVGLE, "Avgle");
        put(VERYSTREAM, "Verystream");
        put(OPENLOAD, "Openload");
        put(RAPIDVIDEO, "Rapidvideo");
        put(VSHARE, "Vshare");
        put(GDRIVE, "GDrive");
        put(THISAV, "ThisAV");
        put(BITPORNO, "Bitporno");
        put(STREAMCHERRY, "Streamcherry");
        put(BESTJAVPORN, "BestJavPorn");
    }};

    protected Map<String, List<VideoUrl>> mVideoSources = new HashMap<>();

    public void addSource(String id, String url){
        if(!AVAILABLE.contains(id)){
            return;
        }
        if (!mVideoSources.containsKey(id)){
            mVideoSources.put(id, new ArrayList<VideoUrl>());
        }
        mVideoSources.get(id).add(new VideoUrl(url, true));
    }

    public Map<String, List<VideoUrl>> getVideoSources(){
        return mVideoSources;
    }

    public boolean hasAvailableSource(){
        return !mVideoSources.isEmpty();
    }

    public boolean needMoreSource(){
        return !hasAvailableSource() || (mVideoSources.keySet().size() == 1 && mVideoSources.containsKey(AVGLE));
    }

    public static class VideoUrl implements Serializable{
        public String url;
        public boolean needRedirect;

        public VideoUrl(String url, boolean needRedirect){
            this.url = url;
            this.needRedirect = needRedirect;
        }
    }

    public int numPart(String source){
       return mVideoSources.get(source).size();
    }

}
