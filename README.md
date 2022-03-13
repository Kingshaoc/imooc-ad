# imooc-ad
imooc-ad


## 项目整体架构
![在这里插入图片描述](https://img-blog.csdnimg.cn/769f736b77ed4634821200c60e50cf40.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yir5om-5oiR5omT55CD5LqG,size_20,color_FFFFFF,t_70,g_se,x_16)
## 服务介绍

| ad-eureka       | 注册中心                      |              |
|-----------------|-------------------------------|:-------------|
| ad-gateway      | 服务网关（zuul）                |              |
| immoocadservice | 广告系统服务                  |              |
ad-common | 通用服务包   |
|                 | ad-dashboard                  | 通用数据看板 |
|                 | ad-search                     | 广告检索系统 |
|                 | ad-sponsor                    | 广告投放系统 |

##  涉及技术栈
● springcloud （eureka、feign、zuul） 
● mysql(binlog)
● 本地缓存
● kafka

## 核心服务

### 广告投放系统

> 接受广告投放方的数据

![在这里插入图片描述](https://img-blog.csdnimg.cn/e4d37382cb1a4fb3ad6bd50542747d2d.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yir5om-5oiR5omT55CD5LqG,size_20,color_FFFFFF,t_70,g_se,x_16)

用户创建推广计划，推广计划关联推广单元，推广单元存在维度限制（关键词、地域、兴趣等）、推广单元又关联广告创意具体形式。

### 库表设计
![在这里插入图片描述](https://img-blog.csdnimg.cn/f1eb8015979c43a29b59521f24e586bc.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yir5om-5oiR5omT55CD5LqG,size_20,color_FFFFFF,t_70,g_se,x_16)

## 广告检索系统

### 索引实现

● 正向索引：通过唯一键、主键生成与对象之间的映射关系

● 倒排索引：
也称为返乡索引，是一种索引方法，他的设计师为了存储在全文搜索下某个单词在一个或一组文档中存储位置的映射，是在文档检索系统中最常用的数据结构
![在这里插入图片描述](https://img-blog.csdnimg.cn/95205ab6f40e4d13ad75642168f30bbf.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yir5om-5oiR5omT55CD5LqG,size_20,color_FFFFFF,t_70,g_se,x_16)

● 地域限制 （province_city -> unitid）
● 关键词限制 （keyword -> unitid）
● 兴趣限制 （ittag_unitid）
![在这里插入图片描述](https://img-blog.csdnimg.cn/cca4f8cd69b14034bd5055cf1014e232.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yir5om-5oiR5omT55CD5LqG,size_20,color_FFFFFF,t_70,g_se,x_16)

#### 索引操作接口


```java
public interface IndexAware<K, V> {


    V get(K key);

    void add(K key, V value);

    void update(K key, V value);

    void delete(K key, V vlaue);

}
```


#### 正向索引与倒排索引举例


```java
/**
 * 推广单元限制
 *  地域限制
 *     倒排索引
 *      province-city -> set<Long> unitId
 *      通过省市信息找到所有的推广单元Id
 *  正向索引
 *      unitId -> province-city
 */
@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String, Set<Long>> {

    //逆向索引
    private static Map<String, Set<Long>> districtUnitMap;

    //正向索引
    private static Map<Long, Set<String>> unitDistrictMap;
    
    //.......维护索引操作
    
}

```

#### 索引服务类缓存： 总目录


```java
/**
 * 索引服务类缓存
 */
@Component
public class DataTable implements ApplicationContextAware, PriorityOrdered {

    private static ApplicationContext applicationContext;

    public static final Map<Class, Object> dataTableMap = new ConcurrentHashMap<>();

    //获取spring启动的上下文
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DataTable.applicationContext = applicationContext;
    }

    // 定义启动加载类的顺序
    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    /**
     * 获取实现类的方法
     * ex : DataTable.of(CreativeUnitIndex.class)
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T of (Class<T> clazz) {
        T instance = (T)dataTableMap.get(clazz);
        if (instance != null) {
            return instance;
        }
        dataTableMap.put(clazz, bean(clazz));
        return (T)dataTableMap.get(clazz);
    }


    private static <T> T bean(String beanName) {
        return (T)applicationContext.getBean(beanName);
    }

    private static <T> T bean(Class clazz) {
        return (T)applicationContext.getBean(clazz);
    }
}

```

#### 索引更新层级维护 索引之间存在依赖关系，需定义索引的加载顺序

● 二级索引
  ○ adplan
  ○ adcreative
● 三级索引
  ○ adunit
  ○ adcreativeUnit
● 四级索引
  ○ 推广单元限制：地域
  ○ 推广单元限制:  兴趣
  ○ 推广单元限制：关键词

```java
// 第二级索引 adplan
    public static void handleLevel2(AdPlanTable planTable, OpType opType) {
        AdPlanObject planObject = new AdPlanObject(planTable.getId(), planTable.getUserId(), planTable.getPlanStatus(), planTable.getStartDate(), planTable.getEndDate());
        handleBinlogEvent(DataTable.of(AdPlanIndex.class),  planObject.getPlanId(), planObject, opType);
    }

    // 第二级索引 adCreative
    public static void handleLevel2(AdCreativeTable creativeTable, OpType opType) {
        CreativeObject creativeObject = new CreativeObject(creativeTable.getAdId(), creativeTable.getName(),
                creativeTable.getType(), creativeTable.getMaterialType(), creativeTable.getHeight(), creativeTable.getWidth(), creativeTable.getAuditStatus(), creativeTable.getAdUrl());
        handleBinlogEvent(DataTable.of(CreativeIndex.class),  creativeObject.getAdId(), creativeObject, opType);
    }

    //第三级别索引 adunit
    public static void handleLevel3(AdUnitTable adUnitTable, OpType opType) {

        AdPlanObject adPlanObject = DataTable.of(AdPlanIndex.class).get(adUnitTable.getPlanId());
        if (adPlanObject == null) {
            log.error("handleLevel3 found adplanObject error:{}", adUnitTable.getPlanId());
            return;
        }
        AdUnitObject adUnitObject = new AdUnitObject(adUnitTable.getUnitId(), adUnitTable.getUnitStatus(), adUnitTable.getPositionType(), adUnitTable.getPlanId(), adPlanObject);

        handleBinlogEvent(DataTable.of(AdUnitIndex.class), adUnitTable.getUnitId(), adUnitObject, opType);
    }

    //第三级别索引 adCreativeUnit
    public static void handleLevel3(AdCreativeUnitTable adCreativeUnitTable, OpType type) {
         if (type == OpType.UPDATE) {
             return;
         }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adCreativeUnitTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel3 found adUnitObject error:{}", adCreativeUnitTable.getUnitId());
            return;
        }
        CreativeObject creativeObject = DataTable.of(CreativeIndex.class).get(adCreativeUnitTable.getAdId());
        if (creativeObject == null) {
            log.error("handleLevel3 found creativeObject error:{}", adCreativeUnitTable.getAdId());
            return;
        }

        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(adCreativeUnitTable.getAdId(), adCreativeUnitTable.getUnitId());
        handleBinlogEvent(
                DataTable.of(CreativeUnitIndex.class),
                CommonUtils.stringConcat(creativeUnitObject.getAdId().toString(), creativeUnitObject.getUnitId().toString()),
                creativeUnitObject,
                type);

    }

    public static void handleLevel4(AdUnitDistrictTable adUnitDistrictTable, OpType type) {
        if (type == OpType.UPDATE) {
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adUnitDistrictTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel4 found adUnitObject error:{}", adUnitDistrictTable.getUnitId());
            return;
        }
        String key = CommonUtils.stringConcat(adUnitDistrictTable.getProvince(), adUnitDistrictTable.getCity());
        Set<Long> value = new HashSet<>(Collections.singleton(adUnitDistrictTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class), key, value, type);
    }

    public static void handleLevel4(AdUnitItTable adUnitItTable, OpType type) {
        if (type == OpType.UPDATE) {
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adUnitItTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel4 found adUnitObject error:{}", adUnitItTable.getUnitId());
            return;
        }
        Set<Long> value = new HashSet<>(Collections.singleton(adUnitItTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class), adUnitItTable.getItTag(), value, type);
    }

    public static void handleLevel4(AdUnitKeyWordTable adUnitKeyWordTable, OpType type) {
        if (type == OpType.UPDATE) {
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(adUnitKeyWordTable.getUnitId());
        if (adUnitObject == null) {
            log.error("handleLevel4 found adUnitObject error:{}", adUnitKeyWordTable.getUnitId());
            return;
        }
        Set<Long> value = new HashSet<>(Collections.singleton(adUnitKeyWordTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class), adUnitKeyWordTable.getKeyword(), value, type);
    }
```


### 全量索引实现

> 检索系统在启动时一次性读取当前数据库中的所有数据，建立索引

● 根据数据库导出的文件、读取文件、加载索引
● 核心类：IndexFileLoader


```java
/**
 * 根据数据库导出的文件、读取文件、加载索引
 */
@Component
@DependsOn("dataTable")
public class IndexFileLoader {

    @PostConstruct
    public void init() {
        List<String> adPlanStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_PLAN));
        adPlanStrings.forEach(p -> AdLevelDataHandler.handleLevel2(JSON.parseObject(p, AdPlanTable.class), OpType.ADD));

        List<String> adCreativeStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE));
        adCreativeStrings.forEach(p -> AdLevelDataHandler.handleLevel2(JSON.parseObject(p, AdCreativeTable.class), OpType.ADD));

        List<String> adUnits = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT));
        adUnits.forEach(p -> AdLevelDataHandler.handleLevel3(JSON.parseObject(p, AdUnitTable.class), OpType.ADD));

        List<String> adCreativeUnits = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE_UNIT));
        adCreativeUnits.forEach(p -> AdLevelDataHandler.handleLevel3(JSON.parseObject(p, AdCreativeUnitTable.class), OpType.ADD));

        List<String> adUnitDistricts = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_DISTIRCT));
        adUnitDistricts.forEach(p -> AdLevelDataHandler.handleLevel4(JSON.parseObject(p, AdUnitDistrictTable.class), OpType.ADD));

        List<String> adUnitIts = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_IT));
        adUnitIts.forEach(p -> AdLevelDataHandler.handleLevel4(JSON.parseObject(p, AdUnitItTable.class), OpType.ADD));

        List<String> adUnitkeyWords = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_KEYWORD));
        adUnitkeyWords.forEach(p -> AdLevelDataHandler.handleLevel4(JSON.parseObject(p, AdUnitKeyWordTable.class), OpType.ADD));
    }

    private List<String> loadDumpData(String fileName) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            return br.lines().collect(Collectors.toList());
        }catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}

```


#### 增量索引实现
系统运行过程中，监控数据库的变化，即增量数据，实施加载更新，构建索引

#### binglog
二进制日志，记录对数据发生或潜在发生更改的SQL语句，并以二进制的形式保存在磁盘中

##### 相关变量

变量名称	变量含义	相关语句
log_bin	binlog开关	show variables like 'log_bin'
binlog_format	binlog日志格式	show variables like 'binlog_fromat'

##### 三种格式

● row 仅保存记录被修改的细节，不记录sql语句上下文相关信息
● statement 每一条回修改数据的sql都会记录在binlog中
● mexed 以上两种level的混合使用

##### event_type:
● QUERY_EVENT: 与数据无关的操作，begin、drop table、 truncate table
● TABLE_MAP_EVENT:记录下一个操作所对应的表信息，存储了数据库名和表名
● XID_EVENT:标记事务提交
● WRITE_ROWS_EVENT: 插入数据
● UPDATE_ROWS_EVENT:更新数据
● DELETE_ROWS_EVENT:删除数据

##### mysql-binlog-connector-java
https://www.jianshu.com/p/a9dbd3fd52f3


#### 核心处理逻辑

![在这里插入图片描述](https://img-blog.csdnimg.cn/ff1da10aec944dca8761c6aa5274b909.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yir5om-5oiR5omT55CD5LqG,size_20,color_FFFFFF,t_70,g_se,x_16)



● AggregationListener 监听binlog
  ○ 过滤飞CRUD类的binlog
  ○ 分发处理类


```java
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

```

● IncrementLister 增量处理类
  ○ 包装成最好需要投递的对象
  ○ 去除模板中该操作对应的字段列表
  ○ 处理发生变化的列和列的值
  ○ 投递rowData

```java
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
```

● IndexSender
  ○ 根据binlog数据rowData更新索引
 

```java
@Override
    public void sender(MySqlRowData rowData) {
        String level = rowData.getLevel();
        if (DataLevel.LEVEL_2.getLevel().equals(level)) {
            Level2RowData(rowData);
        }else if (DataLevel.LEVEL_3.getLevel().equals(level)) {
            Level3RowData(rowData);
        }else if (DataLevel.LEVEL_4.getLevel().equals(level)) {
            Level4RowData(rowData);
        }else {
            log.error("MysqlRowData error : {}", JSON.toJSONString(rowData));
        }
    }
```

