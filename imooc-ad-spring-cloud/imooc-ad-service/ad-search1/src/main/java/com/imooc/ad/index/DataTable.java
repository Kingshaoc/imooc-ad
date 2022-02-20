package com.imooc.ad.index;


import org.hibernate.stat.SecondLevelCacheStatistics;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
