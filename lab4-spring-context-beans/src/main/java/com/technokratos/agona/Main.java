package com.technokratos.agona;

import com.technokratos.agona.config.ApplicationConfig;
import com.technokratos.agona.model.Account;
import com.technokratos.agona.model.Stock;
import lombok.val;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        val applicationContext =
                new AnnotationConfigApplicationContext(ApplicationConfig.class);

        val account = (Account) applicationContext.getBean(Account.class);
        System.out.println(account);

        for (int i = 0; i < 5; i++) {
            val stock = (Stock) applicationContext.getBean(Stock.class);
            System.out.println(stock);
        }
    }
}