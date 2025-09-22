package com.ruoyi.business.util;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.ruoyi.common.domain.ComplaintVO;
import com.ruoyi.common.domain.milvus.RerankResponseVo;
import com.ruoyi.common.utils.DateUtils;
import io.milvus.client.*;
import io.milvus.grpc.*;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.*;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.v2.service.vector.request.data.FloatVec;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MilvusClientContext {
    private  static MilvusClient client;
    private  static String collectionName = "text_embedding_demo22";
    // 向量维度，根据你的模型调整
    private final int dimension = 1024;

    static {
        client = new MilvusServiceClient(ConnectParam.newBuilder()
                .withHost("192.168.66.128")
                .withPort(19530)
                .build());
    }


    public static List<Map<String, Object>> search(String contextQueryParam,Integer searchNum) throws IOException {
        /**
         * 获取查询参数的向量
         */
        List<Float> embedding = VectorUtil.generateEmbedding("为这个问题寻找答案:哪些内容涉及" + contextQueryParam);
        FloatVec floatVec = new FloatVec(embedding);
        /**
         * 到milvus数据库中查询
         */
        List<Map<String, Object>> results = ComplaintSearchUtil.searchByVector(floatVec,searchNum);
        /**
         * 重新排序
         */
        RerankResponseVo rerank = XinFerenceUtil.rerank(contextQueryParam, results);
        List<Map<String, Object>> finalResults = new ArrayList<>(results.size());
        rerank.getResults().forEach(result -> {
            finalResults.add(results.get(result.getIndex()));
        });

        System.out.println("排序之后的数据");
        finalResults.forEach(oneResult ->{
            Object complaintTimestamp = oneResult.get("complaintTimestamp");
            if(Objects.nonNull(complaintTimestamp)){
                Date time = new Date(Long.valueOf(complaintTimestamp.toString()));
                oneResult.put("complaintTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,time));
            }
        });
        return finalResults;
    }


    // 插入向量和文本
    public  static List<Long> insertVectors(List<ComplaintVO> complaintVOS)  {
        // 获取向量
        complaintVOS.forEach(complaintVO -> {
            try {
                complaintVO.setContextVecs(VectorUtil.generateEmbedding(complaintVO.getContext()));
//                complaintVO.setComplaintTypeVecs(VectorUtil.generateEmbedding(complaintVO.getComplaintType()));
//                complaintVO.setCompanyTypeVecs(VectorUtil.generateEmbedding(complaintVO.getCompanyType()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        // 插入
        InsertParam insertParam = ComplaintInsertUtil.buildInsertParam(complaintVOS);
        R<MutationResult> resultR = client.insert(insertParam);
        if (resultR.getStatus() != R.Status.Success.getCode()) {
            System.err.println("插入失败: " + resultR.getMessage());
            return Collections.emptyList();
        }

         return resultR.getData().getIDs().getIntId().getDataList();
    }

    // 根据ID删除记录
    public void deleteByIds(List<Long> ids) {
        String idStr = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        DeleteParam param = DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr("id in [" + idStr + "]")
                .build();

        R<MutationResult> response = client.delete(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.err.println("删除失败: " + response.getMessage());
        }
    }

    public void loadCollection(){
        LoadCollectionParam loadCollectionParam = LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        client.loadCollection(loadCollectionParam);
    }

    // 根据ID查询记录
    public void queryByIds(List<Long> ids) {
        String idStr = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        QueryParam param = QueryParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr("id in [" + idStr + "]")
                .withOutFields(Arrays.asList("id", "companyTypeVec", "complaintTypeVec","contextVec"))
                .build();

        R<QueryResults> response = client.query(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.err.println("查询失败: " + response.getMessage());
            return;
        }

        QueryResultsWrapper wrapper = new QueryResultsWrapper(response.getData());
        wrapper.getRowRecords().forEach(rowRecord -> {
            System.out.println(JSONObject.toJSONString(rowRecord.getFieldValues()));
        });

    }

    // 向量相似度搜索
    public void searchVectors(List<Float> queryVector, int topK) {
        SearchParam param = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(MetricType.COSINE)
                .withOutFields(Arrays.asList("id", "text"))
                .withTopK(topK)
                .withVectors(Collections.singletonList(queryVector))
                .withVectorFieldName("embedding")
                .withExpr("")
                .withParams("{\"ef\":100}")
                .build();

        R<SearchResults> response = client.search(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.err.println("搜索失败: " + response.getMessage());
            return;
        }

        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        wrapper.getIDScore(0).forEach(row->{
            System.out.println(JSONObject.toJSONString(row.getFieldValues()));
        });
    }

    // 释放集合(释放内存)
    public void releaseCollection() {
        ReleaseCollectionParam param = ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<RpcStatus> response = client.releaseCollection(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.err.println("释放集合失败: " + response.getMessage());
        }
    }

    // 删除集合
    public void dropCollection() {
        DropCollectionParam param = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<RpcStatus> response = client.dropCollection(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.err.println("删除集合失败: " + response.getMessage());
        }
    }

    // 关闭客户端连接
    public static void close() {
        client.close();
    }

    // 示例用法
    public static void main(String[] args) {


        try {
            String fileName = "C:\\Users\\fzzhangg\\Desktop\\12315数据\\2025\\4月数据汇总.xlsx";
            List<ComplaintVO> complaintVOS = ExcelUtil.parseExcel(fileName);
            System.out.println("解析出来的数据条数" + complaintVOS.size());
            Lists.partition(complaintVOS,100).forEach(subList ->{
                try {
                    List<Long> ids = MilvusClientContext.insertVectors(subList);
                    System.out.println("插入成功，ID列表: " + ids);
                    // 刷新集合使数据可用
                    MilvusClientContext.flushCollection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


//            util.search("为这个问题寻找答案:哪些内容涉及" + "食物中毒，比如吃完东西后头痛、呕吐、腹泻等");
//            MilvusClientContext.search("为这个问题寻找答案:哪些内容涉及" + "进口商品",100);



//            util.loadCollection();
            // 查询



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 清理资源
            // util.dropCollection();
            MilvusClientContext.close();
        }
    }

    // 刷新集合(使插入的数据立即可用)
    private static void flushCollection() {
        FlushParam param = FlushParam.newBuilder()
                .addCollectionName(collectionName)
                .build();

        R<FlushResponse> responseR = client.flush(param);
        if (responseR.getStatus() != R.Status.Success.getCode()) {
            System.err.println("刷新集合失败: " + responseR.getMessage());
        }
    }
}
