package com.adriangarciao.person_productivity_app.controller;

import com.adriangarciao.person_productivity_app.PersonProductivityApp;
import com.adriangarciao.person_productivity_app.dto.TaskCreateDto;
import com.adriangarciao.person_productivity_app.dto.TaskDto;
import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.model.Task;
import com.adriangarciao.person_productivity_app.repository.PersonRepository;
import com.adriangarciao.person_productivity_app.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = PersonProductivityApp.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test") // Use test profile for test database
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManager entityManager;

    private Person testPerson;
    private TaskCreateDto sampleTaskCreateDto;

    @BeforeEach
    void setUp() {

        taskRepository.deleteAll();
        personRepository.deleteAll();


        entityManager.flush();
        entityManager.clear();


        testPerson = new Person("john.doe@example.com", "John Doe");
        testPerson = personRepository.save(testPerson);


        sampleTaskCreateDto = new TaskCreateDto(
                "Integration Test Task",
                "Test Description",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                null,
                false,
                Task.Priority.MEDIUM,
                Task.Category.WORK
        );
    }

    @AfterEach
    void cleanUp() {

        taskRepository.deleteAll();
        personRepository.deleteAll();


        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void addTask_ValidInput_CreatesAndReturnsTask() throws Exception {
        // When & Then
        mockMvc.perform(post("/tasks/{person_id}", testPerson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTaskCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.category").value("WORK"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.finished").value(false));


        List<Task> tasks = taskRepository.findAll();
        assertEquals(1, tasks.size());
        assertEquals("Integration Test Task", tasks.get(0).getTitle());
        assertEquals(testPerson.getId(), tasks.get(0).getPerson().getId());
    }

    @Test
    void addTask_InvalidPersonId_ReturnsNotFound() throws Exception {
        long invalidPersonId = 99999L;

        mockMvc.perform(post("/tasks/{person_id}", invalidPersonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleTaskCreateDto)))
                .andExpect(status().isNotFound());


        List<Task> tasks = taskRepository.findAll();
        assertEquals(0, tasks.size());
    }

    @Test
    void getTaskById_ExistingTask_ReturnsTask() throws Exception {

        Task savedTask = createAndSaveTask("Get Test Task", "Description");

        // When & Then
        mockMvc.perform(get("/tasks/{id}", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Get Test Task"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void getTaskById_NonExistentTask_ReturnsNotFound() throws Exception {
        long nonExistentId = 99999L;

        mockMvc.perform(get("/tasks/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteTaskById_ExistingTask_DeletesTask() throws Exception {
        Task savedTask = createAndSaveTask("To Delete Task", "Description");

        mockMvc.perform(delete("/tasks/person/{person_id}/task/{id}", testPerson.getId(), savedTask.getId()))
                .andExpect(status().isOk());

        // Verify task was deleted
        assertFalse(taskRepository.existsById(savedTask.getId()));
    }

    @Test
    void updateTask_ValidInput_UpdatesTask() throws Exception {
        Task savedTask = createAndSaveTask("Original Task", "Original Description");

        TaskDto updateDto = new TaskDto(
                "Updated Task",
                "Updated Description",
                savedTask.getCreationDate(),
                LocalDateTime.now().plusDays(5),
                null,
                false,
                Task.Priority.HIGH,
                Task.Category.PERSONAL,
                testPerson
        );

        mockMvc.perform(patch("/tasks/{id}", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.priority").value("HIGH"));


        Task updatedTask = taskRepository.findById(savedTask.getId()).orElseThrow();
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(Task.Priority.HIGH, updatedTask.getPriority());
    }

    @Test
    void finishTask_ExistingTask_MarksTaskAsFinished() throws Exception {
        Task savedTask = createAndSaveTask("Task to Finish", "Description");

        mockMvc.perform(patch("/tasks/{id}/finish", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finished").value(true))
                .andExpect(jsonPath("$.completionDate").exists());


        Task finishedTask = taskRepository.findById(savedTask.getId()).orElseThrow();
        assertTrue(finishedTask.getFinished());
        assertNotNull(finishedTask.getCompletionDate());
    }


    // Helper method for creating test data

    private Task createAndSaveTask(String title, String description) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCreationDate(LocalDateTime.now());
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setFinished(false);
        task.setPriority(Task.Priority.MEDIUM);
        task.setCategory(Task.Category.WORK);

        Person managedPerson = personRepository.findById(testPerson.getId())
                .orElseThrow(() -> new RuntimeException("Test person not found"));
        task.setPerson(managedPerson);

        return taskRepository.save(task);
    }
}
