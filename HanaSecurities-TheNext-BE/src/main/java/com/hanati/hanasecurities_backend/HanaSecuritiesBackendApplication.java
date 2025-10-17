package com.hanati.hanasecurities_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.hanati"})
@EnableJpaRepositories(basePackages = "com.hanati")
@EntityScan(basePackages = "com.hanati")
public class HanaSecuritiesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanaSecuritiesBackendApplication.class, args);
    }

}
