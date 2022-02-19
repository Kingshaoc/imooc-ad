package com.imooc.ad.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.ad.exception.AdException;
import com.imooc.ad.service.IAdUnitService;
import com.imooc.ad.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController

public class AdUnitOPController {

    @Autowired
    private IAdUnitService adUnitService;

    @PostMapping("/create/adUnit")
    public AdUnitResponse createAdUnit(@RequestBody AdUnitRequest request)throws AdException {
        log.info("ad-sponsor : createAdUnit -> {}", JSON.toJSONString(request));
        return adUnitService.createUnit(request);
    }

    @PostMapping("/create/unitKeyWord")
    public AdUnitKeyWordResponse createUnitKeyWord(@RequestBody AdUnitKeyWordRequest request)throws AdException {
        log.info("ad-sponsor : createUnitKeyWord -> {}", JSON.toJSONString(request));
        return adUnitService.createUnitKeyWord(request);
    }


    @PostMapping("/create/unitIt")
    public AdUnitItResponse createUnitIt(@RequestBody AdUnitItRequest request)throws AdException {
        log.info("ad-sponsor : createUnitIt -> {}", JSON.toJSONString(request));
        return adUnitService.createUnitIt(request);
    }

    @PostMapping("/create/unitDistrict")
    public AdUnitDistrictResponse createUnitDistrict(@RequestBody AdUnitDistrictRequest request)throws AdException {
        log.info("ad-sponsor : unitDistrict -> {}", JSON.toJSONString(request));
        return adUnitService.createUnitDistrict(request);
    }

    @PostMapping("/create/creativeUnit")
    public CreativeUnitResponse createCreativeUnit(
            @RequestBody CreativeUnitRequest request
    ) throws AdException {
        log.info("ad-sponsor: createCreativeUnit -> {}",
                JSON.toJSONString(request));
        return adUnitService.createCreativeUnit(request);
    }

}
