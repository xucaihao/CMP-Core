package com.cmp.core.snapshot.controller;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.JsonUtil;
import com.cmp.core.snapshot.model.req.ReqCreSnapshot;
import com.cmp.core.snapshot.model.res.ResSnapshot;
import com.cmp.core.snapshot.model.res.ResSnapshotInfo;
import com.cmp.core.snapshot.model.res.ResSnapshots;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.HEADER_CLOUD_ID;
import static com.cmp.core.common.ErrorEnum.ERR_CREATE_SNAPSHOT_BODY;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("")
public class SnapshotController extends BaseController {

    /**
     * 查询快照列表
     *
     * @param request  http请求
     * @param response http响应
     * @return 快照列表
     */
    @RequestMapping("/snapshots")
    @ResponseBody
    public CompletionStage<JsonNode> describeSnapshots(
            final HttpServletRequest request, final HttpServletResponse response) {
        if (null != request.getHeader(HEADER_CLOUD_ID)) {
            return getCloudEntity(request).thenCompose(cloud ->
                    httpGet("/snapshots", ResSnapshots.class, cloud)
                            .thenApply(resData -> {
                                List<ResSnapshotInfo> snapshots = resData.getData().getSnapshots();
                                addCloudInfo(snapshots, cloud);
                                return okFormat(resData.getCode(), new ResSnapshots(snapshots), response);
                            })
            ).exceptionally(e -> badFormat(e, response));
        } else {
            return getAllCloudEntity(request, true)
                    .thenApply(cloudList -> {
                        List<CompletionStage<List<ResSnapshotInfo>>> futures = cloudList.stream().map(cloud ->
                                httpGet("/snapshots", ResSnapshots.class, cloud)
                                        .thenApply(resData -> {
                                            List<ResSnapshotInfo> snapshots = resData.getData().getSnapshots();
                                            addCloudInfo(snapshots, cloud);
                                            return snapshots;
                                        }).exceptionally(e -> dealException(e, cloud))
                        ).collect(toList());
                        List<ResSnapshotInfo> snapshots = aggregateList(this.joinRes(futures));
                        return okFormat(OK.value(), new ResSnapshots(snapshots), response);
                    }).exceptionally(e -> badFormat(e, response));
        }
    }

    /**
     * 查询指定快照
     *
     * @param request    http请求 http请求
     * @param response   http响应 http响应
     * @param regionId   区域id
     * @param snapshotId 快照id
     * @return 指定快照信息
     */
    @RequestMapping("/{regionId}/snapshots/{snapshotId}")
    @ResponseBody
    public CompletionStage<JsonNode> describeSnapshotAttribute(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable String regionId,
            @PathVariable String snapshotId) {
        return getCloudEntity(request).thenCompose(cloud ->
                httpGet("/" + regionId + "/snapshots/" + snapshotId, ResSnapshot.class, cloud)
                        .thenApply(resData -> {
                            ResSnapshotInfo snapshot = resData.getData().getSnapshot();
                            addCloudInfo(snapshot, cloud);
                            return okFormat(resData.getCode(), new ResSnapshot(snapshot), response);
                        })
        ).exceptionally(e -> badFormat(e, response));
    }

    /**
     * 创建快照
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException 异常
     */
    @PostMapping("/snapshots")
    @ResponseBody
    public CompletionStage<JsonNode> createSnapshot(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqCreSnapshot reqCreSnapshot = JsonUtil.stringToObject(body, ReqCreSnapshot.class);
        return getCloudEntity(request).thenCompose(cloud ->
                checkCreateSnapshotBody(reqCreSnapshot).thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_CREATE_SNAPSHOT_BODY);
                    } else {
                        return httpPost("/snapshots", JsonUtil.objectToString(reqCreSnapshot), cloud)
                                .thenApply(resData -> okFormat(resData.getCode(), null, response));
                    }
                })
        ).exceptionally(e -> badFormat(e, response));
    }

    /**
     * 删除快照
     *
     * @param request    http请求 http请求
     * @param response   http响应 http响应
     * @param regionId   区域id
     * @param snapshotId 快照id
     * @return 操作结果
     */
    @DeleteMapping("/{regionId}/snapshots/{snapshotId}")
    @ResponseBody
    public CompletionStage<JsonNode> deleteSnapshot(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable String regionId,
            @PathVariable String snapshotId) {
        return getCloudEntity(request)
                .thenCompose(cloud -> httpDel("/" + regionId + "/snapshots/" + snapshotId, cloud))
                .thenApply(x -> okFormat(x.getCode(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    private CompletionStage<Boolean> checkCreateSnapshotBody(ReqCreSnapshot body) {
        return CompletableFuture.supplyAsync(() ->
                (null != body.getDiskId()
                        && null != body.getRegionId()
                        && null != body.getSnapshotName())
        );
    }

}
