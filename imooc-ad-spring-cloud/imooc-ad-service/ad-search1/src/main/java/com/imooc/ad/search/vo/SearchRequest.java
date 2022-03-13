package com.imooc.ad.search.vo;


import com.imooc.ad.search.vo.feature.DistrictFeature;
import com.imooc.ad.search.vo.feature.ItFeature;
import com.imooc.ad.search.vo.feature.KeywordFeature;
import com.imooc.ad.search.vo.media.AdSlot;
import com.imooc.ad.search.vo.media.App;
import com.imooc.ad.search.vo.media.Device;
import com.imooc.ad.search.vo.media.Geo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    // 媒体方的请求标识
    private String mediaId;

    //请求基本信息
    private RequestInfo requestInfo;

    //请求匹配信息
    private FeatureInfo featureInfo;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {

        private String requestId;

        private List<AdSlot> adSlots;

        private App app;

        private Geo geo;

        private Device device;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureInfo {
        private KeywordFeature keyWordFeature;
        private DistrictFeature districtFeature;
        private ItFeature itFeature;
        private FeatureRelation featureRelation = FeatureRelation.AND;
    }

}
