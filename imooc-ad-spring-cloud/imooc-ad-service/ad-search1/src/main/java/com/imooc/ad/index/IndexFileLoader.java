package com.imooc.ad.index;

import com.alibaba.fastjson.JSON;
import com.imooc.ad.dump.DConstant;
import com.imooc.ad.dump.table.*;
import com.imooc.ad.handler.AdLevelDataHandler;
import com.imooc.ad.mysql.constant.OpType;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 根据数据库导出的文件、读取文件、加载索引
 */
@Component
@DependsOn("dataTable")
public class IndexFileLoader {

    @PostConstruct
    public void init() {
        List<String> adPlanStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_PLAN));
        adPlanStrings.forEach(p -> AdLevelDataHandler.handleLevel2(JSON.parseObject(p, AdPlanTable.class), OpType.ADD));

        List<String> adCreativeStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE));
        adCreativeStrings.forEach(p -> AdLevelDataHandler.handleLevel2(JSON.parseObject(p, AdCreativeTable.class), OpType.ADD));

        List<String> adUnits = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT));
        adUnits.forEach(p -> AdLevelDataHandler.handleLevel3(JSON.parseObject(p, AdUnitTable.class), OpType.ADD));

        List<String> adCreativeUnits = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE_UNIT));
        adCreativeUnits.forEach(p -> AdLevelDataHandler.handleLevel3(JSON.parseObject(p, AdCreativeUnitTable.class), OpType.ADD));

        List<String> adUnitDistricts = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_DISTIRCT));
        adUnitDistricts.forEach(p -> AdLevelDataHandler.handleLevel4(JSON.parseObject(p, AdUnitDistrictTable.class), OpType.ADD));

        List<String> adUnitIts = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_IT));
        adUnitIts.forEach(p -> AdLevelDataHandler.handleLevel4(JSON.parseObject(p, AdUnitItTable.class), OpType.ADD));

        List<String> adUnitkeyWords = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_KEYWORD));
        adUnitkeyWords.forEach(p -> AdLevelDataHandler.handleLevel4(JSON.parseObject(p, AdUnitKeyWordTable.class), OpType.ADD));
    }

    private List<String> loadDumpData(String fileName) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            return br.lines().collect(Collectors.toList());
        }catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
