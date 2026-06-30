package com.bbv.payment_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.bbv.payment_batch", "com.bbv.shared_core_api"})
@EnableJpaRepositories(basePackages = "com.bbv.payment_batch.repository")
@EntityScan(basePackages = "com.bbv.shared_core_api.domain.model") // Lê as entidades do Core
@EnableScheduling // 🚀 ATIVA O AGENDADOR DE TAREFAS NO PROJETO
public class PaymentBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentBatchApplication.class, args);
	}

}
