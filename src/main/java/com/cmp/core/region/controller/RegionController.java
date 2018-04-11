package com.cmp.core.region.controller;

import com.cmp.core.common.BaseController;
import com.cmp.core.region.model.RegionEntity;
import com.cmp.core.region.model.ResRegions;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Controller
public class RegionController extends BaseController {

    /**
     * 查询地域列表
     *
     * @param request  http请求
     * @param response http响应
     * @return 地域列表
     */
    public CompletionStage<JsonNode> describeRegions(
            final HttpServletRequest request, final HttpServletResponse response) {
        return getCloudEntity(request).thenCompose(cloud ->
                httpGet("/regions", ResRegions.class, cloud)
                        .thenApply(resData -> {
                            List<RegionEntity> regions = resData.getData().getRegions();
                            addCloudInfo(regions, cloud);
                            return okFormat(resData.getCode(), new ResRegions(regions), response);
                        })
        ).exceptionally(e -> badFormat(e, response));
    }

}
