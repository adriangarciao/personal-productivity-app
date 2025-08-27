package com.adriangarciao.person_productivity_app.exception;
import com.adriangarciao.person_productivity_app.model.Person;

/**
 * Exception thrown when a requested {@link Person} cannot be found in the database.
 */
public class PersonNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code PersonNotFoundException} for the specified person ID.
     *
     * @param id the ID of the person that was not found
     */
    public PersonNotFoundException(Long id) {
        super("Person with ID " + id + " not found.");
    }
}
