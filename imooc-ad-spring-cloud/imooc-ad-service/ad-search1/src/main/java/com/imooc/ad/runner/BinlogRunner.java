package com.imooc.ad.runner;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.imooc.ad.mysql.BinlogConfig;
import com.imooc.ad.mysql.listen.BinlogClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BinlogRunner implements CommandLineRunner {

    @Autowired
    private BinlogClient client;

    @Override
    public void run(String... args) throws Exception {
        log.info("coming in binLogRunner.....");
        client.connect();
    }
}
