package com.seventv.network.parser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seventv.model.SevenVideoSource;
import com.seventv.network.parser.item.FembedVideo;

import java.io.IOException;

import java.util.Map;

public class FembedParser {

    public static void parseVideo(String json, Map<String, SevenVideoSource.VideoUrl> map) throws IOException {
        ObjectMapper mapper1 = new ObjectMapper();
        JsonNode jNode1 = mapper1.readTree(json).get("data");
        FembedVideo[] fembedVideos = mapper1.treeToValue(jNode1, FembedVideo[].class);
        for (FembedVideo fembedVideo : fembedVideos) {
            map.put(fembedVideo.getLabel(), new SevenVideoSource.VideoUrl(fembedVideo.getFile(), true));
        }
    }
}
