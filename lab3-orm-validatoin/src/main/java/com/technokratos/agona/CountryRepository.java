package com.technokratos.agona;

import com.technokratos.agona.model.Country;
import com.technokratos.agona.orm.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CountryRepository {

    private final EntityManager entityManager;

    public List<Country> findAll() {
        return entityManager.findAll(Country.class);
    }

    public Optional<Country> findById(Integer id) {
        return entityManager.findById(Country.class, id);
    }

    public void removeById(Integer id) {
        entityManager.remove(Country.class, id);
    }

    public Country save(Country country) {
        return entityManager.save(country);
    }
}
