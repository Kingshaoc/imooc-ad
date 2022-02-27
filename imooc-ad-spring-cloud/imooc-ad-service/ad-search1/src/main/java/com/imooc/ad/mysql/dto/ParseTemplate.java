package com.imooc.ad.mysql.dto;


import com.imooc.ad.mysql.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParseTemplate {

    private String database;

    private Map<String, TableTemplate> tableTemplateMap = new HashMap<>();

    public static ParseTemplate parse(Template template) {
        ParseTemplate parseTemplate = new ParseTemplate();
        parseTemplate.setDatabase(template.getDatabases());

        List<JsonTable> tableList = template.getTableList();
        for (JsonTable table : tableList) {
            String name = table.getTableName();
            Integer level = table.getLevel();
            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setTableName(name);
            tableTemplate.setLevel(level.toString());
            parseTemplate.tableTemplateMap.put(name, tableTemplate);

            Map<OpType, List<String>> opTypeFieldSetMap = tableTemplate.getOpTypeFieldSetMap();

            for (JsonTable.Column column : table.getInsert()) {
                getAndCreateIfNeed(OpType.ADD, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }

            for (JsonTable.Column column : table.getUpdate()) {
                getAndCreateIfNeed(OpType.UPDATE, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }

            for (JsonTable.Column column : table.getDelete()) {
                getAndCreateIfNeed(OpType.DELETE, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }
        }
        return parseTemplate;

    }

    private static <T, R> R getAndCreateIfNeed(T key, Map<T, R> map, Supplier<R> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }


}
