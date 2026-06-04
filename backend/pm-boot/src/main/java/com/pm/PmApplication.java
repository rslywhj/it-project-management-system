package com.pm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.pm")
@MapperScan(basePackages = {
        "com.pm.common.mapper",
        "com.pm.project.mapper",
        "com.pm.requirement.mapper",
        "com.pm.task.mapper",
        "com.pm.milestone.mapper",
        "com.pm.promotion.mapper",
        "com.pm.test.mapper",
        "com.pm.delivery.mapper",
        "com.pm.lowcode.mapper",
        "com.pm.risk.mapper",
        "com.pm.resource.mapper",
        "com.pm.knowledge.mapper"
})
public class PmApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmApplication.class, args);
    }
}
