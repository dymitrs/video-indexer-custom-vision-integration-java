package com.wt.integration.videoindexer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wt.integration.ConfigVariables.VI_SUBSCRIPTION_KEY;

public final class VideoIndexerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoIndexerService.class);

    private static final String BASE_URI = "https://api.videoindexer.ai";
    private static final String LOCATION = "westeurope";
    private static final String AUTH_SERVICE_PATH = "/Auth/%s/Accounts/%s/AccessToken";
    private static final String INDEX_SERVICE_PATH = "/%s/Accounts/%s/Videos/%s/Index";
    private static final String THUMBNAIL_SERVICE_PATH = "/%s/Accounts/%s/Videos/%s/Thumbnails/%s";

    private static final String OCP_HEADER = "Ocp-Apim-Subscription-Key";

    public static String getAccessToken(String accountId) throws IOException {
        String accessToken;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet(BASE_URI + String.format(AUTH_SERVICE_PATH, LOCATION, accountId));
            httpGet.setHeader(OCP_HEADER, VI_SUBSCRIPTION_KEY);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                accessToken = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }

        return Optional.ofNullable(accessToken)
                .map(body -> body.replace("\"", ""))
                .map(body -> body.replaceAll("(\r\n|\n)", ""))
                .orElse(null);
    }

    public static VideoIndexerResponse getIndexedVideo(String accessToken, String accountId, String videoId) throws IOException {
        VideoIndexerResponse videoIndexerResponse = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet(BASE_URI + String.format(INDEX_SERVICE_PATH, LOCATION, accountId, videoId));
            httpGet.setHeader(OCP_HEADER, VI_SUBSCRIPTION_KEY);
            httpGet.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                videoIndexerResponse = objectMapper.readValue(response.getEntity().getContent(), VideoIndexerResponse.class);
            }
        }

        return videoIndexerResponse;
    }

    public static byte[] getThumbnail(String accessToken, String accountId, String videoId, String thumbnailId) throws IOException {
        LOGGER.info("Getting thumbnailId {}...", thumbnailId);

        byte[] image;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet(BASE_URI + String.format(THUMBNAIL_SERVICE_PATH, LOCATION, accountId, videoId, thumbnailId));
            httpGet.setHeader(OCP_HEADER, VI_SUBSCRIPTION_KEY);
            httpGet.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                image = EntityUtils.toByteArray(response.getEntity());
            }
        }

        return image;
    }

    public static List<String> toThumbnailIds(VideoIndexerResponse videoIndexerResponse) {
        return videoIndexerResponse.getVideos().stream()
                .map(Video::getInsights)
                .flatMap(insights -> insights.getShots().stream())
                .flatMap(shot -> shot.getKeyFrames().stream())
                .flatMap(keyframe -> keyframe.getInstances().stream())
                .map(Instance::getThumbnailId)
                .collect(Collectors.toList());
    }

}
