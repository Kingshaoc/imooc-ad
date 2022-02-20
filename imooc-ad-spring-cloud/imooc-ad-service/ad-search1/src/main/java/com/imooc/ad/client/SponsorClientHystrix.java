package com.imooc.ad.client;

import com.imooc.ad.client.vo.AdPlan;
import com.imooc.ad.client.vo.AdPlanGetRequest;
import com.imooc.ad.vo.CommonReponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SponsorClientHystrix implements SponsorClient {

    /**
     * 当使用feign调用ad-sponsor接口出错时，hystirx处理方法
     * @param adPlanGetRequest
     * @return
     */
    @Override
    public CommonReponse<List<AdPlan>> getAdPlans(AdPlanGetRequest adPlanGetRequest) {
        return new CommonReponse<>(-1 , "eureka-client-ad-sponsor error");
    }
}
