package com.seventv.network.parser;

import android.util.Log;

import com.seventv.BuildConfig;
import com.seventv.R;
import com.seventv.model.SevenVideoSource;
import com.seventv.model.Video;
import com.seventv.model.VideoDetail;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SevenParser {

    public static List<Video> parseVideoList(String html, String category) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass("latest-korean-box-row");
        List<Video> videos = new ArrayList<>();
        for (Element element : elements){
            try {
                Video video = new Video();
                // get url of detail page
                video.setDetailUrl(element.getElementsByTag("a").first().attr("href"));
                // get url of thumbnail and preview
                Element elementVideo = element.getElementsByTag("video").first();
                if (elementVideo == null) {
                    video.setThumbnailUrl(element.getElementsByTag("img").first().attr("src"));
                } else {
                    video.setThumbnailUrl(elementVideo.attr("poster"));
                    video.setPreviewUrl(elementVideo.attr("srcmv"));
                }
                // get title and id
                String title = element.getElementsByTag("h2").first().text();
                int a = title.indexOf('[');
                int b = title.indexOf(']');
                String id;
                if (a >= 0 && b >= 0 && b > a + 1){
                    id = title.substring(a+1, b);
                    if(id.contains("中文")){
                        title = (a == 0) ? title.substring(b + 1).trim() : title.substring(0, a).trim();
                        String [] split = title.split(" ");
                        if (split[0].contains("-")){
                            id = split[0];
                            title = title.substring(title.indexOf(' ') + 1);
                        } else {
                            id = split[split.length - 1];
                            title = title.substring(0, title.lastIndexOf(' '));
                        }
                    } else {
                        title = title.substring(b + 1);
                    }
                } else {
                    b = title.indexOf(' ');
                    id = title.substring(0, b);
                    title = title.substring(b + 1);
                }
                video.setTitle(title);
                video.setId(id);
                // get upload date
                video.setDate(element.getElementsByClass("date-part").first().text().split(" ")[0]);
                videos.add(video);
            } catch (Exception e){
                e.printStackTrace();
                Log.e("SEVEN_PARSER_VIDEO_LIST", "ERROR: " + e);
            }
        }
        if(BuildConfig.DEBUG){
            Log.d("SEVEN_PARSER_VIDEO_LIST", "get " + videos.size() + " video(s)");
        }
        return videos;
    }

    public static VideoDetail parseVideoDetail(String html, String category){

        VideoDetail videoDetail = new VideoDetail();

        Document doc = Jsoup.parse(html);
        Elements sections = doc.getElementsByTag("section");
        //section 0
        Element section0 = sections.get(0);
        //cover and title
        Element img = section0.getElementsByClass("post-inner-details-img").first().child(0);
        //Element img = section0.getElementsByTag("img").first();
        videoDetail.addInfo(R.string.info_title, img.attr("title"), null);
        videoDetail.setCoverUrl(img.attr("src"));
        //infos
        Elements infos = section0.getElementsByClass("posts-message");
        int[] ids = {R.string.info_id, R.string.info_release_date, R.string.info_length, R.string.info_label, R.string.info_studio, R.string.info_director};
        for(int i = 0; i < 6; i++){
            Element linkElement = infos.get(i).getElementsByTag("a").first();
            videoDetail.addInfo(ids[i], infos.get(i).text(), (linkElement != null) ? linkElement.attr("href") : null);
        }
        // genres
        Elements spans = infos.get(6).getElementsByTag("span");
        List<String> genres = new ArrayList<>();
        for (Element span: spans){
            Element linkElement = span.getElementsByTag("a").first();
            videoDetail.addGenres((linkElement != null)? linkElement.attr("href"):null, span.text());
        }
        // idols
        Elements divs = section0.getElementsByClass("actor-right-part").first().getElementsByTag("div");
        for(int i = 1; i < divs.size(); i++){
            Element div = divs.get(i);
            Elements links = div.getElementsByTag("a");
            String avatarUrl = (links.size() == 2) ? div.getElementsByTag("img").first().attr("src") : null;
            videoDetail.addIdols(links.last().attr("href"), links.last().text(), avatarUrl);
        }
        //section 1
        //screenshot
        Elements imgs = sections.get(1).getElementsByClass("video-introduction-images-list-row").first().getElementsByTag("img");
        List<String> screenshots = new ArrayList<>();
        for(Element im: imgs){
            screenshots.add(im.attr("src"));
        }
        videoDetail.setScreenshotUrls(screenshots);
        //script
        Elements scripts = sections.get(1).getElementsByTag("script");
        String s = scripts.get(0).html();
        int rad = Integer.parseInt(s.substring(s.indexOf('=')+1, s.indexOf(';')).trim());
        char c = (char) (( rad % 25) + 97);
        int idxStar = s.indexOf('*');
        int a = Integer.parseInt(s.substring(idxStar+1, s.indexOf(')', idxStar)).trim());
        int b = Integer.parseInt(s.substring(s.lastIndexOf('=')+1, s.lastIndexOf(';')).trim());
        Matcher matcher = Pattern.compile("mvarr\\['.*?\\)").matcher(scripts.get(1).html());
        //video source
        SevenVideoSource svs = new SevenVideoSource();
        while (matcher.find()){
            // get source id and code
            String matchString =matcher.group();
            String sourceId = matchString.substring(matchString.indexOf('[')+2, matchString.indexOf('_'));
            String code = matchString.substring(matchString.indexOf('(')+2, matchString.length()-2);
            // decode
            String decode = decryptEmbeddedVideo(code, c, rad, a, b);
            Document decodeDoc = Jsoup.parse(decode);
            Elements videoSources = decodeDoc.getElementsByTag("iframe");
            for (Element videoSrc: videoSources){
                if (sourceId.equals(SevenVideoSource.AVGLE)){
                    svs.addSource(sourceId, videoSrc.attr("src") + "/" + videoDetail.getId());
                } else {
                    svs.addSource(sourceId, videoSrc.attr("src"));
                }
            }
        }
        videoDetail.setSevenVideoSource(svs);
        return videoDetail;
    }

    public static String decryptEmbeddedVideo(String string, char split, int rad, int a, int b){
        StringBuilder outString = new StringBuilder();
        String[] splitString = string.split(String.valueOf(split));
        for(String s : splitString){
            int j = (Integer.parseInt(s, rad) * a) ^ b;
            outString.append((char) j);
        }
        return outString.toString();
    }

}
