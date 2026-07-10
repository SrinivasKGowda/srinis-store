package com.shopsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
public class ShopSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopSphereApplication.class, args);
    }
}
