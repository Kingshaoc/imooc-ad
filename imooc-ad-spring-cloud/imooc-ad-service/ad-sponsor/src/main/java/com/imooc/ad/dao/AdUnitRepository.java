package com.imooc.ad.dao;

import com.imooc.ad.entity.AdUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdUnitRepository extends JpaRepository<AdUnit, Long> {

    AdUnit findByPlanIdAndUnitName(Long planId, String unitName);

    AdUnit findAllByUnitStatus(int unitStatus);
}
