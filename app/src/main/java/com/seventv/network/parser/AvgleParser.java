package com.seventv.network.parser;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seventv.BuildConfig;
import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.model.SevenVideoSource;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AvgleParser {

    public static void parseVideo(String json, Map<String, SevenVideoSource.VideoUrl> map) throws IOException {
        if(BuildConfig.DEBUG){
            Log.d("AVGLE SEARCH", "response: " + json);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jNode = mapper.readTree(json).get("response").get("videos").get(0).get("vid");
        String vid = mapper.treeToValue(jNode, String.class);
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        map.put(SevenTVApplication.myGetString(R.string.standard), new SevenVideoSource.VideoUrl(String.format("http://api.rekonquer.com/psvs/mp4.php?vid=%s&ts=%s&sign=%s", vid, ts, encodeMD5(vid, ts)), false));
    }

    private static String encodeMD5(String s1, String s2) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(String.format("%s%sBrynhildr", s1, s2).getBytes());
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
