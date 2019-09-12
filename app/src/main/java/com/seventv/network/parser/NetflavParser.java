package com.seventv.network.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NetflavParser {

    public static String parsePageUrl(String html, String id){
        int start = html.lastIndexOf("\"docs\"");
        final int MAX = 2;
        for (int i = 0; i < MAX; i++){
            start = html.indexOf("\"title\"", start);
            int a = start + "\"title\":\"".length();
            start = html.indexOf("\"", a);
            String title = html.substring(a, start);
            if (title.toLowerCase().contains(id.toLowerCase()) && title.contains("中文")){
                start = html.indexOf("\"videoId\"", start);
                a = start + "\"videoId\":\"".length();
                start = html.indexOf("\"", a);
                return html.substring(a, start);
            }
        }
        return "";
    }

    public static String parseSource(String html){
        Document doc = Jsoup.parse(html);
        return doc.getElementById("iframe-block").attr("src");
    }
}
