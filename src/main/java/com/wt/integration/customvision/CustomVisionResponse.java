package com.wt.integration.customvision;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomVisionResponse {

    public List<Prediction> predictions = new ArrayList<>();

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Prediction {

    public double probability;
    public String tagName;

}
