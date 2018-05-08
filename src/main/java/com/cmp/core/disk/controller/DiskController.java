package com.cmp.core.disk.controller;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.JsonUtil;
import com.cmp.core.disk.model.req.ReqModifyDisk;
import com.cmp.core.disk.model.res.ResDiskInfo;
import com.cmp.core.disk.model.res.ResDisks;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.HEADER_CLOUD_ID;
import static com.cmp.core.common.ErrorEnum.ERR_MODIFY_DISK_NAME_BODY;
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

    /**
     * 修改硬盘名称
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException 异常
     */
    @PutMapping("/disks/modifyName")
    @ResponseBody
    public CompletionStage<JsonNode> modifyDiskName(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqModifyDisk reqModifyDisk = JsonUtil.stringToObject(body, ReqModifyDisk.class);
        return getCloudEntity(request).thenCompose(cloud ->
                checkModifyDiskNameBody(reqModifyDisk).thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_MODIFY_DISK_NAME_BODY);
                    } else {
                        return httpPut("/disks/modifyName", JsonUtil.objectToString(reqModifyDisk), cloud)
                                .thenApply(resData -> okFormat(resData.getCode(), null, response));
                    }
                })
        ).exceptionally(e -> badFormat(e, response));
    }

    private CompletionStage<Boolean> checkModifyDiskNameBody(ReqModifyDisk body) {
        return CompletableFuture.supplyAsync(() ->
                (null != body.getDiskId()
                        && null != body.getRegionId()
                        && null != body.getDiskName())
        );
    }
}
