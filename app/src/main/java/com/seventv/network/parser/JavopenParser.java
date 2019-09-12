package com.seventv.network.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class JavopenParser {

    public static List<String> parsePageUrl(String html){
        Document doc = Jsoup.parse(html);
        Element nResult = doc.getElementsByClass("page-title").first();
        List<String> urls = new ArrayList<>();
        if (!nResult.text().split(" ")[1].equals("0")){
            Elements videos = doc.getElementsByClass("item-img");
            for (int i = 0; i < videos.size() && i < 3; i ++){
                String[] split = videos.get(i).getElementsByTag("a").first().attr("href").split("/");
                urls.add(split[split.length - 1].isEmpty()?split[split.length - 2]:split[split.length - 1]);
            }
        }
        return urls;
    }

    public static String parseSource(String html){
        Document doc = Jsoup.parse(html);
        return doc.getElementsByTag("iframe").first().attr("src");
    }
}
