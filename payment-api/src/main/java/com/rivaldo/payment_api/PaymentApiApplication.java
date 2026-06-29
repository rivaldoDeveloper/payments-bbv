package com.rivaldo.payment_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.rivaldo.payment_api", "com.bbv.shared_core_api"})
@EnableJpaRepositories(basePackages = "com.rivaldo.payment_api.repository")
@EntityScan(basePackages = "com.bbv.shared_core_api.domain.model")
public class PaymentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApiApplication.class, args);
    }

}