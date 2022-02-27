package com.imooc.ad.mysql.listen;

import com.imooc.ad.mysql.dto.BinLogRowData;

public interface Ilistener {

    void register();

    void onEvent(BinLogRowData eventData);

}
