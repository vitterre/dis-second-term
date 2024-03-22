package com.technokratos.agona.config;

import com.technokratos.agona.model.Account;
import com.technokratos.agona.model.Stock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.math.BigDecimal;
import java.util.UUID;

@Configuration
@ComponentScan("com.technokratos.agona")
public class ApplicationConfig {

    @Bean
    public Account account() {
        return Account.builder()
                .accountUuid(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Bean
    @Scope("prototype")
    public Stock stock() {
        return Stock.builder()
                .stockUuid(UUID.randomUUID())
                .ticker("AAPL")
                .isin("US0378331005")
                .averagePrice(BigDecimal.valueOf(171.37))
                .build();
    }
}
