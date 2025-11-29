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

    @Test
    void unfinishTask_MarksTaskAsUnfinished() throws Exception {
        Task savedTask = createAndSaveTask("Task to flip", "Description");

        mockMvc.perform(patch("/tasks/{id}/finish", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finished").value(true));

        mockMvc.perform(patch("/tasks/{id}/unfinish", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finished").value(false));

        Task updated = taskRepository.findById(savedTask.getId()).orElseThrow();
        assertFalse(updated.getFinished());
    }

    @Test
    void getAllTasks_ReturnsAllTasks() throws Exception {
        createAndSaveTask("Task A", "A");
        createAndSaveTask("Task B", "B");

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTasksByPerson_ReturnsOnlyPersonsTasks() throws Exception {
        Task taskForTestPerson = createAndSaveTask("Mine", "desc");

        Person otherPerson = new Person("other@example.com", "Other User");
        otherPerson = personRepository.save(otherPerson);
        Task taskForOther = createAndSaveTaskWithOwner("Other Task", "desc", otherPerson);

        mockMvc.perform(get("/tasks/person/{personId}", testPerson.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value(taskForTestPerson.getTitle()));

        assertTrue(taskRepository.existsById(taskForOther.getId()));
    }

    @Test
    void deleteAllTasks_RemovesEverything() throws Exception {
        createAndSaveTask("Task A", "A");
        createAndSaveTask("Task B", "B");

        mockMvc.perform(delete("/tasks/all"))
                .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }

    @Test
    void filterByCategory_ReturnsMatchingTasks() throws Exception {
        Task workTask = createAndSaveTaskWithCategory("Work Task", Task.Category.WORK);
        createAndSaveTaskWithCategory("Personal Task", Task.Category.PERSONAL);

        mockMvc.perform(get("/tasks/category/{category}", Task.Category.WORK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value(workTask.getTitle()));
    }

    @Test
    void filterByPriority_ReturnsMatchingTasks() throws Exception {
        Task high = createAndSaveTaskWithPriority("High Task", Task.Priority.HIGH);
        createAndSaveTaskWithPriority("Low Task", Task.Priority.LOW);

        mockMvc.perform(get("/tasks/priority/{priority}", Task.Priority.HIGH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value(high.getTitle()));
    }

    @Test
    void sortByDueDate_ReturnsAscendingByDefault() throws Exception {
        Task early = createAndSaveTaskWithDueDate("Early", LocalDateTime.now().plusDays(1));
        Task later = createAndSaveTaskWithDueDate("Later", LocalDateTime.now().plusDays(3));

        mockMvc.perform(get("/tasks/sort/dueDate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(early.getTitle()))
                .andExpect(jsonPath("$[1].title").value(later.getTitle()));
    }

    @Test
    void sortByPriority_ReturnsDescendingWhenRequested() throws Exception {
        Task low = createAndSaveTaskWithPriority("Low Task", Task.Priority.LOW);
        Task high = createAndSaveTaskWithPriority("High Task", Task.Priority.HIGH);

        mockMvc.perform(get("/tasks/sort/priority")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(low.getTitle()))
                .andExpect(jsonPath("$[1].title").value(high.getTitle()));
    }

    @Test
    void findTaskForPersonInBetweenDates_FiltersByRange() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Task inside = createAndSaveTaskWithDueDate("Inside", now.plusDays(2));
        createAndSaveTaskWithDueDate("Outside", now.plusDays(10));

        mockMvc.perform(get("/tasks/{personId}", testPerson.getId())
                        .param("startDate", now.plusDays(1).toString())
                        .param("endDate", now.plusDays(5).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value(inside.getTitle()));
    }

    @Test
    void getTasksPaged_returnsPage() throws Exception {
        // create 15 tasks
        for (int i = 0; i < 15; i++) {
            createAndSaveTask("T" + i, "desc");
        }

        mockMvc.perform(get("/tasks/paged")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15));
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

    private Task createAndSaveTaskWithOwner(String title, String description, Person owner) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCreationDate(LocalDateTime.now());
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setFinished(false);
        task.setPriority(Task.Priority.MEDIUM);
        task.setCategory(Task.Category.WORK);
        task.setPerson(owner);
        return taskRepository.save(task);
    }

    private Task createAndSaveTaskWithCategory(String title, Task.Category category) {
        Task task = createAndSaveTask(title, "desc");
        task.setCategory(category);
        return taskRepository.save(task);
    }

    private Task createAndSaveTaskWithPriority(String title, Task.Priority priority) {
        Task task = createAndSaveTask(title, "desc");
        task.setPriority(priority);
        return taskRepository.save(task);
    }

    private Task createAndSaveTaskWithDueDate(String title, LocalDateTime dueDate) {
        Task task = createAndSaveTask(title, "desc");
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }
}
