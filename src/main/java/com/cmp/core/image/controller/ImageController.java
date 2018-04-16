package com.cmp.core.image.controller;

import com.cmp.core.common.BaseController;
import com.cmp.core.image.model.res.ResImageInfo;
import com.cmp.core.image.model.res.ResImages;
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
public class ImageController extends BaseController {

    /**
     * 查询镜像列表
     *
     * @param request  http请求
     * @param response http响应
     * @return 镜像列表
     */
    @RequestMapping("/images")
    @ResponseBody
    public CompletionStage<JsonNode> describeImages(
            final HttpServletRequest request, final HttpServletResponse response) {
        if (null != request.getHeader(HEADER_CLOUD_ID)) {
            return getCloudEntity(request).thenCompose(cloud ->
                    httpGet("/images", ResImages.class, cloud)
                            .thenApply(resData -> {
                                List<ResImageInfo> images = resData.getData().getImages();
                                addCloudInfo(images, cloud);
                                return okFormat(resData.getCode(), new ResImages(images), response);
                            })
            ).exceptionally(e -> badFormat(e, response));
        } else {
            return getAllCloudEntity(request, true)
                    .thenApply(cloudList -> {
                        List<CompletionStage<List<ResImageInfo>>> futures = cloudList.stream().map(cloud ->
                                httpGet("/images", ResImages.class, cloud)
                                        .thenApply(resData -> {
                                            List<ResImageInfo> images = resData.getData().getImages();
                                            addCloudInfo(images, cloud);
                                            return images;
                                        }).exceptionally(e -> dealException(e, cloud))
                        ).collect(toList());
                        List<ResImageInfo> instances = aggregateList(this.joinRes(futures));
                        return okFormat(OK.value(), new ResImages(instances), response);
                    }).exceptionally(e -> badFormat(e, response));
        }
    }

}
