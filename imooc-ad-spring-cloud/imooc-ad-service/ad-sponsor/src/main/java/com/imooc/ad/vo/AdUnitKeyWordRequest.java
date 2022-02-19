package com.imooc.ad.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitKeyWordRequest {

    private List<UnitKeyWord> unitKeyWordList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitKeyWord {
        private Long unitId; //限制的推广单元ID
        private String keyWord; //限制的推广单元关键字
    }


}
