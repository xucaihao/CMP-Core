package com.cmp.core.snapshot.controller;

import com.cmp.core.common.BaseController;
import com.cmp.core.image.model.res.ResImageInfo;
import com.cmp.core.image.model.res.ResImages;
import com.cmp.core.snapshot.model.res.ResSnapshotInfo;
import com.cmp.core.snapshot.model.res.ResSnapshots;
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
public class SnapshotController extends BaseController {

    /**
     * 查询镜像列表
     *
     * @param request  http请求
     * @param response http响应
     * @return 镜像列表
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

}
