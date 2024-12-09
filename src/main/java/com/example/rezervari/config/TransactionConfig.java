package com.example.rezervari.config;

import com.example.rezervari.transaction.TransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {

    @Bean
    public TransactionManager transactionManager() {
        return new TransactionManager();
    }
}
