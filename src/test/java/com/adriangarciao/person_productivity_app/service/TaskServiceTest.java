package com.adriangarciao.person_productivity_app.service;

import com.adriangarciao.person_productivity_app.dto.TaskCreateDto;
import com.adriangarciao.person_productivity_app.dto.TaskDto;
import com.adriangarciao.person_productivity_app.exception.PersonNotFoundException;
import com.adriangarciao.person_productivity_app.exception.TaskNotFoundException;
import com.adriangarciao.person_productivity_app.exception.TaskOwnershipException;
import com.adriangarciao.person_productivity_app.mapper.TaskMapper;
import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.model.Task;
import com.adriangarciao.person_productivity_app.repository.PersonRepository;
import com.adriangarciao.person_productivity_app.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {


    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    private Task createTestTask(TaskCreateDto dto, Person person) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDueDate(dto.dueDate());
        task.setCompletionDate(null);
        task.setFinished(false);
        task.setPriority(dto.priority());
        task.setCategory(dto.category());
        task.setPerson(person);

        task.setCreationDate(LocalDateTime.now());

        return task;
    }

    private TaskDto createTestTaskDto(Task task, Person person){
        var personDto = new com.adriangarciao.person_productivity_app.dto.PersonDto(person.getId(), person.getName(), person.getEmail());
        TaskDto taskDto = new TaskDto(
                task.getTitle(),
                task.getDescription(),
                task.getCreationDate(),
                task.getDueDate(),
                task.getCompletionDate(),
                task.getFinished(),
                task.getPriority(),
                task.getCategory(),
                personDto
        );
        return taskDto;
    }

    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTask_success() {
        long personId = 1L;
        Person person = new Person();
        person.setId(personId);

        TaskCreateDto createDto = new TaskCreateDto(
                "Title",
                "Desc",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), // dueDate
                null, // completionDate
                false, // finished
                Task.Priority.HIGH,
                Task.Category.WORK
        );

        Task task = createTestTask(createDto, person);

        var personDto = new com.adriangarciao.person_productivity_app.dto.PersonDto(person.getId(), person.getName(), person.getEmail());
        TaskDto taskDto = new TaskDto(task.getTitle(), task.getDescription(), task.getCreationDate(), task.getDueDate(),
            task.getCompletionDate(), task.getFinished(), task.getPriority(), task.getCategory(), personDto);


        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(taskMapper.TaskCreateDtoToTask(createDto)).thenReturn(task);
        when(taskMapper.taskToTaskDTO(task)).thenReturn(taskDto);
        when(taskRepository.save(task)).thenReturn(task);


        TaskDto result = taskService.addTask(createDto, personId);


        assertNotNull(result);
        assertEquals(taskDto, result);
        assertFalse(task.getFinished()); // finished should be false
        assertEquals(person, task.getPerson()); // task should be assigned to the correct person

        verify(taskRepository, times(1)).save(task);
        verify(taskMapper, times(1)).TaskCreateDtoToTask(createDto);
        verify(taskMapper, times(1)).taskToTaskDTO(task);
    }

    @Test
    void addTask_personNotFound_throwsException() {
        long missingPersonId = 999L;
        Person person = new Person();
        person.setId(missingPersonId);

        TaskCreateDto createDto = new TaskCreateDto(
                "Title",
                "Desc",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), // dueDate
                null, // completionDate
                false, // finished
                Task.Priority.HIGH,
                Task.Category.WORK
        );

        when(personRepository.findById(missingPersonId)).thenReturn(Optional.empty());

        //ensure PersonNotFoundException is thrown
        PersonNotFoundException exception = assertThrows(
                PersonNotFoundException.class,
                () -> taskService.addTask(createDto, missingPersonId)
        );

        assertTrue(exception.getMessage().contains(String.valueOf(missingPersonId)));

        // Verify no task was saved or mapped since person lookup failed
        verify(taskRepository, never()).save(any(Task.class));
        verify(taskMapper, never()).TaskCreateDtoToTask(any(TaskCreateDto.class));
        verify(taskMapper, never()).taskToTaskDTO(any(Task.class));
    }

    @Test
    void getTaskById_success() {
        long taskId = 1L;

        Person person = new Person();
        person.setId(1L);

        Task task = new Task();
        task.setTitle("Title");
        task.setDescription("Desc");
        task.setDueDate(LocalDateTime.now());
        task.setPerson(person);
        task.setFinished(false);

        TaskDto taskDto = createTestTaskDto(task, person);


        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.taskToTaskDTO(task)).thenReturn(taskDto);


        TaskDto result = taskService.getTaskById(taskId);


        assertNotNull(result);
        assertEquals(taskDto, result);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskMapper, times(1)).taskToTaskDTO(task);
    }

    @Test
    void getTaskById_taskNotFound_throwsException() {
        long taskId = 1L;


        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());


        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskMapper, never()).taskToTaskDTO(any());
    }

    @Test
    void deleteTask_notOwner_throwsTaskOwnershipException() {
        long taskId = 1L;
        long ownerId = 10L;
        long currentUserId = 20L; // not the owner

        Person owner = new Person();
        owner.setId(ownerId);

        Task task = createTestTask(new TaskCreateDto(
                "Title",
                "Desc",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), // dueDate
                null, // completionDate
                false, // finished
                Task.Priority.HIGH,
                Task.Category.WORK
        ), owner);
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));


        assertThrows(TaskOwnershipException.class, () -> {
            taskService.deleteTaskById(currentUserId, taskId);
        });


        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void deleteTaskById_success() {

        Long taskId = 1L;
        Long userId = 100L;

        Person owner = new Person();
        owner.setId(userId);

        Task task = createTestTask(new TaskCreateDto(
                "Title",
                "Desc",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), // dueDate
                null, // completionDate
                false, // finished
                Task.Priority.HIGH,
                Task.Category.WORK
        ), owner);
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when
        taskService.deleteTaskById(userId, taskId); // personId first, then taskId

        // then
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
        verifyNoMoreInteractions(taskRepository);
    }
}
