package com.ruoyi.common.domain.milvus;

import lombok.Data;

import java.util.List;

@Data
public class RerankResponseVo {

    private String id;

    private List<Result> results;

    @Data
    public class Result{

        private Integer index;

        private Float relevance_score;

        private String document;
    }


}
