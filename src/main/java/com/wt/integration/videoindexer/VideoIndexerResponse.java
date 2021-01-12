package com.wt.integration.videoindexer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoIndexerResponse {

    private List<Video> videos = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Video {

    private Insights insights;

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Insights {

    private List<Shot> shots = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Shot {

    private List<Keyframe> keyFrames = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Keyframe {

    private List<Instance> instances = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Instance {

    private String thumbnailId;

}

