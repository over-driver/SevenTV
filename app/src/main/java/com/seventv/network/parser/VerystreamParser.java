package com.seventv.network.parser;

import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.model.SevenVideoSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

public class VerystreamParser {

    public static void parseVideo(String html, Map<String, SevenVideoSource.VideoUrl> map){
        Document doc2 = Jsoup.parse(html);
        String token = doc2.getElementById("videolink").text();
        map.put(SevenTVApplication.getApp().getResources().getString(R.string.standard), new SevenVideoSource.VideoUrl("https://verystream.com/gettoken/" + token + "?download=false", true));
    }
}
