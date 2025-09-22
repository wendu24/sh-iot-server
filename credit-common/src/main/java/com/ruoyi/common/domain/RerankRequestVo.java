package com.ruoyi.common.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class RerankRequestVo {

    private String model;

    private String query;

    private List<String> documents;

    public RerankRequestVo(String modelName, String query, List<Map<String,Object>> docs) {
        this.model = modelName;
        this.query = query;
        this.documents = docs.stream().map(map -> map.get("context").toString()).collect(Collectors.toList());
    }
}
