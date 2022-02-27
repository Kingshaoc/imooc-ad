package com.imooc.ad.Search;

import com.imooc.ad.Search.vo.SearchRequest;
import com.imooc.ad.Search.vo.SearchResponse;

public interface ISearch {

    SearchResponse fetchAds(SearchRequest request);
}
