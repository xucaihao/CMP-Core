package com.cmp.core.instance.controller;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.ErrorEnum;
import com.cmp.core.common.JsonUtil;
import com.cmp.core.instance.model.req.ReqCloseInstance;
import com.cmp.core.instance.model.req.ReqStartInstance;
import com.cmp.core.instance.model.res.ResInstanceInfo;
import com.cmp.core.instance.model.res.ResInstances;
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
import static com.cmp.core.common.ErrorEnum.ERR_CLOSE_INSTANCE_BODY;
import static com.cmp.core.common.ErrorEnum.ERR_START_INSTANCE_BODY;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("")
public class instanceController extends BaseController {

    /**
     * 查询主机列表
     *
     * @param request  http请求
     * @param response http响应
     * @return 主机列表
     */
    @RequestMapping("/instances")
    @ResponseBody
    public CompletionStage<JsonNode> describeInstances(
            final HttpServletRequest request, final HttpServletResponse response) {
        if (null != request.getHeader(HEADER_CLOUD_ID)) {
            return getCloudEntity(request).thenCompose(cloud ->
                    httpGet("/instances", ResInstances.class, cloud)
                            .thenApply(resData -> {
                                List<ResInstanceInfo> instances = resData.getData().getInstances();
                                addCloudInfo(instances, cloud);
                                return okFormat(resData.getCode(), new ResInstances(instances), response);
                            })
            ).exceptionally(e -> badFormat(e, response));
        } else {
            return getAllCloudEntity(request, true)
                    .thenApply(cloudList -> {
                        List<CompletionStage<List<ResInstanceInfo>>> futures = cloudList.stream().map(cloud ->
                                httpGet("/instances", ResInstances.class, cloud)
                                        .thenApply(resData -> {
                                            List<ResInstanceInfo> instances = resData.getData().getInstances();
                                            addCloudInfo(instances, cloud);
                                            return instances;
                                        }).exceptionally(e -> dealException(e, cloud))
                        ).collect(toList());
                        List<ResInstanceInfo> instances = aggregateList(this.joinRes(futures));
                        return okFormat(OK.value(), new ResInstances(instances), response);
                    }).exceptionally(e -> badFormat(e, response));
        }
    }

    /**
     * 关闭主机
     *
     * @param request  http请求
     * @param response http响应
     * @return 关闭主机
     * @throws IOException 异常
     */
    @PutMapping("/instances/close")
    @ResponseBody
    public CompletionStage<JsonNode> closeInstance(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqCloseInstance reqCloseInstance = JsonUtil.stringToObject(body, ReqCloseInstance.class);
        return getCloudEntity(request).thenCompose(cloud ->
                checkCloseInstanceBody(reqCloseInstance).thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_CLOSE_INSTANCE_BODY);
                    } else {
                        return httpPut("/instances/close", JsonUtil.objectToString(reqCloseInstance), cloud)
                                .thenApply(resData -> okFormat(resData.getCode(), null, response));
                    }
                })
        ).exceptionally(e -> badFormat(e, response));
    }

    /**
     * 启动主机
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException 异常
     */
    @PutMapping("/instances/start")
    @ResponseBody
    public CompletionStage<JsonNode> startInstance(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqStartInstance reqStartInstance = JsonUtil.stringToObject(body, ReqStartInstance.class);
        return getCloudEntity(request).thenCompose(cloud ->
                checkStartInstanceBody(reqStartInstance).thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_START_INSTANCE_BODY);
                    } else {
                        return httpPut("/instances/start", JsonUtil.objectToString(reqStartInstance), cloud)
                                .thenApply(resData -> okFormat(resData.getCode(), null, response));
                    }
                })
        ).exceptionally(e -> badFormat(e, response));
    }

    private CompletionStage<Boolean> checkCloseInstanceBody(ReqCloseInstance body) {
        return CompletableFuture.supplyAsync(() ->
                (null != body.getInstanceId()
                        || null != body.getRegionId())
        );
    }

    private CompletionStage<Boolean> checkStartInstanceBody(ReqStartInstance body) {
        return CompletableFuture.supplyAsync(() ->
                (null != body.getInstanceId()
                        || null != body.getRegionId())
        );
    }

}
