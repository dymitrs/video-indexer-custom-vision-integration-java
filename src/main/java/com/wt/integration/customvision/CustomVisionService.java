package com.wt.integration.customvision;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class CustomVisionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomVisionService.class);

    private static final String BASE_URI = "https://westeurope.api.cognitive.microsoft.com";
    private static final String PREDICT_SERVICE_PATH = "/customvision/v3.0/Prediction/%s/detect/iterations/%s/image";
    private static final String PREDICT_HEADER = "Prediction-Key";

    private static final double ENSO_THRESHOLD = 0.60;

    public static CustomVisionResponse detectImage(String predictionKey, String projectId, String publishedName, byte[] image) throws IOException {
        CustomVisionResponse customVisionResponse;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost(BASE_URI + String.format(PREDICT_SERVICE_PATH, projectId, publishedName));
            httpPost.setHeader(PREDICT_HEADER, predictionKey);

            File imageFile = File.createTempFile("image", "file", null);

            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(image);
            }

            httpPost.setEntity(new FileEntity(imageFile));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                customVisionResponse = objectMapper.readValue(response.getEntity().getContent(), CustomVisionResponse.class);
            }
        }

        return customVisionResponse;
    }

    public static void detectEnsoLogo(CustomVisionResponse customVisionResponse, String thumbnailId) {
        for (Prediction prediction : customVisionResponse.getPredictions()) {
            double probability = prediction.getProbability();
            if (probability >= ENSO_THRESHOLD && "logo-enso".equals(prediction.getTagName())) {
                LOGGER.info("Detected ENSO logo in thumbnail with id {}. Probability: {}.", thumbnailId, probability);
            }
        }
    }

}
