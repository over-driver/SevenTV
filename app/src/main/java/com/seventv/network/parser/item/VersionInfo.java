package com.seventv.network.parser.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionInfo {
    public long versionCode;
    public String versionName;
}
