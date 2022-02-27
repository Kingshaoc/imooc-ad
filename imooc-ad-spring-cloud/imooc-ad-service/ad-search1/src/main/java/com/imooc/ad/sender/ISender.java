package com.imooc.ad.sender;

import com.imooc.ad.mysql.dto.MySqlRowData;

public interface ISender {

    void sender(MySqlRowData rowData);

}
