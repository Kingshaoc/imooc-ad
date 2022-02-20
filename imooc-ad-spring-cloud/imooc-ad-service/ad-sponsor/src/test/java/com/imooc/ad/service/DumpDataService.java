package com.imooc.ad.service;


import com.alibaba.fastjson.JSON;
import com.imooc.ad.Application;
import com.imooc.ad.constant.CommonStatus;
import com.imooc.ad.dao.AdPlanRepository;
import com.imooc.ad.dao.AdUnitRepository;
import com.imooc.ad.dao.CreativeRepository;
import com.imooc.ad.dao.CreativeUnitRepository;
import com.imooc.ad.dao.unit_condition.AdUnitDistrictRepository;
import com.imooc.ad.dao.unit_condition.AdUnitItRepository;
import com.imooc.ad.dao.unit_condition.AdUnitKeyWordRepository;
import com.imooc.ad.dump.DConstant;
import com.imooc.ad.dump.table.*;
import com.imooc.ad.entity.AdPlan;
import com.imooc.ad.entity.AdUnit;
import com.imooc.ad.entity.Creative;
import com.imooc.ad.entity.CreativeUnit;
import com.imooc.ad.entity.unit_condition.AdUnitDistrict;
import com.imooc.ad.entity.unit_condition.AdUnitIt;
import com.imooc.ad.entity.unit_condition.AdUnitKeyWord;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DumpDataService {

    @Autowired
    private AdPlanRepository adPlanRepository;

    @Autowired
    private AdUnitRepository adUnitRepository;

    @Autowired
    private CreativeRepository creativeRepository;

    @Autowired
    private CreativeUnitRepository creativeUnitRepository;

    @Autowired
    private AdUnitItRepository adUnitItRepository;

    @Autowired
    private AdUnitKeyWordRepository adUnitKeyWordRepository;

    @Autowired
    private AdUnitDistrictRepository adUnitDistrictRepository;


    @Test
    public void dumpAdTableData() {
       dumpAdPlanTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_PLAN));
       dumpAdUnitTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT));
       dumpCreativeTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE));
       dumpAdCreativeUnitTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE_UNIT));
       dumpAdUnitDristrictTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_DISTIRCT));
       dumpAdUnitItTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_IT));
       dumpAdUnitKeywordTable(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_KEYWORD));
    }


    /**
     * 将数据库里的数据导入到文件
     * @param fileName
     */
    private void dumpAdPlanTable(String fileName) {
        List<AdPlan> adPlans = adPlanRepository.findAllByPlanStatus(CommonStatus.VALID.getStatus());
        if (CollectionUtils.isEmpty(adPlans)) {
            return;
        }
        List<AdPlanTable> adPlanTableList = new ArrayList<>();
        adPlans.forEach(p -> adPlanTableList.add(new AdPlanTable(p.getId(), p.getUserId(), p.getPlanStatus(), p.getStartDate(), p.getEndDate())));

        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdPlanTable adPlanTable : adPlanTableList) {
                writer.write(JSON.toJSONString(adPlanTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e) {
            log.error("dudumpAdPlanTable eror ");
        }
    }

    private void dumpAdUnitTable(String fileName) {
        List<AdUnit> adUnitList = adUnitRepository.findAllByUnitStatus(CommonStatus.VALID.getStatus());
        if (CollectionUtils.isEmpty(adUnitList)) {
            return;
        }
        List<AdUnitTable> adUnitTableList = new ArrayList<>();
        adUnitList.forEach(p -> adUnitTableList.add(new AdUnitTable(p.getId(), p.getUnitStatus(), p.getPositionType(), p.getPlanId())));

        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitTable adUnitTable : adUnitTableList) {
                writer.write(JSON.toJSONString(adUnitTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e) {
            log.error("dumpAdUnitTable eror ");
        }
    }


    private void dumpCreativeTable(String fileName) {
        List<Creative> creativeList = creativeRepository.findAll();
        if (CollectionUtils.isEmpty(creativeList)) {
            return;
        }
        List<AdCreativeTable> adCreativeTableList = new ArrayList<>();
        creativeList.forEach(c -> adCreativeTableList.add(new AdCreativeTable(c.getId(), c.getName(), c.getType(), c.getMaterialType(), c.getHeight(), c.getWidth(), c.getAuditStatus(), c.getUrl())));

        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdCreativeTable creativeTable : adCreativeTableList) {
                writer.write(JSON.toJSONString(creativeTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e) {
            log.error("dumpCreativeTable eror ");
        }
    }




    private void dumpAdCreativeUnitTable(String fileName) {
        List<CreativeUnit> creativeUnitList = creativeUnitRepository.findAll();
        if (CollectionUtils.isEmpty(creativeUnitList)) {
            return;
        }
        List<AdCreativeUnitTable> adCreativeUnitTableList = new ArrayList<>();
        creativeUnitList.forEach(c-> adCreativeUnitTableList.add(new AdCreativeUnitTable(c.getId(), c.getUnitId())));

        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdCreativeUnitTable adCreativeUnitTable : adCreativeUnitTableList) {
                writer.write(JSON.toJSONString(adCreativeUnitTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e) {
            log.error("dumpAdCreativeUnitTable eror ");
        }
    }

    private void dumpAdUnitDristrictTable(String fileName) {
        List<AdUnitDistrict> adUnitDistrictList = adUnitDistrictRepository.findAll();
        if (CollectionUtils.isEmpty(adUnitDistrictList)) {
            return;
        }
        List<AdUnitDistrictTable> adUnitDistrictTableList = new ArrayList<>();
        adUnitDistrictList.forEach(c-> adUnitDistrictTableList.add(new AdUnitDistrictTable(c.getUnitId(), c.getProvince(), c.getProvince())));

        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitDistrictTable adUnitDistrictTable : adUnitDistrictTableList) {
                writer.write(JSON.toJSONString(adUnitDistrictTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e) {
            log.error("dumpAdCreativeUnitTable eror ");
        }
    }

    private void dumpAdUnitItTable(String fileName) {
        List<AdUnitIt> adUnitItList = adUnitItRepository.findAll();
        if (CollectionUtils.isEmpty(adUnitItList)) {
            return;
        }
        List<AdUnitItTable> adUnitItTableList = new ArrayList<>();
        adUnitItList.forEach(c-> adUnitItTableList.add(new AdUnitItTable(c.getUnitId(), c.getItTag())));

        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitItTable adUnitItTable : adUnitItTableList) {
                writer.write(JSON.toJSONString(adUnitItTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e) {
            log.error("dumpAdCreativeUnitTable eror ");
        }
    }

    private void dumpAdUnitKeywordTable(String fileName) {
        List<AdUnitKeyWord> adUnitKeyWordList = adUnitKeyWordRepository.findAll();
        if (CollectionUtils.isEmpty(adUnitKeyWordList)) {
            return;
        }
        List<AdUnitKeyWordTable> adUnitKeyWordTableList = new ArrayList<>();
        adUnitKeyWordList.forEach(c-> adUnitKeyWordTableList.add(new AdUnitKeyWordTable(c.getUnitId(), c.getKeyword())));

        Path path = Paths.get(fileName);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (AdUnitKeyWordTable adUnitKeyWordTable : adUnitKeyWordTableList) {
                writer.write(JSON.toJSONString(adUnitKeyWordTable));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e) {
            log.error("dumpAdCreativeUnitTable eror ");
        }
    }
}
