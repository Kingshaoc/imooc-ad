package com.imooc.ad.index.adplan;


import com.imooc.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推广计划索引对象与实现 : 正向索引
 */
@Slf4j
@Component
public class AdPlanIndex implements IndexAware<Long, AdPlanObject> {

    private static Map<Long, AdPlanObject> objectMap;

    static {
        objectMap = new ConcurrentHashMap<>();
    }


    @Override
    public AdPlanObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, AdPlanObject value) {
        log.info("before add : {}" , objectMap);
        objectMap.put(key, value);
        log.info("after add : {}" , objectMap);
    }

    @Override
    public void update(Long key, AdPlanObject value) {
        log.info("before update : {}" , objectMap);
        AdPlanObject adPlanObject = objectMap.get(key);
        if (adPlanObject == null) {
            objectMap.put(key, value);
        }else {
            adPlanObject.update(value);
        }
        log.info("after update : {}" , objectMap);
    }

    @Override
    public void delete(Long key, AdPlanObject vlaue) {
        log.info("before remove : {}" , objectMap);
        objectMap.remove(key);
        log.info("after remove : {}" , objectMap);
    }
}
