package com.ruoyi.business.controller;

import com.ruoyi.business.util.MilvusClientContext;
import com.ruoyi.business.vo.ComplaintSearchVO;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.domain.ComplaintVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/complaint")
public class ComplaintController {


    @RequestMapping("/search")
    public AjaxResult search(@RequestBody ComplaintSearchVO searchVO) throws IOException {
        List<Map<String, Object>> search = MilvusClientContext.search(searchVO.getContextQueryParam(), searchVO.getSearchNum());
        return AjaxResult.success(search);
    }


}
