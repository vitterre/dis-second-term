package com.technokratos.agona;

import com.technokratos.agona.model.Author;
import com.technokratos.agona.orm.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthorRepository {

    private final EntityManager entityManager;

    public Optional<Author> findById(Integer id) {
        return entityManager.findById(Author.class, id);
    }
}
