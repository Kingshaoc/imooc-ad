package com.imooc.ad.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUnitDistrictRequest {

    private List<UnitDistrict> unitDistrictList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnitDistrict {
        private Long unitId;
        private String province;
        private String city;
    }
}
