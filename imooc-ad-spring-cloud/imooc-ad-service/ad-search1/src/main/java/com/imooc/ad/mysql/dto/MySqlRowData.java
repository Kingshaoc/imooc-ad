package com.imooc.ad.mysql.dto;


import com.imooc.ad.mysql.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySqlRowData {

    private String tableName;

    //检索业务的层级关系
    private String level;

    private OpType opType;

    private List<Map<String, String>> fieldValueMap = new ArrayList<>();
}
