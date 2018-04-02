package com.cmp.core.cloud.controller;

import com.cmp.core.cloud.entity.CloudEntity;
import com.cmp.core.cloud.service.CloudService;
import com.cmp.core.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cmp.core.common.Constance.TIME_OUT_SECONDS;

@Controller
@RequestMapping("/cmp")
public class TestController extends BaseController {

    @Autowired
    private CloudService cloudService;

    @RequestMapping("/clouds")
    @ResponseBody
    public List<CloudEntity> describeClouds() {
        try {
            return cloudService.describeClouds().toCompletableFuture().get(TIME_OUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    @RequestMapping("/clouds/{cloudName}")
//    @ResponseBody
//    public CloudEntity describeCloudByName(@PathVariable("cloudName") String cloudName) {
//        return cloudService.describeCloudByName(cloudName)
//                .orElseThrow(() -> new CoreException(ERR_CLOUD_NOT_FOUND));
//    }

}
