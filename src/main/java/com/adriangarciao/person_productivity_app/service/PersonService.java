package com.adriangarciao.person_productivity_app.service;


import com.adriangarciao.person_productivity_app.dto.PersonDto;
import com.adriangarciao.person_productivity_app.exception.PersonNotFoundException;
import com.adriangarciao.person_productivity_app.exception.ResourceNotFoundException;
import com.adriangarciao.person_productivity_app.mapper.PersonMapper;
import com.adriangarciao.person_productivity_app.repository.PersonRepository;
import com.adriangarciao.person_productivity_app.model.Person;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for managing persons.
 *
 * Handles business logic related to creating, retrieving, updating, and deleting persons.
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    /**
     * Creates a new {@code PersonService}.
     *
     * @param personRepository the repository used to access person data
     * @param personMapper     the mapper used to convert between entities and DTOs
     */
    public PersonService(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    /**
     * Retrieves a person by their unique ID.
     *
     * @param id the ID of the person to retrieve
     * @return the corresponding {@link PersonDto}
     * @throws PersonNotFoundException if no person exists with the given ID
     */
    public PersonDto getPersonById(Long id) {
        Person person =  personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        return personMapper.personToPersonDto(person);
    }

    /**
     * Creates and saves a new person.
     *
     * @param personDto the person data to save
     * @return the saved {@link PersonDto}
     */
    public PersonDto addPerson(PersonDto personDto) {
        Person person = personMapper.personDtoToPerson(personDto);
        person = personRepository.save(person);
        return personMapper.personToPersonDto(person);
    }

    /**
     * Deletes a person by their ID.
     *
     * @param id the ID of the person to delete
     * @throws PersonNotFoundException if no person exists with the given ID
     */
    public void deletePersonById(Long id) {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException(id);
        }
        personRepository.deleteById(id);
    }

    /**
     * Deletes all persons from the system.
     */
    public void deleteAllPersons() {
        personRepository.deleteAll();
    }


    /**
     * Updates an existing person with new data.
     *
     * @param id        the ID of the person to update
     * @param personDto the updated person data
     * @return the updated {@link PersonDto}
     * @throws PersonNotFoundException if no person exists with the given ID
     */
    public PersonDto updatePerson(Long id, PersonDto personDto) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));


        personMapper.updatePerson(personDto, existingPerson);


        Person savedPerson = personRepository.save(existingPerson);
        return personMapper.personToPersonDto(savedPerson);
    }

    /**
     * Retrieves all persons in the system.
     *
     * @return a list of all {@link PersonDto}s
     */
    public List<PersonDto> getAllPersons() {
        return personMapper.personsToPersonDtos(personRepository.findAll());
    }

    /**
     * Returns a page of PersonDto using Spring Data pagination.
     */
    public Page<PersonDto> getAllPersons(Pageable pageable) {
        var page = personRepository.findAll(pageable);
        var dtos = personMapper.personsToPersonDtos(page.getContent());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * Finds all persons with the given name.
     *
     * @param name the name to search for
     * @return a list of {@link PersonDto}s matching the name
     */
    public List<PersonDto> findByName(String name) {
        List<Person> persons = personRepository.findByName(name);
        return personMapper.personsToPersonDtos(persons);
    }


    /**
     * Finds a person by their email address.
     *
     * @param email the email address to search for
     * @return the matching {@link PersonDto}
     * @throws ResourceNotFoundException if no person exists with the given email
     */
    public PersonDto findByEmail(String email) {
        Person person = personRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("Person not found with email: " + email));;
        return personMapper.personToPersonDto(person);
    }
}
