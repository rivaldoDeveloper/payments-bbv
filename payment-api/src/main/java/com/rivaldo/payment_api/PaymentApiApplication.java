package com.rivaldo.payment_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// O scanBasePackages diz para o Spring gerenciar as classes deste projeto e do pacote com.bbv
@SpringBootApplication(scanBasePackages = {"com.rivaldo.payment_api", "com.bbv"})
// O EntityScan e o EnableJpaRepositories localizam as tabelas e interfaces do seu Core
@EntityScan(basePackages = "com.bbv.shared_core_api.domain")
@EnableJpaRepositories(basePackages = "com.bbv.shared_core_api.repository")
public class PaymentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApiApplication.class, args);
    }

}