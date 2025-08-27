package com.adriangarciao.person_productivity_app.controller;

import com.adriangarciao.person_productivity_app.PersonProductivityApp;
import com.adriangarciao.person_productivity_app.dto.PersonDto;
import com.adriangarciao.person_productivity_app.mapper.PersonMapper;
import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersonProductivityApp.class)
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        personRepository.deleteAll();
    }

    @Test
    @Order(1)
    void addPerson_success_noIdInDto() throws Exception {
        PersonDto personDto = new PersonDto(null, "John Doe", "john@example.com");

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));


        // Verify the person was actually saved to the database
        List<Person> savedPersons = personRepository.findAll();
        assertEquals(1, savedPersons.size());
        assertEquals("John Doe", savedPersons.get(0).getName());
        assertEquals("john@example.com", savedPersons.get(0).getEmail());
        assertNotNull(savedPersons.get(0).getId());
    }

    @Test
    @Order(3)
    void getPersonById_success() throws Exception {

        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john@example.com");
        person = personRepository.save(person);

        mockMvc.perform(get("/persons/{id}", person.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @Order(4)
    void getPersonById_notFound() throws Exception {
        mockMvc.perform(get("/persons/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    void getAllPersons_success() throws Exception {
        // Add test data
        Person person1 = new Person();
        person1.setName("John Doe");
        person1.setEmail("john@example.com");

        Person person2 = new Person();
        person2.setName("Jane Smith");
        person2.setEmail("jane@example.com");

        personRepository.saveAll(List.of(person1, person2));

        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    @Order(6)
    void getAllPersons_emptyList() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Order(7)
    void updatePerson_success() throws Exception {

        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john@example.com");
        person = personRepository.save(person);

        // Update the person
        PersonDto updateDto = new PersonDto(null, "Jane Doe", "jane@example.com");

        mockMvc.perform(patch("/persons/{id}", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));

        // Verify the person was actually updated in the database
        Person updatedPerson = personRepository.findById(person.getId()).orElseThrow();
        assertEquals("Jane Doe", updatedPerson.getName());
        assertEquals("jane@example.com", updatedPerson.getEmail());
    }

    @Test
    @Order(8)
    void updatePerson_partialUpdate() throws Exception {
        // First create a person
        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john@example.com");
        person = personRepository.save(person);

        // Partial update (only email)
        PersonDto updateDto = new PersonDto(null,"John Doe", "newemail@example.com");

        mockMvc.perform(patch("/persons/{id}", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe")) // unchanged
                .andExpect(jsonPath("$.email").value("newemail@example.com")); // changed
    }

    @Test
    @Order(9)
    void updatePerson_notFound() throws Exception {
        PersonDto updateDto = new PersonDto(null, "Jane Doe", "jane@example.com");

        mockMvc.perform(patch("/persons/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    void deletePerson_success() throws Exception {
        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john@example.com");
        person = personRepository.save(person);

        mockMvc.perform(delete("/persons/{id}", person.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Person deleted successfully."));

        // Verify the person was actually deleted from the database
        assertFalse(personRepository.existsById(person.getId()));
    }

    @Test
    @Order(11)
    void deletePerson_notFound() throws Exception {
        mockMvc.perform(delete("/persons/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    void deleteAllPersons_success() throws Exception {

        Person person1 = new Person();
        person1.setName("John Doe");
        person1.setEmail("john@example.com");

        Person person2 = new Person();
        person2.setName("Jane Smith");
        person2.setEmail("jane@example.com");

        personRepository.saveAll(List.of(person1, person2));

        mockMvc.perform(delete("/persons"))
                .andExpect(status().isOk())
                .andExpect(content().string("All persons deleted successfully."));

        // Verify all persons were deleted
        assertEquals(0, personRepository.count());
    }

    @Test
    @Order(13)
    void findByName_success() throws Exception {

        Person person1 = new Person();
        person1.setName("John Doe");
        person1.setEmail("john1@example.com");

        Person person2 = new Person();
        person2.setName("John Doe");
        person2.setEmail("john2@example.com");

        Person person3 = new Person();
        person3.setName("Jane Smith");
        person3.setEmail("jane@example.com");

        personRepository.saveAll(List.of(person1, person2, person3));

        mockMvc.perform(get("/persons/name/{name}", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("John Doe"));
    }

    @Test
    @Order(14)
    void findByEmail_success() throws Exception {

        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john@example.com");
        personRepository.save(person);

        mockMvc.perform(get("/persons/email/{email}", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @Order(15)
    void findByEmail_notFound() throws Exception {
        mockMvc.perform(get("/persons/email/{email}", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Person not found with email")));
    }
}