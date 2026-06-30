package com.bbv.payment_batch.config;

import com.bbv.payment_batch.repository.PaymentBatchRepository;
import com.bbv.shared_core_api.domain.model.Transaction;
import com.bbv.shared_core_api.domain.enums.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Configuration
public class PaymentBatchConfig {

    private final PaymentBatchRepository repository;

    // 1. READER: Lê os registros PENDENTES diretamente do banco de dados
    @Bean
    public ItemReader<Transaction> transactionReader() {
        return new ItemReader<Transaction>() {
            private List<Transaction> transactions;
            private int nextIndex = 0;

            @Override
            public Transaction read() {
                // Carrega a lista na primeira execução do bloco
                if (transactions == null) {
                    transactions = repository.findByStatus(TransactionStatus.PENDENTE);
                    System.out.println("[BATCH-READER] Encontradas " + transactions.size() + " transações PENDENTES.");
                }

                if (nextIndex < transactions.size()) {
                    return transactions.get(nextIndex++);
                }

                transactions = null; // Reseta para a próxima execução do Job
                nextIndex = 0;
                return null; // Indica fim do lote (EOF)
            }
        };
    }

    // 2. PROCESSOR: Executa as regras de negócio em lote (Simulação de conciliação)
    @Bean
    public ItemProcessor<Transaction, Transaction> transactionProcessor() {
        return transaction -> {
            System.out.println("[BATCH-PROCESSOR] Conciliando transação ID: " + transaction.getId() + " | Código: " + transaction.getCodigoTransacao());

            transaction.setStatus(TransactionStatus.PROCESSADO);
            transaction.setDataTransferencia(LocalDateTime.now()); // 🚀 HORA DA TRANSFERÊNCIA

            return transaction;
        };
    }

    // 3. WRITER: Grava as transações atualizadas de volta no banco (Commit em bloco)
    @Bean
    public ItemWriter<Transaction> transactionWriter() {
        return chunk -> {
            System.out.println("[BATCH-WRITER] Gravando lote de " + chunk.size() + " transações atualizadas no banco.");
            repository.saveAll(chunk.getItems());
        };
    }

    // 4. STEP: Junta o Reader, Processor e Writer em um bloco que processa de 10 em 10 (chunk)
    @Bean
    public Step conciliationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("conciliationStep", jobRepository)
                .<Transaction, Transaction>chunk(10, transactionManager)
                .reader(transactionReader())
                .processor(transactionProcessor())
                .writer(transactionWriter())
                .build();
    }

    // 5. JOB: O motor principal que orquestra e executa o Step
    @Bean
    public Job conciliationJob(JobRepository jobRepository, Step conciliationStep) {
        return new JobBuilder("conciliationJob", jobRepository)
                .start(conciliationStep)
                .build();
    }
}