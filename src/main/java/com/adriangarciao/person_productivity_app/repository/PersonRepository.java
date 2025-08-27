package com.adriangarciao.person_productivity_app.repository;

import com.adriangarciao.person_productivity_app.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Person} entities.
 *
 * Extends {@link JpaRepository} to provide basic CRUD operations and adds
 * custom query methods for searching by name and email.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {


    /**
     * Finds all persons with the given name.
     *
     * @param name the name to search for
     * @return a list of {@link Person} entities matching the name,
     *         or an empty list if none found
     */
    List<Person> findByName(String name);

    /**
     * Finds a person by their email address.
     *
     * @param email the email to search for
     * @return an {@link Optional} containing the matching {@link Person}
     *         if found, or {@link Optional#empty()} if not found
     */
    Optional<Person> findByEmail(String email);
}
