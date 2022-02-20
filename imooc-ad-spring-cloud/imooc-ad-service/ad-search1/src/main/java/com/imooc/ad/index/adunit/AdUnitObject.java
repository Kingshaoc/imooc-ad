package com.imooc.ad.index.adunit;

import com.imooc.ad.index.adplan.AdPlanObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitObject {

    private Long unitId;

    private Integer unitStatus;

    private Integer positonType;

    /**
     * 关联的推广计划Id
     */
    private Long planId;

    /**
     * 关联的推广计划
     */
    private AdPlanObject adPlanObject;


    void update(AdUnitObject adUnitObject) {
        if (adUnitObject != null && adUnitObject.getUnitId() != null) {
            this.unitId = adUnitObject.getUnitId();
        }
        if (adUnitObject.getUnitStatus() != null) {
            this.unitStatus = adUnitObject.getUnitStatus();
        }
        if (adUnitObject.getPositonType() != null) {
            this.positonType = adUnitObject.getPositonType();
        }
        if (adUnitObject.getPlanId() != null) {
            this.planId = adUnitObject.getPlanId();
        }
        if (adUnitObject.getAdPlanObject() != null) {
            this.adPlanObject = adUnitObject.getAdPlanObject();
        }
    }

}
