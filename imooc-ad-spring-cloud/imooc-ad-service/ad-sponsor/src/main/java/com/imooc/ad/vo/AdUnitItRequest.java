package com.imooc.ad.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitItRequest {

    private List<UnitIt> unitItList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitIt {

        private Long unitId;

        private String itType;

    }
}
