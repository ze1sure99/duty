package com.bank.duty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动程序
 */
@SpringBootApplication(
        exclude = { DataSourceAutoConfiguration.class ,
        org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration.class,
        org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration.class
        })
@ComponentScan("com.bank.duty.*")
public class BankDutyApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankDutyApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  银行履职管理系统启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}