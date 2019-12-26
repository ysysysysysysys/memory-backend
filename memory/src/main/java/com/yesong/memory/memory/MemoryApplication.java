package com.yesong.memory.memory;

import com.yesong.memory.memory.enums.MemoryType;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MemoryApplication {
    private final static Logger log = LoggerFactory.getLogger(MemoryApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(MemoryApplication.class, args);
        log.info("init finsh");
    }

}
