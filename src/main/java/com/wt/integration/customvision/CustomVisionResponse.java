package com.wt.integration.customvision;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomVisionResponse {

    private List<Prediction> predictions = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Prediction {

    private double probability;
    private String tagName;

}
