package com.cmp.core.image.controller;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.cmp.core.common.BaseController;
import com.cmp.core.common.CoreException;
import com.cmp.core.common.JsonUtil;
import com.cmp.core.image.model.req.ReqCreImage;
import com.cmp.core.image.model.res.ResImage;
import com.cmp.core.image.model.res.ResImageInfo;
import com.cmp.core.image.model.res.ResImages;
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
import static com.cmp.core.common.ErrorEnum.ERR_CREATE_IMAGE_BODY;
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
                        List<ResImageInfo> images = aggregateList(this.joinRes(futures));
                        return okFormat(OK.value(), new ResImages(images), response);
                    }).exceptionally(e -> badFormat(e, response));
        }
    }

    /**
     * 查询指定镜像
     *
     * @param request  http请求 http请求
     * @param response http响应 http响应
     * @param regionId 区域id
     * @param imageId  镜像id
     * @return 指定镜像信息
     */
    @RequestMapping("/{regionId}/images/{imageId}")
    @ResponseBody
    public CompletionStage<JsonNode> describeImageAttribute(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable String regionId,
            @PathVariable String imageId) {
        return getCloudEntity(request).thenCompose(cloud ->
                httpGet("/" + regionId + "/images/" + imageId, ResImage.class, cloud)
                        .thenApply(resData -> {
                            ResImageInfo image = resData.getData().getImage();
                            addCloudInfo(image, cloud);
                            return okFormat(resData.getCode(), new ResImage(image), response);
                        })
        ).exceptionally(e -> badFormat(e, response));
    }

    /**
     * 创建镜像
     *
     * @param request  http请求
     * @param response http响应
     * @return 操作结果
     * @throws IOException 异常
     */
    @PostMapping("/images")
    @ResponseBody
    public CompletionStage<JsonNode> createImage(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String body = IOUtils.read(reader);
        ReqCreImage reqCreImage = JsonUtil.stringToObject(body, ReqCreImage.class);
        return getCloudEntity(request).thenCompose(cloud ->
                checkCreateImageBody(reqCreImage).thenCompose(flag -> {
                    if (!flag) {
                        throw new CoreException(ERR_CREATE_IMAGE_BODY);
                    } else {
                        return httpPost("/images", JsonUtil.objectToString(reqCreImage), cloud)
                                .thenApply(resData -> okFormat(resData.getCode(), null, response));
                    }
                })
        ).exceptionally(e -> badFormat(e, response));
    }

    /**
     * 删除镜像
     *
     * @param request  http请求 http请求
     * @param response http响应 http响应
     * @param regionId 区域id
     * @param imageId  镜像id
     * @return 操作结果
     */
    @DeleteMapping("/{regionId}/images/{imageId}")
    @ResponseBody
    public CompletionStage<JsonNode> deleteImage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable String regionId,
            @PathVariable String imageId) {
        return getCloudEntity(request)
                .thenCompose(cloud -> httpDel("/" + regionId + "/images/" + imageId, cloud))
                .thenApply(x -> okFormat(x.getCode(), null, response))
                .exceptionally(e -> badFormat(e, response));
    }

    private CompletionStage<Boolean> checkCreateImageBody(ReqCreImage body) {
        return CompletableFuture.supplyAsync(() ->
                (null != body.getInstanceId()
                        && null != body.getRegionId()
                        && null != body.getImageName())
        );
    }

}
