package com.ruoyi.common.domain;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.List;

@Data
@ExcelIgnoreUnannotated
public class ComplaintVO {

    @ExcelProperty("登记编号")
    private String  complaintCode;

    @ExcelProperty("登记日期")
    private String  complaintTime;


    @ExcelProperty("提供方联系方式")
    private String  complainterPhone;

    @ExcelProperty("企业名称")
    private String  companyName;

    @ExcelProperty("客体类别")
    private String  companyType;

    @ExcelProperty("问题类别")
    private String  complaintType;

    @ExcelProperty("具体问题")
    private String  context;

    private Long  complaintTimestamp;

//    private List<Float> companyTypeVecs;
//
//    private List<Float> complaintTypeVecs;

    private List<Float> contextVecs;


}
