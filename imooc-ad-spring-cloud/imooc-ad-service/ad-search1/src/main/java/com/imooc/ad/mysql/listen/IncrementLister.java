package com.imooc.ad.mysql.listen;

import com.github.shyiko.mysql.binlog.event.EventType;
import com.imooc.ad.mysql.constant.Constant;
import com.imooc.ad.mysql.constant.OpType;
import com.imooc.ad.mysql.dto.BinLogRowData;
import com.imooc.ad.mysql.dto.MySqlRowData;
import com.imooc.ad.mysql.dto.TableTemplate;
import com.imooc.ad.sender.ISender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IncrementLister implements  Ilistener{

    @Autowired
    private AggregationListener aggregationListener;

    @Resource(name = "indexSender")
    private ISender sender;

    @Override
    @PostConstruct
    public void register() {
        log.info("IncrementListener register db and table");
        Constant.table2Db.forEach((k, v) -> aggregationListener.register(v, k, this));
    }

    @Override
    public void onEvent(BinLogRowData eventData) {
        TableTemplate table = eventData.getTable();

        EventType eventType = eventData.getEventType();
        //包装成最好需要投递的对象
        MySqlRowData mySqlRowData = new MySqlRowData();
        mySqlRowData.setTableName(table.getTableName());
        mySqlRowData.setLevel(eventData.getTable().getLevel());

        OpType opType = OpType.to(eventType);
        mySqlRowData.setOpType(opType);

        //去除模板中该操作对应的字段列表
        List<String> fieldList = table.getOpTypeFieldSetMap().get(opType);
        if (fieldList == null) {
            log.warn("{} not support for {}", opType, table.getTableName());
            return;
        }
        //发生变化的列和列的值
        for (Map<String, String> afterMap : eventData.getAfter()) {
            Map<String, String>  _afterMap = new HashMap<>();
            for (Map.Entry<String, String> entry : afterMap.entrySet()) {
                String colName = entry.getKey();
                String colValue = entry.getValue();
                _afterMap.put(colName, colValue);
            }
            mySqlRowData.getFieldValueMap().add(_afterMap)  ;
        }

        //投递rowData
        sender.sender(mySqlRowData);
    }
}
