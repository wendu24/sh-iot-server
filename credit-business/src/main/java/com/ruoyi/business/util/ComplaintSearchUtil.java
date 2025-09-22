package com.ruoyi.business.util;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComplaintSearchUtil {

    static List<String> fieldNames = Arrays.asList("complaintCode","complaintTimestamp","complainterPhone","companyName","companyType","complaintType","context");
    static MilvusClientV2 client;
    static {
        client = new MilvusClientV2(ConnectConfig.builder()
                .uri("http://192.168.66.128:19530")
                .token("root:Milvus")
                .build());
    }

    public static List<Map<String, Object>> searchByVector(FloatVec queryVector,Integer searchNum){
//        FloatVec queryVector = new FloatVec(new float[]{0.3580376395471989f, -0.6023495712049978f, 0.18414012509913835f, -0.26286205330961354f, 0.9029438446296592f});
        SearchReq searchReq = SearchReq.builder()
                .collectionName("complaint_12315")
                .annsField("contextVec")
                .outputFields(fieldNames)
                .data(Collections.singletonList(queryVector))
                .topK(searchNum)
                .build();

        SearchResp searchResp = client.search(searchReq);

        List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            System.out.println("TopK results:");
            for (SearchResp.SearchResult result : results) {
                System.out.println(result);
            }
        }
        return searchResults.stream().flatMap(List::stream).map(SearchResp.SearchResult::getEntity).collect(Collectors.toList());
    }

}
