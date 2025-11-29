package com.adriangarciao.person_productivity_app.controller;

import com.adriangarciao.person_productivity_app.dto.PersonDto;
import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Rest Controller for managing Persons.
 *
 * Provides endpoints to create, retrieve, update, and delete tasks.
 */
@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    /**
     * Creates a new {@code PersonController}.
     *
     * @param personService the service used to handle person operations
     */
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Retrieves a person by their unique ID.
     *
     * @param id the ID of the person to retrieve
     * @return the {@link PersonDto} containing the person's data
     *
     */
    @GetMapping("{id}")
    public PersonDto getPerson(@PathVariable long id){
        return personService.getPersonById(id);
    }

    /**
     * Deletes all persons in the system.
     *
     * @return 200 OK with a confirmation message once deletion is complete
     */
    @DeleteMapping
    public ResponseEntity<?> deleteAllPersons(){
        personService.deleteAllPersons();
        return ResponseEntity.ok("All persons deleted successfully.");
    }

    /**
     * Creates a new person.
     *
     * @param personDto the person data to create
     * @return 201 Created with the saved {@link PersonDto}
     *
     */
    @PostMapping
    public ResponseEntity<PersonDto> addPerson(@RequestBody @Valid PersonDto personDto) {
        PersonDto savedPerson = personService.addPerson(personDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    /**
     * Deletes a specific person by ID.
     *
     * @param id the ID of the person to delete
     * @return 200 OK with a confirmation message once deletion is complete
     *
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable long id){
        personService.deletePersonById(id);
        return ResponseEntity.ok("Person deleted successfully.");
    }

    /**
     * Updates an existing person with new data.
     *
     * @param id        the ID of the person to update
     * @param personDto the updated person data
     * @return 200 OK with the updated {@link PersonDto}
     *
     */
    @PatchMapping("/{id}")
    public ResponseEntity<PersonDto> updatePerson(@PathVariable long id, @RequestBody PersonDto personDto) {
        PersonDto updatedPerson = personService.updatePerson(id, personDto);
        return ResponseEntity.ok(updatedPerson);
    }

    /**
     * Retrieves all persons.
     *
     * @return the list of all {@link PersonDto}s
     */
    @GetMapping
    public List<PersonDto> getAllPersons(){
        return personService.getAllPersons();
    }

    /**
     * Returns a pageable list of persons. Backwards-compatible: keeps existing `/persons` endpoint returning a list.
     * Pageable query params: `page`, `size`, `sort`.
     */
    @GetMapping("/paged")
    public Page<PersonDto> getAllPersonsPaged(Pageable pageable) {
        return personService.getAllPersons(pageable);
    }

    /**
     * Finds all persons with the given name.
     *
     * @param name the name to search for
     * @return the list of matching {@link PersonDto}s
     */
    @GetMapping("/name/{name}")
    public List<PersonDto> findByName(@PathVariable String name){
        return personService.findByName(name);
    }


    /**
     * Finds a person by their email address.
     *
     * @param email the email to search for
     * @return the matching {@link PersonDto}
     *
     */
    @GetMapping("/email/{email}")
    public PersonDto findByEmail(@PathVariable String email){
        return personService.findByEmail(email);
    }
}
