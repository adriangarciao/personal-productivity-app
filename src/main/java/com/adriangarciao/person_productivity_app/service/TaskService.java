package com.adriangarciao.person_productivity_app.service;

import com.adriangarciao.person_productivity_app.dto.TaskCreateDto;
import com.adriangarciao.person_productivity_app.dto.TaskDto;
import com.adriangarciao.person_productivity_app.exception.PersonNotFoundException;
import com.adriangarciao.person_productivity_app.exception.TaskNotFoundException;
import com.adriangarciao.person_productivity_app.exception.TaskOwnershipException;
import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.model.Task;
import com.adriangarciao.person_productivity_app.repository.PersonRepository;
import com.adriangarciao.person_productivity_app.repository.TaskRepository;
import jakarta.validation.Valid;
import com.adriangarciao.person_productivity_app.mapper.TaskMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing {@link Task} entities.
 *
 * Provides business logic for creating, retrieving, updating, deleting,
 * filtering, and sorting tasks. Interacts with the {@link TaskRepository},
 * {@link PersonRepository}, and uses {@link TaskMapper} for DTO conversions.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final PersonRepository personRepository;
    private final TaskMapper  taskMapper;

    /**
     * Instantiates a new Task service.
     *
     * @param taskRepository   the repository for task persistence
     * @param personRepository the repository for person persistence
     * @param taskMapper       the mapper for converting between entities and DTOs
     */
    public TaskService(TaskRepository taskRepository, PersonRepository personRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.personRepository = personRepository;
        this.taskMapper = taskMapper;
    }


    /**
     * Creates and assigns a new task to a specific person.
     *
     * @param taskCreateDto DTO containing task creation details
     * @param personId      the ID of the person the task is assigned to
     * @return the created task as a DTO
     * @throws PersonNotFoundException if the person with the given ID does not exist
     */
    public TaskDto addTask(@Valid TaskCreateDto taskCreateDto, long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        Task task = taskMapper.TaskCreateDtoToTask(taskCreateDto);
        task.setFinished(false);
        task.setPerson(person);
        taskRepository.save(task);
        return taskMapper.taskToTaskDTO(task);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the task ID
     * @return the task as a DTO
     * @throws TaskNotFoundException if no task with the given ID exists
     */
    public TaskDto getTaskById(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.taskToTaskDTO(task);
    }

    /**
     * Retrieves all tasks.
     *
     * @return list of all tasks as DTOs
     */
    public List<TaskDto> getAllTask() {

        return taskMapper.tasksToTaskDtos(taskRepository.findAll());
    }

    /**
     * Deletes all tasks from the repository.
     */
    public void deleteAllTask() {
        taskRepository.deleteAll();
    }

    /**
     * Deletes a task by its ID, ensuring it belongs to the given person.
     *
     * @param person_id the ID of the person who owns the task
     * @param id        the ID of the task
     * @throws TaskNotFoundException   if the task with the given ID does not exist
     * @throws TaskOwnershipException if the task does not belong to the given person
     */
    public void deleteTaskById(long person_id, long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        Person person = task.getPerson();
        if(person.getId() != person_id){
            throw new TaskOwnershipException(person_id, id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Updates an existing task.
     *
     * @param id      the ID of the task to update
     * @param taskDto DTO containing updated task data
     * @return the updated task as a DTO
     * @throws TaskNotFoundException if no task with the given ID exists
     */
    public TaskDto updateTask(long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskMapper.updateTask(taskDto, task);
        taskRepository.save(task);

        return taskMapper.taskToTaskDTO(task);
    }

    /**
     * Retrieves all tasks for a given person.
     *
     * @param personId the person ID
     * @return list of tasks assigned to the person
     * @throws PersonNotFoundException if the person with the given ID does not exist
     */
    public List<TaskDto> getTasksByPersonId(long personId) {
        personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        List<Task> tasks = taskRepository.findByPersonId(personId);
        return taskMapper.tasksToTaskDtos(tasks);
    }

    /**
     * Filters tasks by exact due date.
     *
     * @param dueDate the due date to filter by
     * @return list of tasks due on the given date
     */
    public List<TaskDto> filterByDueDate(LocalDateTime dueDate) {
        return taskMapper.tasksToTaskDtos(taskRepository.findByDueDate(dueDate));
    }

    /**
     * Filters tasks within a due date range.
     *
     * @param startDate start of the range
     * @param endDate   end of the range
     * @return list of tasks within the given date range
     */
    public List<TaskDto> filterByDueDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return taskMapper.tasksToTaskDtos(taskRepository.findByDueDateBetween(startDate, endDate));
    }

    /**
     * Retrieves tasks for a person that fall within a specific date range.
     *
     * @param personId  the person ID
     * @param startDate start of the range
     * @param endDate   end of the range
     * @return list of matching tasks
     * @throws PersonNotFoundException if the person with the given ID does not exist
     */
    public List<TaskDto> findTaskForPersonInBetweenDates(long personId,
                                                         LocalDateTime startDate,
                                                         LocalDateTime endDate) {
        personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        return taskMapper.tasksToTaskDtos(taskRepository.findTasksForPersonBetweenDates(personId, startDate, endDate));
    }

    /**
     * Marks a task as finished.
     *
     * @param id the task ID
     * @return the updated task as a DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    public TaskDto finishTask(long id) {
        Task task = taskRepository.findById(id).
                orElseThrow(()-> new TaskNotFoundException(id));
        task.setFinished(true);
        taskRepository.save(task);
        return taskMapper.taskToTaskDTO(task);
    }


    /**
     * Marks a task as unfinished.
     *
     * @param id the task ID
     * @return the updated task as a DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    public TaskDto unfinishTask(long id) {
        Task task = taskRepository.findById(id).
                orElseThrow(()-> new TaskNotFoundException(id));
        task.setFinished(false);
        taskRepository.save(task);
        return taskMapper.taskToTaskDTO(task);
    }

    /**
     * Filters tasks by category.
     *
     * @param category the category
     * @return list of tasks matching the category
     */
    public List<TaskDto> filterByCategory(Task.Category category) {
        return taskMapper.tasksToTaskDtos(taskRepository.findByCategory(category));
    }

    /**
     * Filters tasks by priority.
     *
     * @param priority the priority
     * @return list of tasks matching the priority
     */
    public List<TaskDto> filterByPriority(Task.Priority priority) {
        return taskMapper.tasksToTaskDtos(taskRepository.findByPriority(priority));
    }

    /**
     * Sorts tasks by due date.
     *
     * @param asc whether to sort in ascending (true) or descending (false) order
     * @return sorted list of tasks
     */
    public List<TaskDto> sortByDueDate(boolean asc) {
        Sort sort = asc ? Sort.by("dueDate").ascending() : Sort.by("dueDate").descending();
        List<Task> tasks = taskRepository.findAll(sort);
        return taskMapper.tasksToTaskDtos(tasks);
    }

    /**
     * Sorts tasks by priority.
     *
     * @param asc whether to sort in ascending (true) or descending (false) order
     * @return sorted list of tasks
     */
    public List<TaskDto> sortByPriority(boolean asc) {
        Sort sort = asc ? Sort.by("priority").ascending() : Sort.by("priority").descending();
        List<Task> tasks = taskRepository.findAll(sort);
        return taskMapper.tasksToTaskDtos(tasks);
    }


    /**
     * Sorts tasks by both due date and priority.
     *
     * @param dueAsc      sort order for due date (true for ascending)
     * @param priorityAsc sort order for priority (true for ascending)
     * @return sorted list of tasks
     */
    public List<TaskDto> sortByDueDateAndPriority(boolean dueAsc, boolean priorityAsc) {
        Sort sort = Sort.by(
                dueAsc ? Sort.Order.asc("dueDate") : Sort.Order.desc("dueDate"),
                priorityAsc ? Sort.Order.asc("priority") : Sort.Order.desc("priority")
        );

        List<Task> tasks = taskRepository.findAll(sort);
        return taskMapper.tasksToTaskDtos(tasks);
    }

    /**
     * Sorts tasks dynamically based on multiple fields and sort orders.
     *
     * @param sortFields the fields to sort by (e.g., "dueDate", "priority")
     * @param orders     the sort orders corresponding to each field ("asc" or "desc")
     * @return sorted list of tasks
     */
    public List<TaskDto> getSortedTasks(List<String> sortFields, List<String> orders) {
        List<Task> tasks = taskRepository.findAll();

        if (sortFields == null || sortFields.isEmpty()) {
            return taskMapper.tasksToTaskDtos(tasks);
        }

        Comparator<Task> comparator = null;

        for (int i = 0; i < sortFields.size(); i++) {
            String field = sortFields.get(i);
            String order = (orders != null && i < orders.size()) ? orders.get(i) : "asc";

            Comparator<Task> fieldComparator = switch (field) {
                case "dueDate" -> Comparator.comparing(Task::getDueDate);
                case "priority" -> Comparator.comparing(Task::getPriority);
                default -> null;
            };

            if (fieldComparator != null) {
                if ("desc".equalsIgnoreCase(order)) {
                    fieldComparator = fieldComparator.reversed();
                }
                comparator = (comparator == null) ? fieldComparator : comparator.thenComparing(fieldComparator);
            }
        }

        if (comparator != null) {
            tasks.sort(comparator);
        }

        return taskMapper.tasksToTaskDtos(tasks);
    }
}
