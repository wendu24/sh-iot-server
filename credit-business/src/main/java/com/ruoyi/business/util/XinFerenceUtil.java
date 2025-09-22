package com.ruoyi.business.util;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.ruoyi.common.domain.RerankRequestVo;
import com.ruoyi.common.domain.milvus.RerankResponseVo;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XinFerenceUtil {
    private final static String baseUrl = "http://192.168.3.77:9997";
    private final static String modelName = "bge-reranker-v2-m3";
    private final static Gson gson = new Gson();
    private final static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static RerankResponseVo rerank(String query, List<Map<String,Object>> docs) throws IOException {

        String endpoint = "/v1/rerank";
        HttpPost httpPost = new HttpPost(baseUrl + endpoint);
        httpPost.setHeader("Content-Type", "application/json");

        // 构建请求体
        RerankRequestVo rerankRequest = new RerankRequestVo(modelName,query, docs);
        String requestJson = gson.toJson(rerankRequest);
        httpPost.setEntity(new StringEntity(requestJson, StandardCharsets.UTF_8));
        System.out.println(JSONObject.toJSONString(httpPost));
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            if (statusCode == 200) {
                return gson.fromJson(responseBody, RerankResponseVo.class);
            } else {
                throw new IOException("HTTP error code: " + statusCode + "\n" + responseBody);
            }
        }


    }



}
