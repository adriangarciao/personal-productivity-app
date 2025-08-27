package com.adriangarciao.person_productivity_app.service;

import com.adriangarciao.person_productivity_app.dto.PersonDto;
import com.adriangarciao.person_productivity_app.exception.PersonNotFoundException;
import com.adriangarciao.person_productivity_app.mapper.PersonMapper;
import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    private Person person;
    private PersonDto personDto;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setName("John Doe");
        person.setEmail("john@example.com");

        personDto = new PersonDto(null,"John Doe", "john@example.com");
    }

    @Test
    void getPersonById_success() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personMapper.personToPersonDto(person)).thenReturn(personDto);

        PersonDto result = personService.getPersonById(1L);

        assertEquals(personDto, result);
        verify(personRepository).findById(1L);
        verify(personMapper).personToPersonDto(person);
    }

    @Test
    void getPersonById_notFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.getPersonById(1L));
    }

    @Test
    void addPerson_success() {
        Person personToSave = new Person();
        personToSave.setName("John Doe");
        personToSave.setEmail("john@example.com");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setName("John Doe");
        savedPerson.setEmail("john@example.com");

        PersonDto expectedResult = new PersonDto(null, "John Doe", "john@example.com");

        when(personMapper.personDtoToPerson(personDto)).thenReturn(personToSave);
        when(personRepository.save(personToSave)).thenReturn(savedPerson);
        when(personMapper.personToPersonDto(savedPerson)).thenReturn(expectedResult);

        PersonDto result = personService.addPerson(personDto);

        assertEquals(expectedResult, result);
        verify(personRepository).save(personToSave);
        verify(personMapper).personDtoToPerson(personDto);
        verify(personMapper).personToPersonDto(savedPerson);
    }

    @Test
    void deletePersonById_success() {
        when(personRepository.existsById(1L)).thenReturn(true);

        personService.deletePersonById(1L);

        verify(personRepository).deleteById(1L);
    }

    @Test
    void deletePersonById_notFound() {
        when(personRepository.existsById(1L)).thenReturn(false);

        assertThrows(PersonNotFoundException.class, () -> personService.deletePersonById(1L));
    }

    @Test
    void deleteAllPersons() {
        personService.deleteAllPersons();
        verify(personRepository).deleteAll();
    }

    @Test
    void updatePerson_success_allFieldsChanged() {
        PersonDto updateDto = new PersonDto(null, "Jane Doe", "jane@example.com");
        PersonDto expectedResult = new PersonDto(null, "Jane Doe", "jane@example.com");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personMapper.personToPersonDto(any(Person.class))).thenReturn(expectedResult);

        PersonDto updated = personService.updatePerson(1L, updateDto);

        assertEquals("Jane Doe", updated.name());
        assertEquals("jane@example.com", updated.email());


        verify(personMapper).updatePerson(updateDto, person);
        verify(personRepository).save(person);
        verify(personMapper).personToPersonDto(person);
    }

    @Test
    void updatePerson_success_partialFieldsChanged() {
        PersonDto updateDto = new PersonDto(null, "John Doe", "jane@example.com");
        PersonDto expectedResult = new PersonDto(null, "John Doe", "jane@example.com");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personMapper.personToPersonDto(any(Person.class))).thenReturn(expectedResult);

        PersonDto updated = personService.updatePerson(1L, updateDto);

        assertEquals("John Doe", updated.name());
        assertEquals("jane@example.com", updated.email());

        verify(personMapper).updatePerson(updateDto, person);
        verify(personRepository).save(person);
    }

    @Test
    void updatePerson_noChanges() {
        PersonDto updateDto = new PersonDto(null, null, null);

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personMapper.personToPersonDto(person)).thenReturn(personDto);

        PersonDto updated = personService.updatePerson(1L, updateDto);

        assertEquals(personDto, updated);


        verify(personMapper).updatePerson(updateDto, person);
        verify(personRepository).save(person);
    }

    @Test
    void updatePerson_notFound() {
        PersonDto updateDto = new PersonDto(null, "Jane", "jane@example.com");
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.updatePerson(1L, updateDto));

        verify(personMapper, never()).updatePerson(any(), any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void getAllPersons() {
        List<Person> persons = Arrays.asList(person);
        List<PersonDto> expectedDtos = Arrays.asList(personDto);

        when(personRepository.findAll()).thenReturn(persons);
        when(personMapper.personsToPersonDtos(persons)).thenReturn(expectedDtos);

        List<PersonDto> result = personService.getAllPersons();

        assertEquals(1, result.size());
        assertEquals(personDto, result.get(0));
        verify(personRepository).findAll();
        verify(personMapper).personsToPersonDtos(persons);
    }

    @Test
    void findByName() {
        List<Person> persons = Arrays.asList(person);
        List<PersonDto> expectedDtos = Arrays.asList(personDto);

        when(personRepository.findByName("John Doe")).thenReturn(persons);
        when(personMapper.personsToPersonDtos(persons)).thenReturn(expectedDtos);

        List<PersonDto> result = personService.findByName("John Doe");

        assertEquals(1, result.size());
        assertEquals(personDto, result.get(0));
        verify(personRepository).findByName("John Doe");
        verify(personMapper).personsToPersonDtos(persons);
    }

    @Test
    void findByEmail() {
        when(personRepository.findByEmail("john@example.com")).thenReturn(Optional.ofNullable(person));
        when(personMapper.personToPersonDto(person)).thenReturn(personDto);

        PersonDto result = personService.findByEmail("john@example.com");

        assertEquals(personDto, result);
        verify(personRepository).findByEmail("john@example.com");
        verify(personMapper).personToPersonDto(person);
    }
}
