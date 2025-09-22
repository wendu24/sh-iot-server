package com.ruoyi.business.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class VectorUtil {
    private final static String baseUrl = "http://192.168.3.77:8003";
    private final static String modelName = "bge-m3:latest";
    private final static Gson gson = new Gson();
    private final static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static List<Float> generateEmbedding(String text) throws IOException {
        String endpoint = "/api/embeddings";
        HttpPost httpPost = new HttpPost(baseUrl + endpoint);
        httpPost.setHeader("Content-Type", "application/json");

        // 构建请求体
        EmbeddingRequest request = new EmbeddingRequest();
        request.model = modelName;
        request.prompt = text;

        String requestJson = gson.toJson(request);
        httpPost.setEntity(new StringEntity(requestJson, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            if (statusCode == 200) {
                return gson.fromJson(responseBody, EmbeddingResponse.class).getEmbedding();
            } else {
                throw new IOException("HTTP error code: " + statusCode + "\n" + responseBody);
            }
        }
    }

    // 请求和响应类保持不变
    public static class EmbeddingRequest {
        String model;
        String prompt;
        @SerializedName("options")
        Options options = new Options();

        static class Options {
            @SerializedName("temperature")
            double temperature = 0;
        }
    }

    public static class EmbeddingResponse {
        String model;
        @SerializedName("embedding")
        List<Float> embedding;
        @SerializedName("created_at")
        String createdAt;

        public List<Float> getEmbedding() {
            return embedding;
        }
    }

    public static void main(String[] args) {
        try {
            List<Float> embedding = VectorUtil.generateEmbedding("这是一个测试文本");
            System.out.println("向量维度: " + embedding.size());
            System.out.println("向量前10个元素: " + embedding.subList(0, Math.min(10, embedding.size())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}