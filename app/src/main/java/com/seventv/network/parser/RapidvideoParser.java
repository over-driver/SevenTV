package com.seventv.network.parser;

import com.seventv.model.SevenVideoSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

public class RapidvideoParser {

    public static void parseVideo(String html, Map<String, SevenVideoSource.VideoUrl> map){
        Document doc = Jsoup.parse(html);
        Elements downloadButtons = doc.getElementsByClass("button is-info is-outlined is-rounded");
        for (Element downloadButton : downloadButtons) {
            map.put(downloadButton.getElementsByTag("span").first().text().split(" ")[1],
                    new SevenVideoSource.VideoUrl(downloadButton.attr("href"), false));
        }
    }
}
