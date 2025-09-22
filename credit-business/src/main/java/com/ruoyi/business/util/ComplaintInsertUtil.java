package com.ruoyi.business.util;

import com.ruoyi.common.domain.ComplaintVO;
import io.milvus.param.dml.InsertParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComplaintInsertUtil {

    private static final String collectionName = "complaint_12315";

    public static InsertParam buildInsertParam(List<ComplaintVO> complaintVOS) {

        InsertParam.Field complaintCode = InsertParam.Field.builder()
                .name("complaintCode")
                .values(complaintVOS.stream().map(ComplaintVO::getComplaintCode).collect(Collectors.toList()))
                .build();

        InsertParam.Field complaintTimestamp = InsertParam.Field.builder()
                .name("complaintTimestamp")
                .values(complaintVOS.stream().map(ComplaintVO::getComplaintTimestamp).collect(Collectors.toList()))
                .build();

        InsertParam.Field complainterPhone = InsertParam.Field.builder()
                .name("complainterPhone")
                .values(complaintVOS.stream().map(ComplaintVO::getComplainterPhone).collect(Collectors.toList()))
                .build();

        InsertParam.Field companyName = InsertParam.Field.builder()
                .name("companyName")
                .values(complaintVOS.stream().map(ComplaintVO::getCompanyName).collect(Collectors.toList()))
                .build();

        InsertParam.Field companyType = InsertParam.Field.builder()
                .name("companyType")
                .values(complaintVOS.stream().map(ComplaintVO::getCompanyType).collect(Collectors.toList()))
                .build();

        InsertParam.Field complaintType = InsertParam.Field.builder()
                .name("complaintType")
                .values(complaintVOS.stream().map(ComplaintVO::getComplaintType).collect(Collectors.toList()))
                .build();

        InsertParam.Field context = InsertParam.Field.builder()
                .name("context")
                .values(complaintVOS.stream().map(ComplaintVO::getContext).collect(Collectors.toList()))
                .build();

//        InsertParam.Field complaintTypeVec = InsertParam.Field.builder()
//                .name("complaintTypeVec")
//                .values(complaintVOS.stream().map(ComplaintVO::getComplaintTypeVecs).collect(Collectors.toList()))
//                .build();
//
//        InsertParam.Field companyTypeVec = InsertParam.Field.builder()
//                .name("companyTypeVec")
//                .values(complaintVOS.stream().map(ComplaintVO::getCompanyTypeVecs).collect(Collectors.toList()))
//                .build();

        InsertParam.Field contextVec = InsertParam.Field.builder()
                .name("contextVec")
                .values(complaintVOS.stream().map(ComplaintVO::getContextVecs).collect(Collectors.toList()))
                .build();

        List<InsertParam.Field> fieldsValues = Arrays.asList(
                complaintCode,
                complaintTimestamp,
                complainterPhone,
                companyName,
                companyType,
                complaintType,
                context,
//                complaintTypeVec,
//                companyTypeVec,
                contextVec
        );

        return InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fieldsValues)
                .build();

    }

}
