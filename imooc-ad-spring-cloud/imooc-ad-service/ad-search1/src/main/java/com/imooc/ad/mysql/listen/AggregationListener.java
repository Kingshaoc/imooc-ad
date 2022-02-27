package com.imooc.ad.mysql.listen;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.imooc.ad.mysql.TemplateHolder;
import com.imooc.ad.mysql.dto.BinLogRowData;
import com.imooc.ad.mysql.dto.TableTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener {

    private String dbName;

    private String tableName;

    private Map<String, Ilistener> listenerMap = new HashMap<>();

    @Autowired
    private TemplateHolder templateHolder;

    @Override
    public void onEvent(Event event) {
        EventType type = event.getHeader().getEventType();
        log.debug("event type : {}", type);
        //table_map 记录当前表名与数据库名
        if (type == EventType.TABLE_MAP) {
            TableMapEventData data = event.getData();
            this.tableName = data.getTable();
            this.dbName = data.getDatabase();
            return;
        }
        if (type != EventType.EXT_UPDATE_ROWS && type != EventType.DELETE_ROWS && type != EventType.EXT_UPDATE_ROWS) {
            return;
        }
        //表名和库名是否已经完成填充
        if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("no meta data event");
        }
        //找出对应表有兴趣的监听器
        String key = genKey(this.dbName, this.tableName);
        Ilistener listener = this.listenerMap.get(key);
        if (listener == null) {
            log.debug("skip {}", key);
        }
        log.debug("skip {}", key);
        log.info("trigger event : {}", type.name());
        try {
            BinLogRowData rowData = buildRowData(event.getData());
            if (rowData == null) {
                return;
            }
            rowData.setEventType(type);
            //处理event事件
            listener.onEvent(rowData);
         }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }finally {
            this.dbName = "";
            this.tableName = "";
        }
    }

    private String genKey(String dbName, String tableName) {
        return dbName + ":" + tableName;
    }

    public void register(String dbName, String tableName, Ilistener ilistener) {
        log.info("register tableName : {}, dbName : {}", tableName, dbName);
        this.listenerMap.put(genKey(dbName, tableName), ilistener);
    }



    public BinLogRowData buildRowData(EventData eventData) {
        TableTemplate table = templateHolder.getTable(tableName);
        if (table == null) {
            log.warn("table {} not found", tableName);
            return null;
        }
        List<Map<String, String>> afterMapList = new ArrayList<>();

        for (Serializable[] after: getAfterValues(eventData)) {
             Map<String, String> afterMap = new HashMap<>();
             int colLen = after.length;
             for (int i = 0; i < colLen; i++) {
                 //取出当前位置对应的列名
                 String colName = table.getPosMap().get(i);
                 //如果没有则不关心该列
                 if (StringUtils.isEmpty(colName)) {
                     log.debug("ignore position: {}", i);
                     continue;
                 }
                 String colValue = after[i].toString();
                 afterMap.put(colName, colValue);
             }
             afterMapList.add(afterMap);
        }
        BinLogRowData binLogRowData = new BinLogRowData();
        binLogRowData.setAfter(afterMapList);
        binLogRowData.setTable(table);

        return binLogRowData;
    }


    private List<Serializable[]> getAfterValues(EventData eventData) {
        if (eventData instanceof WriteRowsEventData) {
            return ((WriteRowsEventData)eventData).getRows();
        }
        if (eventData instanceof  UpdateRowsEventData) {
            return ((UpdateRowsEventData)eventData).getRows().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        }
        if (eventData instanceof DeleteRowsEventData) {
            return ((DeleteRowsEventData)eventData).getRows();
        }
        return Collections.emptyList() ;
    }

}
