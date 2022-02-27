package com.imooc.ad.index.adunit;

import com.imooc.ad.index.IndexAware;
import com.imooc.ad.index.adplan.AdPlanObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推广单元索引 ： 正向索引
 */
@Slf4j
@Component
public class AdUnitIndex implements IndexAware<Long, AdUnitObject> {

    private static Map<Long, AdUnitObject> objectMap;

    static {
        objectMap = new ConcurrentHashMap<>();
    }

    public Set<Long> match(Integer positionType) {
        Set<Long> adUnitIds = new HashSet<>();
        objectMap.forEach((k, v) -> {
            if (AdUnitObject.isAdSlotTypeOK(positionType, v.getPositonType())) {
                adUnitIds.add(k);
            }
        });
        return adUnitIds;
    }

    public List<AdUnitObject> fetch(Collection<Long> adUnitIds) {
        if (CollectionUtils.isEmpty(adUnitIds)) {
            return Collections.emptyList();
        }
        List<AdUnitObject> result = new ArrayList<>();
        adUnitIds.forEach(u -> {
            AdUnitObject adUnitObject = get(u);
            if (adUnitObject == null) {
                log.error("adUnitObject not found:{}", u);
            }else {
                result.add(adUnitObject);
            }
        });
        return result;
    }

    @Override
    public AdUnitObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, AdUnitObject value) {
        log.info("before add : {}" , objectMap);
        objectMap.put(key, value);
        log.info("after add : {}" , objectMap);
    }

    @Override
    public void update(Long key, AdUnitObject value) {
        log.info("before update : {}" , objectMap);
        AdUnitObject adUnitObject = objectMap.get(key);
        if (adUnitObject == null) {
            objectMap.put(key, value);
        }else {
            adUnitObject.update(value);
        }
        log.info("after update : {}" , objectMap);
    }

    @Override
    public void delete(Long key, AdUnitObject value) {
        log.info("before remove : {}" , objectMap);
        objectMap.remove(key);
        log.info("after remove : {}" , objectMap);
    }
}
