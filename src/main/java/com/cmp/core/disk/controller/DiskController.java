package com.cmp.core.disk.controller;

import com.cmp.core.common.BaseController;
import com.cmp.core.disk.model.res.ResDiskInfo;
import com.cmp.core.disk.model.res.ResDisks;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.HEADER_CLOUD_ID;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("")
public class DiskController extends BaseController {

    /**
     * 查询硬盘列表
     *
     * @param request  http请求 http请求
     * @param response http响应 http响应
     * @return 硬盘列表
     */
    @RequestMapping("/disks")
    @ResponseBody
    public CompletionStage<JsonNode> describeDisks(
            final HttpServletRequest request, final HttpServletResponse response) {
        if (null != request.getHeader(HEADER_CLOUD_ID)) {
            return getCloudEntity(request).thenCompose(cloud ->
                    httpGet("/disks", ResDisks.class, cloud)
                            .thenApply(resData -> {
                                List<ResDiskInfo> disks = resData.getData().getDisks();
                                addCloudInfo(disks, cloud);
                                return okFormat(resData.getCode(), new ResDisks(disks), response);
                            })
            ).exceptionally(e -> badFormat(e, response));
        } else {
            return getAllCloudEntity(request, true)
                    .thenApply(cloudList -> {
                        List<CompletionStage<List<ResDiskInfo>>> futures = cloudList.stream().map(cloud ->
                                httpGet("/disks", ResDisks.class, cloud)
                                        .thenApply(resData -> {
                                            List<ResDiskInfo> disks = resData.getData().getDisks();
                                            addCloudInfo(disks, cloud);
                                            return disks;
                                        }).exceptionally(e -> dealException(e, cloud))
                        ).collect(toList());
                        List<ResDiskInfo> disks = aggregateList(this.joinRes(futures));
                        return okFormat(OK.value(), new ResDisks(disks), response);
                    }).exceptionally(e -> badFormat(e, response));
        }
    }
}
