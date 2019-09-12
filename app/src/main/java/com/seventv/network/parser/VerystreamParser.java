package com.seventv.network.parser;

import com.seventv.model.SevenVideoSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

public class VerystreamParser {

    public static void parseVideo(String html, Map<String, SevenVideoSource.VideoUrl> map){
        Document doc2 = Jsoup.parse(html);
        String token = doc2.getElementById("videolink").text();
        map.put("标准", new SevenVideoSource.VideoUrl("https://verystream.com/gettoken/" + token + "?download=false", true));
    }
}
