package com.bbv.payment_batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PaymentJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job conciliationJob;

    public PaymentJobScheduler(JobLauncher jobLauncher, Job conciliationJob) {
        this.jobLauncher = jobLauncher;
        this.conciliationJob = conciliationJob;
    }

    // Executa a cada 60000 milissegundos (1 minuto)
    @Scheduled(fixedDelay = 60000)
    public void executarJobConciliacao() {
        System.out.println("[SCHEDULER] Verificando se há novas transações para conciliar...");

        try {
            // Criamos parâmetros dinâmicos com a data/hora para o Batch entender que é uma nova rodada
            JobParameters params = new JobParametersBuilder()
                    .addDate("currentTime", new Date())
                    .toJobParameters();

            // Dispara o motor do Spring Batch
            jobLauncher.run(conciliationJob, params);

        } catch (Exception e) {
            System.err.println("[SCHEDULER ERROR] Falha ao executar o Job: " + e.getMessage());
        }
    }
}