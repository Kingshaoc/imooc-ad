package com.imooc.ad.mysql.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 解析template.json
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTable {

    private String tableName;

    private Integer level;

    private List<Column> insert;

    private List<Column> update;

    private List<Column> delete;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column{
        private String column;

    }


}
