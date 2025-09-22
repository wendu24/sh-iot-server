package com.ruoyi.business.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.read.listener.PageReadListener;
import com.ruoyi.common.domain.ComplaintVO;
import com.ruoyi.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {
    private final static String  DEFAULT_TIME = "2000-01-01 00:00:01";
    private final static String  DEFAULT_STR = "-";

    public static List<ComplaintVO> parseExcel(String fileName){
        List<ComplaintVO> results = new ArrayList<>();
        // 使用 PageReadListener 来分批读取数据，避免一次性加载大量数据导致内存溢出
        EasyExcel.read(fileName)
                .sheet() // 默认读取第一个工作表
                .head(ComplaintVO.class) // 定义一个映射类来接收每一行的数据
                .registerReadListener(new PageReadListener<ComplaintVO>(dataList -> {
                    dataList.forEach(data -> {
                        if(StringUtils.isEmpty(data.getComplaintCode()))
                            data.setComplaintCode(DEFAULT_STR);
                        if(StringUtils.isEmpty(data.getContext()))
                            data.setContext(DEFAULT_STR);
                        if(StringUtils.isEmpty(data.getCompanyType()))
                            data.setCompanyType(DEFAULT_STR);
                        if(StringUtils.isEmpty(data.getCompanyName()))
                            data.setCompanyName(DEFAULT_STR);
                        if(StringUtils.isEmpty(data.getComplainterPhone()))
                            data.setComplainterPhone(DEFAULT_STR);
                        if(StringUtils.isEmpty(data.getComplaintTime()))
                            data.setComplaintTime(DEFAULT_TIME);
                        if(StringUtils.isEmpty(data.getComplaintType()))
                            data.setComplaintType(DEFAULT_STR);

                        Date date = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, data.getComplaintTime());
                        data.setComplaintTimestamp(date.getTime());
                        results.add(data);
                    });
                }))
                .doRead();
        return results;
    }
}
