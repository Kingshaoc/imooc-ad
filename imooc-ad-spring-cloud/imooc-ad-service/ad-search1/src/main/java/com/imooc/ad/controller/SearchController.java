package com.imooc.ad.controller;


import com.alibaba.fastjson.JSON;
import com.imooc.ad.search.ISearch;
import com.imooc.ad.search.vo.SearchRequest;
import com.imooc.ad.search.vo.SearchResponse;
import com.imooc.ad.annotation.IngoreResponseAdvice;
import com.imooc.ad.client.SponsorClient;
import com.imooc.ad.client.vo.AdPlan;
import com.imooc.ad.client.vo.AdPlanGetRequest;
import com.imooc.ad.vo.CommonReponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
public class SearchController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SponsorClient sponsorClient;

    @Autowired
    private ISearch search;

    @SuppressWarnings("all")
    @IngoreResponseAdvice //不使用统一响应
    @PostMapping("/getAdPlansByRibbon")
    public CommonReponse<List<AdPlan>> getAdPlansByRebbon(@RequestBody AdPlanGetRequest request){
        log.info("getAdPlansByRebbon params: {}" , JSON.toJSONString(request));
        return restTemplate.postForEntity("http://eureka-ad-sponsor/ad-sponsor/get/adPlan", request, CommonReponse.class).getBody();
    }

    /**
     * 使用Feigh
     * @param request
     * @return
     */
    @IngoreResponseAdvice
    @PostMapping("/getAdPlansByFeign")
    public CommonReponse<List<AdPlan>> getAdPlansByFeign(@RequestBody AdPlanGetRequest request){
        log.info("getAdPlansByFeign params: {}" , JSON.toJSONString(request));
        return sponsorClient.getAdPlans(request);
    }


    @PostMapping("/fetchAds")
    public SearchResponse fetchAds(SearchRequest request) {
        log.info("fetchAds request:{}", JSON.toJSONString(request));
        return search.fetchAds(request);
    }




}
