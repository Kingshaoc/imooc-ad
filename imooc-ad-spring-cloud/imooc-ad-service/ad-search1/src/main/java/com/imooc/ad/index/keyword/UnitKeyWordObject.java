package com.imooc.ad.index.keyword;

import com.imooc.ad.index.IndexAware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


/**
 * 推广单元限制
 *  关键词限制
 *     倒排索引
 *      keyword -> set<Long> unitId
 *      通过省市信息找到所有的推广单元Id
 *  正向索引
 *      unitId -> province-city
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitKeyWordObject  {

    private Long unitId;

    private String keyword;

}
