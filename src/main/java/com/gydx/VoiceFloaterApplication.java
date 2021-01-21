package com.gydx;

import com.gydx.utils.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 拽小白
 */
@EnableScheduling
@SpringBootApplication
@EnableSwagger2
@PropertySource("classpath:constant.properties")
@MapperScan(basePackages = "com.gydx.mapper")
@EnableTransactionManagement
@EnableAsync
public class VoiceFloaterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(VoiceFloaterApplication.class, args);
        // 设置上下文
        SpringContextUtil.setApplicationContext(app);
    }

}
