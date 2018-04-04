package com.cmp.core.instance.controller;

import com.cmp.core.common.BaseController;
import com.cmp.core.common.ResData;
import com.cmp.core.instance.model.res.ResInstanceInfo;
import com.cmp.core.instance.model.res.ResInstances;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.CMP_V1;
import static com.cmp.core.common.Constance.HEADER_CLOUD_ID;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/instances")
public class instanceController extends BaseController {

    public CompletionStage<JsonNode> describeInstances(
            final HttpServletRequest request, final HttpServletResponse response) {
        if (null != request.getHeader(HEADER_CLOUD_ID)) {
            return getCloudEntity(request).thenCompose(cloud ->
                    httpGet(CMP_V1 + "/instances", ResInstances.class, cloud)
                            .thenApply(resData -> {
                                List<ResInstanceInfo> instances = resData.getData().getInstances();
                                addCloudInfo(instances, cloud);
                                return okFormat(resData.getCode(), new ResInstances(instances), response);
                            })
            ).exceptionally(e -> badFormat(e, response));
        } else {
            return getAllCloudEntity(request)
                    .thenApply(cloudList -> {
                        List<CompletionStage<List<ResInstanceInfo>>> futures = cloudList.stream().map(cloud ->
                                httpGet(CMP_V1 + "/instances", ResInstances.class, cloud)
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
}
