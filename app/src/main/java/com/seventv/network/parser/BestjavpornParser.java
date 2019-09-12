package com.seventv.network.parser;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seventv.model.SevenVideoSource;
import com.seventv.network.parser.item.FembedVideo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;

public class BestjavpornParser {

    public static String parsePageUrl(String html){
        Document doc = Jsoup.parse(html);
        Element item = doc.getElementsByTag("article").first();
        if (item != null){
            String[] split = item.getElementsByTag("a").first().attr("href").split("/");
            return split[split.length - 1].isEmpty()?split[split.length - 2]:split[split.length - 1];
        }
        return "";
    }

    public static String parseSource(String html){
        Document doc = Jsoup.parse(html);
        String script = doc.getElementsByClass("responsive-player").first().getElementsByTag("script").first().html();
        String key = "playerid=";
        int a = script.indexOf(key);
        int b = script.indexOf("\"", a);
        return script.substring(a + key.length(), b);
    }

    public static void parseVideo(String html, Map<String, SevenVideoSource.VideoUrl> map){
        int a = html.lastIndexOf("sources");
        a = html.indexOf("[", a);
        int b = html.indexOf("]", a);
        String json = html.substring(a, b+1).replace("file", "\"file\"").replace("label","\"label\"");
        json = json.substring(0, json.length()-2) + "]";
        ObjectMapper mapper = new ObjectMapper();
        try {
            FembedVideo[] fembedVideos = mapper.readValue(json, FembedVideo[].class);
            for (FembedVideo fembedVideo : fembedVideos) {
                map.put(fembedVideo.getLabel(), new SevenVideoSource.VideoUrl(fembedVideo.getFile(), false));
            }
        }catch (Exception e){
            Log.e("BestjavpornParser", "Error: " + e);
        }
    }
}
