package com.cmp.core.eip.controller;

import com.cmp.core.common.BaseController;
import com.cmp.core.eip.modle.EipAddressInfo;
import com.cmp.core.eip.modle.ResEipAddresses;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.cmp.core.common.Constance.CMP_V1;
import static com.cmp.core.common.Constance.HEADER_CLOUD_ID;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/eipAddresses")
public class EipController extends BaseController {

    @RequestMapping("/regions/{regionId}")
    @ResponseBody
    public CompletionStage<JsonNode> describeEipAddresses(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable String regionId) {
        if (null != request.getHeader(HEADER_CLOUD_ID)) {
            return getCloudEntity(request).thenCompose(cloud ->
                    httpGet(CMP_V1 + "/eipAddresses/regions/" + regionId, ResEipAddresses.class, cloud)
                            .thenApply(resData -> {
                                List<EipAddressInfo> eipAddress = resData.getData().getEipAddresses();
                                addCloudInfo(eipAddress, cloud);
                                return okFormat(resData.getCode(), new ResEipAddresses(eipAddress), response);
                            })
            ).exceptionally(e -> badFormat(e, response));
        } else {
            return getAllCloudEntity(request, true)
                    .thenApply(cloudList -> {
                        List<CompletionStage<List<EipAddressInfo>>> futures = cloudList.stream().map(cloud ->
                                httpGet(CMP_V1 + "/eipAddresses/regions/" + regionId, ResEipAddresses.class, cloud)
                                        .thenApply(resData -> {
                                            List<EipAddressInfo> eipAddress = resData.getData().getEipAddresses();
                                            addCloudInfo(eipAddress, cloud);
                                            return eipAddress;
                                        }).exceptionally(e -> dealException(e, cloud))
                        ).collect(toList());
                        List<EipAddressInfo> eipAddress = aggregateList(this.joinRes(futures));
                        return okFormat(OK.value(), new ResEipAddresses(eipAddress), response);
                    }).exceptionally(e -> badFormat(e, response));
        }
    }

}
