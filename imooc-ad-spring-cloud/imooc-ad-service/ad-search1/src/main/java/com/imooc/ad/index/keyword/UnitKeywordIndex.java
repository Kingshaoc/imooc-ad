package com.imooc.ad.index.keyword;


import com.imooc.ad.index.IndexAware;
import com.imooc.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;


/**
 * 推广单元限制
 * 关键词限制
 * 倒排索引
 * keyword -> set<Long> unitId
 * 通过省市信息找到所有的推广单元Id
 * 正向索引
 * unitId -> set<String>keyword
 */
@Slf4j
@Component
public class UnitKeywordIndex implements IndexAware<String, Set<Long>> {

    private static Map<String, Set<Long>> keyWordUnitMap; // 逆向 keyWord -> unitId

    private static Map<Long, Set<String>> unitKeyWordMap; //正向  unitId -> keyWord

    static {
        keyWordUnitMap = new ConcurrentHashMap<>();
        unitKeyWordMap = new ConcurrentHashMap<>();
    }


    @Override
    public Set<Long> get(String key) {
        if (StringUtils.isBlank(key)) {
            return Collections.emptySet();
        }
        Set<Long> result = keyWordUnitMap.get(key);
        if (result == null) {
            return Collections.emptySet();
        }
        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("unitkeywordIndex, before add:{}", unitKeyWordMap);

        //当keyWordUnitMap不存在key时，会返回一个ConcurrentSkipListSet->unitIdSet;
        Set<Long> unitIdSet = CommonUtils.getorCreate(key, keyWordUnitMap, ConcurrentSkipListSet::new);
        unitIdSet.addAll(value);


        for (Long unitId : value) {
            Set<String> keyWordSet = CommonUtils.getorCreate(unitId, unitKeyWordMap, ConcurrentSkipListSet::new);
            keyWordSet.add(key);
        }
        log.info("unitkeywordIndex, after add:{}", unitKeyWordMap);

    }

    //先删除 -> 重新add
    @Override
    public void update(String key, Set<Long> value) {
        log.error("keyword index can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("unitkeywordIndex, before remove:{}", unitKeyWordMap);
        //当keyWordUnitMap不存在key时，会返回一个ConcurrentSkipListSet->unitIdSet;
        Set<Long> unitIds = CommonUtils.getorCreate(key, keyWordUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);

        for (Long unitId : value) {
            Set<String> keyWordSet = CommonUtils.getorCreate(unitId, unitKeyWordMap, ConcurrentSkipListSet::new);
            keyWordSet.remove(key);
        }
        log.info("unitkeywordIndex, after remove:{}", unitKeyWordMap);
    }

    public boolean match(Long unitId, List<String> keywords) {
        if (unitKeyWordMap.containsKey(unitId) && CollectionUtils.isNotEmpty(unitKeyWordMap.get(unitId))) {
            Set<String> unitKeywords = unitKeyWordMap.get(unitId);
            return CollectionUtils.isSubCollection(keywords, unitKeywords);
        }
        return false;
    }
}
