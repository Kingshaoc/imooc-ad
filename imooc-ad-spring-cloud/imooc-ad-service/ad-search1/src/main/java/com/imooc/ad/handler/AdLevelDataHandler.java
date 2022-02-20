package com.imooc.ad.handler;

import com.imooc.ad.dump.table.*;
import com.imooc.ad.index.DataTable;
import com.imooc.ad.index.IndexAware;
import com.imooc.ad.index.adplan.AdPlanIndex;
import com.imooc.ad.index.adplan.AdPlanObject;
import com.imooc.ad.index.adunit.AdUnitIndex;
import com.imooc.ad.index.adunit.AdUnitObject;
import com.imooc.ad.index.creative.CreativeIndex;
import com.imooc.ad.index.creative.CreativeObject;
import com.imooc.ad.index.creativeunit.CreativeUnitIndex;
import com.imooc.ad.index.creativeunit.CreativeUnitObject;
import com.imooc.ad.index.district.UnitDistrictIndex;
import com.imooc.ad.mysql.constant.OpType;
import com.imooc.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 1. 索引之间的层级的划分，也就是依赖关系的划分
 * 2. 加载全量索引其实是增量索引【添加】的一种特殊实现
 *
 */
@Slf4j
public class AdLevelDataHandler {

     private static <K,V> void handleBinlogEvent(IndexAware<K, V> index, K key, V v, OpType opType){

         switch (opType) {
             case ADD:
                index.add(key, v);
                break;
             case DELETE:
                index.delete(key, v);
                break;
             case UPDATE:
                 index.update(key, v);
                 break;
             default:
                 break;
         }

    }

    // 第二级索引 adplan
    public static void handleLevel2(AdPlanTable planTable, OpType opType) {
        AdPlanObject planObject = new AdPlanObject(planTable.getId(), planTable.getUserId(), planTable.getPlanStatus(), planTable.getStartDate(), planTable.getEndDate());
        handleBinlogEvent(DataTable.of(AdPlanIndex.class),  planObject.getPlanId(), planObject, opType);
    }

    // 第二级索引 adCreative
    public static void handleLevel2(AdCreativeTable creativeTable, OpType opType) {
        CreativeObject creativeObject = new CreativeObject(creativeTable.getAdId(), creativeTable.getName(),
                creativeTable.getType(), creativeTable.getMaterialType(), creativeTable.getHeight(), creativeTable.getWidth(), creativeTable.getAuditStatus(), creativeTable.getAdUrl());
        handleBinlogEvent(DataTable.of(CreativeIndex.class),  creativeObject.getAdId(), creativeObject, opType);
    }

    //第三级别索引 adunit
    public static void handleLevel3(AdUnitTable adUnitTable, OpType opType) {

        AdPlanObject adPlanObject = DataTable.of(AdPlanIndex.class).get(adUnitTable.getPlanId());
        if (adPlanObject == null) {
            log.error("handleLevel3 found adplanObject error:{}", adUnitTable.getPlanId());
            return;
        }
        AdUnitObject adUnitObject = new AdUnitObject(adUnitTable.getUnitId(), adUnitTable.getUnitStatus(), adUnitTable.getPositionType(), adUnitTable.getPlanId(), adPlanObject);

        handleBinlogEvent(DataTable.of(AdUnitIndex.class), adUnitTable.getUnitId(), adUnitObject, opType);
    }

    //第三级别索引 adCreativeUnit
    public static void handleLevel3(AdCreativeUnitTable adCreativeUnitTable, OpType type) {
         if (type == OpType.UPDATE) {
             return;
         }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adCreativeUnitTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel3 found adUnitObject error:{}", adCreativeUnitTable.getUnitId());
            return;
        }
        CreativeObject creativeObject = DataTable.of(CreativeIndex.class).get(adCreativeUnitTable.getAdId());
        if (creativeObject == null) {
            log.error("handleLevel3 found creativeObject error:{}", adCreativeUnitTable.getAdId());
            return;
        }

        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(adCreativeUnitTable.getAdId(), adCreativeUnitTable.getUnitId());
        handleBinlogEvent(
                DataTable.of(CreativeUnitIndex.class),
                CommonUtils.stringConcat(creativeUnitObject.getAdId().toString(), creativeUnitObject.getUnitId().toString()),
                creativeUnitObject,
                type);

    }

    public static void handleLevel4(AdUnitDistrictTable adUnitDistrictTable, OpType type) {
        if (type == OpType.UPDATE) {
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adUnitDistrictTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel4 found adUnitObject error:{}", adUnitDistrictTable.getUnitId());
            return;
        }
        String key = CommonUtils.stringConcat(adUnitDistrictTable.getProvince(), adUnitDistrictTable.getCity());
        Set<Long> value = new HashSet<>(Collections.singleton(adUnitDistrictTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class), key, value, type);
    }

    public static void handleLevel4(AdUnitItTable adUnitItTable, OpType type) {
        if (type == OpType.UPDATE) {
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adUnitItTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel4 found adUnitObject error:{}", adUnitItTable.getUnitId());
            return;
        }
        Set<Long> value = new HashSet<>(Collections.singleton(adUnitItTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class), adUnitItTable.getItTag(), value, type);
    }

    public static void handleLevel4(AdUnitKeyWordTable adUnitKeyWordTable, OpType type) {
        if (type == OpType.UPDATE) {
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adUnitKeyWordTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel4 found adUnitObject error:{}", adUnitKeyWordTable.getUnitId());
            return;
        }
        Set<Long> value = new HashSet<>(Collections.singleton(adUnitKeyWordTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class), adUnitKeyWordTable.getKeyword(), value, type);
    }


}
