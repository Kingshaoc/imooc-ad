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

    private static boolean isKaiPing(int positonType) {
        return (positonType & AdUnitConstants.POSITION_TYPE.KAIPING) > 0;
    }

    private static boolean isTiePian(int positonType) {
        return (positonType & AdUnitConstants.POSITION_TYPE.TIEPING) > 0;
    }

    private static boolean isTiePianMiddle(int positonType) {
        return (positonType & AdUnitConstants.POSITION_TYPE.TIEPING_MIDDLE) > 0;
    }

    private static boolean isTiePianPause(int positonType) {
        return (positonType & AdUnitConstants.POSITION_TYPE.TIEPING_PAUSE) > 0;
    }

    private static boolean isTiePianPost(int positonType) {
        return (positonType & AdUnitConstants.POSITION_TYPE.TIEPING_POST) > 0;
    }

    public static  boolean isAdSlotTypeOK(int adSlotType, int positonType) {
        switch (adSlotType) {
            case AdUnitConstants.POSITION_TYPE.KAIPING:
                return isKaiPing(positonType);
            case AdUnitConstants.POSITION_TYPE.TIEPING:
                return  isTiePian(positonType);
            case AdUnitConstants.POSITION_TYPE.TIEPING_MIDDLE:
                return isTiePianMiddle(positonType);
            case AdUnitConstants.POSITION_TYPE.TIEPING_PAUSE:
                return isTiePianPause(positonType);
            case AdUnitConstants.POSITION_TYPE.TIEPING_POST:
                return isTiePianPost(positonType);
            default:
                return false;
        }
    }

}
