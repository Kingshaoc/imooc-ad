package com.imooc.ad.sender.index;

import com.alibaba.fastjson.JSON;
import com.imooc.ad.dump.table.*;
import com.imooc.ad.handler.AdLevelDataHandler;
import com.imooc.ad.index.DataLevel;
import com.imooc.ad.mysql.constant.Constant;
import com.imooc.ad.mysql.dto.MySqlRowData;
import com.imooc.ad.sender.ISender;
import com.imooc.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("indexSender")
public class IndexSender implements ISender {

    @Override
    public void sender(MySqlRowData rowData) {
        String level = rowData.getLevel();
        if (DataLevel.LEVEL_2.getLevel().equals(level)) {
            Level2RowData(rowData);
        }else if (DataLevel.LEVEL_3.getLevel().equals(level)) {
            Level3RowData(rowData);
        }else if (DataLevel.LEVEL_4.getLevel().equals(level)) {
            Level4RowData(rowData);
        }else {
            log.error("MysqlRowData error : {}", JSON.toJSONString(rowData));
        }
    }

    //binlog -> 索引
    private void Level2RowData(MySqlRowData rowData) {
        if (rowData.getTableName().equals(Constant.AD_PLAN_TABLE_INFO.TABLE_NAME)) {
            List<AdPlanTable> planTables = new ArrayList<>();
            for (Map<String, String> fieldValueMap: rowData.getFieldValueMap()) {
                AdPlanTable adPlanTable = new AdPlanTable();
                // k 列名 v 值
                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_ID :
                            adPlanTable.setId(Long.valueOf(v));
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_USER_ID:
                            adPlanTable.setUserId(Long.valueOf(v));
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_PLAN_STATUS:
                            adPlanTable.setPlanStatus(Integer.valueOf(v));
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_START_DATE:
                            adPlanTable.setStartDate(CommonUtils.parseStringDate(v));
                            break;
                        case Constant.AD_PLAN_TABLE_INFO.COLUMN_END_DATE:
                            adPlanTable.setEndDate(CommonUtils.parseStringDate(v));
                            break;
                    }
                });
                planTables.add(adPlanTable);
            }
            planTables.forEach(p -> AdLevelDataHandler.handleLevel2(p, rowData.getOpType()));
        }else if (rowData.getTableName().equals(Constant.AD_CREATIVE_TABLE_INFO.TABLE_NAME)) {
            List<AdCreativeTable> adCreativeTableList = new ArrayList<>();
            for (Map<String, String> fieldValueMap : rowData.getFieldValueMap()) {
                AdCreativeTable adCreativeTable = new AdCreativeTable();
                fieldValueMap.forEach((k, v) ->  {
                    switch(k) {
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_ID :
                            adCreativeTable.setAdId(Long.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_TYPE:
                            adCreativeTable.setType(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_MATERIAL_TYPE:
                            adCreativeTable.setMaterialType(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_HEIGHT:
                            adCreativeTable.setHeight(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_WIDTH:
                            adCreativeTable.setWidth(Integer.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_AUDIT_STATUS:
                            adCreativeTable.setAuditStatus(Integer.valueOf(v));
                        case Constant.AD_CREATIVE_TABLE_INFO.COLUMN_URL:
                            adCreativeTable.setAdUrl(v);
                            break;
                    }
                });
                adCreativeTableList.add(adCreativeTable);
            }
            adCreativeTableList.forEach(creativeTable -> {AdLevelDataHandler.handleLevel2(creativeTable, rowData.getOpType());  });
        }
    }

    private void Level3RowData(MySqlRowData rowData) {

        if (rowData.getTableName().equals(
                Constant.AD_UNIT_TABLE_INFO.TABLE_NAME)) {

            List<AdUnitTable> unitTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getFieldValueMap()) {

                AdUnitTable unitTable = new AdUnitTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_ID:
                            unitTable.setUnitId(Long.valueOf(v));
                            break;
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_UNIT_STATUS:
                            unitTable.setUnitStatus(Integer.valueOf(v));
                            break;
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_POSITION_TYPE:
                            unitTable.setPositionType(Integer.valueOf(v));
                            break;
                        case Constant.AD_UNIT_TABLE_INFO.COLUMN_PLAN_ID:
                            unitTable.setPlanId(Long.valueOf(v));
                            break;
                    }
                });

                unitTables.add(unitTable);
            }

            unitTables.forEach(u ->
                    AdLevelDataHandler.handleLevel3(u, rowData.getOpType()));
        } else if (rowData.getTableName().equals(
                Constant.AD_CREATIVE_UNIT_TABLE_INFO.TABLE_NAME
        )) {
            List<AdCreativeUnitTable> creativeUnitTables = new ArrayList<>();

            for (Map<String, String> fieldValueMap :
                    rowData.getFieldValueMap()) {

                AdCreativeUnitTable creativeUnitTable = new AdCreativeUnitTable();

                fieldValueMap.forEach((k, v) -> {
                    switch (k) {
                        case Constant.AD_CREATIVE_UNIT_TABLE_INFO.COLUMN_CREATIVE_ID:
                            creativeUnitTable.setAdId(Long.valueOf(v));
                            break;
                        case Constant.AD_CREATIVE_UNIT_TABLE_INFO.COLUMN_UNIT_ID:
                            creativeUnitTable.setUnitId(Long.valueOf(v));
                            break;
                    }
                });

                creativeUnitTables.add(creativeUnitTable);
            }

            creativeUnitTables.forEach(
                    u -> AdLevelDataHandler.handleLevel3(u, rowData.getOpType())
            );
        }
    }

    private void Level4RowData(MySqlRowData rowData) {

        switch (rowData.getTableName()) {

            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.TABLE_NAME:
                List<AdUnitDistrictTable> districtTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getFieldValueMap()) {

                    AdUnitDistrictTable districtTable = new AdUnitDistrictTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_UNIT_ID:
                                districtTable.setUnitId(Long.valueOf(v));
                                break;
                            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_PROVINCE:
                                districtTable.setProvince(v);
                                break;
                            case Constant.AD_UNIT_DISTRICT_TABLE_INFO.COLUMN_CITY:
                                districtTable.setCity(v);
                                break;
                        }
                    });

                    districtTables.add(districtTable);
                }

                districtTables.forEach(
                        d -> AdLevelDataHandler.handleLevel4(d, rowData.getOpType())
                );
                break;
            case Constant.AD_UNIT_IT_TABLE_INFO.TABLE_NAME:
                List<AdUnitItTable> itTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getFieldValueMap()) {

                    AdUnitItTable itTable = new AdUnitItTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Constant.AD_UNIT_IT_TABLE_INFO.COLUMN_UNIT_ID:
                                itTable.setUnitId(Long.valueOf(v));
                                break;
                            case Constant.AD_UNIT_IT_TABLE_INFO.COLUMN_IT_TAG:
                                itTable.setItTag(v);
                                break;
                        }
                    });
                    itTables.add(itTable);
                }
                itTables.forEach(
                        i -> AdLevelDataHandler.handleLevel4(i, rowData.getOpType())
                );
                break;
            case Constant.AD_UNIT_KEYWORD_TABLE_INFO.TABLE_NAME:

                List<AdUnitKeyWordTable> keywordTables = new ArrayList<>();

                for (Map<String, String> fieldValueMap :
                        rowData.getFieldValueMap()) {
                    AdUnitKeyWordTable keywordTable = new AdUnitKeyWordTable();

                    fieldValueMap.forEach((k, v) -> {
                        switch (k) {
                            case Constant.AD_UNIT_KEYWORD_TABLE_INFO.COLUMN_UNIT_ID:
                                keywordTable.setUnitId(Long.valueOf(v));
                                break;
                            case Constant.AD_UNIT_KEYWORD_TABLE_INFO.COLUMN_KEYWORD:
                                keywordTable.setKeyword(v);
                                break;
                        }
                    });
                    keywordTables.add(keywordTable);
                }

                keywordTables.forEach(
                        k -> AdLevelDataHandler.handleLevel4(k, rowData.getOpType())
                );
                break;
        }
    }
}
