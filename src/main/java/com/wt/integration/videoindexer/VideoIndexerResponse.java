package com.wt.integration.videoindexer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoIndexerResponse {

    public List<Video> videos = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Video {

    public Insights insights;

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Insights {

    public List<Shot> shots = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Shot {

    public List<Keyframe> keyFrames = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Keyframe {

    public List<Instance> instances = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Instance {

    public String thumbnailId;

}

