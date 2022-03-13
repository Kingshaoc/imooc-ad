package com.imooc.ad.search;

import com.imooc.ad.search.vo.SearchRequest;
import com.imooc.ad.search.vo.SearchResponse;

public interface ISearch {

    SearchResponse fetchAds(SearchRequest request);
}
