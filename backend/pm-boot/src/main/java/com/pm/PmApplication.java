package com.pm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * IT项目管理系统 - 启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.pm")
@MapperScan(basePackages = {"com.pm.project.mapper", "com.pm.requirement.mapper", "com.pm.task.mapper", "com.pm.promotion.mapper"})
public class PmApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmApplication.class, args);
    }
}
