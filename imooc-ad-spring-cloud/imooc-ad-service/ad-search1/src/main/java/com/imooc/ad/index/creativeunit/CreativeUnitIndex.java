package com.imooc.ad.index.creativeunit;


import com.imooc.ad.index.IndexAware;
import com.imooc.ad.index.adunit.AdUnitObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 创意与推广单元关联索引
 *  key : adId-unitId
 */
@Slf4j
@Component
public class CreativeUnitIndex implements IndexAware<String, CreativeUnitObject> {

    private static Map<String, CreativeUnitObject> objectMap;

    // <adId, unitId Set>
    private static Map<Long, Set<Long>> creativeUnitMap;

    // <unitId, adId set>
    private static Map<Long, Set<Long>> unitCreativeMap;

    static  {
        objectMap = new HashMap<>();
        creativeUnitMap = new HashMap<>();
        unitCreativeMap = new HashMap<>();
    }

    @Override
    public CreativeUnitObject get(String key) {
       return objectMap.get(key);
    }

    @Override
    public void add(String key, CreativeUnitObject value) {
        log.info("before add:{}", objectMap);
        objectMap.put(key, value);
        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (unitSet == null) {
            unitSet = new ConcurrentSkipListSet<>();
            creativeUnitMap.put(value.getAdId(), unitSet);
        }
        unitSet.add(value.getUnitId());

        Set<Long> creativeSet = unitCreativeMap.get(value.getAdId());
        if (creativeSet == null) {
            creativeSet = new ConcurrentSkipListSet<>();
            creativeUnitMap.put(value.getAdId(), creativeSet);
        }
        creativeSet.add(value.getAdId());
        log.info("after add:{}", objectMap);
    }

    @Override
    public void update(String key, CreativeUnitObject value) {
        log.info("CreativeUnitIndex not support update");
    }

    @Override
    public void delete(String key, CreativeUnitObject value) {
        log.info("before remove:{}", objectMap);
        objectMap.remove(key);
        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (CollectionUtils.isNotEmpty(unitSet)) {
            unitSet.remove(value.getUnitId());
        }
        Set<Long> creativeSet = unitCreativeMap.get(value.getAdId());
        if (CollectionUtils.isNotEmpty(creativeSet)) {
            creativeSet.remove(value.getAdId());
        }
        log.info("after remove:{}", objectMap);
    }


    public List<Long> selectAds(List<AdUnitObject> unitObjects) {
        if (CollectionUtils.isEmpty(unitObjects)) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        for (AdUnitObject unitObject : unitObjects) {
            Set<Long> adIds = unitCreativeMap.get(unitObject.getUnitId());
            if (CollectionUtils.isNotEmpty(adIds)) {
                result.addAll(adIds);
            }
        }
        return result;
    }


}
