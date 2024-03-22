package com.technokratos.agona;

import com.technokratos.agona.config.DatabaseConfig;
import com.technokratos.agona.model.Country;
import com.technokratos.agona.orm.EntityManager;
import lombok.val;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        val context = new AnnotationConfigApplicationContext(DatabaseConfig.class);

        // migrations
        val flyway = (Flyway) context.getBean("flyway");
        flyway.migrate();

        // entity manager
        val entityManager = (EntityManager) context.getBean(EntityManager.class);
        entityManager.validateEntities();

        // repository
        val countryRepository = (CountryRepository) context.getBean(CountryRepository.class);
        val authorRepository = (AuthorRepository) context.getBean(AuthorRepository.class);

        val author = authorRepository.findById(1);

        System.out.println(author);

    }
}