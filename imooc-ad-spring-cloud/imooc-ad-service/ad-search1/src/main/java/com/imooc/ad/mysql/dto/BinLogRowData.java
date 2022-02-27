package com.imooc.ad.mysql.dto;


import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinLogRowData {

    private TableTemplate table;

    private EventType eventType;

    private List<Map<String, String>> after;

    private List<Map<String, String>> before;


}
