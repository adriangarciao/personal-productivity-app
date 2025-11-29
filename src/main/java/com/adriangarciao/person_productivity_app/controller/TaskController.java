package com.adriangarciao.person_productivity_app.controller;

import com.adriangarciao.person_productivity_app.dto.TaskCreateDto;
import com.adriangarciao.person_productivity_app.dto.TaskDto;
import com.adriangarciao.person_productivity_app.model.Task;
import com.adriangarciao.person_productivity_app.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * REST controller for managing tasks.
 *
 * Provides endpoints to create, retrieve, update, delete, filter, and sort tasks.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new {@code TaskController}.
     *
     * @param taskService the service used to handle task operations
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a new task for a specific person.
     *
     * @param taskCreateDto the task data to create
     * @param person_id     the ID of the person to associate with the task
     * @return 201 Created with the saved {@link TaskDto}
     */
    @PostMapping("/{person_id}")
    public ResponseEntity<TaskDto> addTask(@Valid @RequestBody TaskCreateDto taskCreateDto,
                                           @PathVariable long person_id) {
        TaskDto savedTask = taskService.addTask(taskCreateDto, person_id);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return the corresponding {@link TaskDto}
     *
     */
    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable long id) {
        return taskService.getTaskById(id);
    }

    /**
     * Retrieves all tasks.
     *
     * @return a list of all {@link TaskDto}s
     */
    @GetMapping
    public Page<TaskDto> getAllTask(Pageable pageable) {
        return taskService.getAllTask(pageable);
    }

    /**
     * Retrieves all tasks associated with a specific person.
     *
     * @param personId the ID of the person
     * @return a list of {@link TaskDto}s
     */
    @GetMapping({"/person/{personId}"})
    public List<TaskDto> getTasksByPerson(@PathVariable long personId) {
        return taskService.getTasksByPersonId(personId);
    }



    /**
     * Deletes all tasks.
     *
     * @return 200 OK when deletion is complete
     */
    @DeleteMapping("/all")
    public void deleteAllTask() {
        taskService.deleteAllTask();
    }

    /**
     * Deletes a specific task for a specific person.
     *
     * @param person_id the ID of the person
     * @param id        the ID of the task
     *
     */
    @DeleteMapping("/person/{person_id}/task/{id}")
    public void deleteTaskById(@PathVariable long person_id, @PathVariable long id) {
        taskService.deleteTaskById(person_id, id);
    }

    /**
     * Updates an existing task.
     *
     * @param id      the ID of the task to update
     * @param taskDto the updated task data
     * @return 200 OK with the updated {@link TaskDto}
     *
     */
    @PatchMapping("{id}")
    public ResponseEntity<?> updateTask(@PathVariable long id, @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Filters tasks by a specific due date.
     *
     * @param dueDate the due date to filter by
     * @return a list of {@link TaskDto}s matching the due date
     */
    @GetMapping(value = "/task", params = "dueDate")
    public List<TaskDto> filterByDueDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        return taskService.filterByDueDate(dueDate);
    }

    /**
     * Filters tasks within a range of due dates.
     *
     * @param startDate the start of the date range
     * @param endDate   the end of the date range
     * @return a list of {@link TaskDto}s within the date range
     */
    @GetMapping(params = {"startDate", "endDate"})
    public List<TaskDto> filterByDueDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return taskService.filterByDueDateRange(startDate, endDate);
    }


    /**
     * Filters tasks for a specific person within a date range.
     *
     * @param personId  the ID of the person
     * @param startDate the start of the date range
     * @param endDate   the end of the date range
     * @return a list of {@link TaskDto}s matching the criteria
     */
    @GetMapping(value = "/{personId}", params = {"startDate", "endDate"})
    public List<TaskDto> filterTaskByPerson(@PathVariable long personId,
                                            @RequestParam LocalDateTime startDate,
                                            @RequestParam LocalDateTime endDate) {
        return taskService.findTaskForPersonInBetweenDates(personId, startDate, endDate);
    }


    /**
     * Marks a task as finished.
     *
     * @param id the ID of the task
     * @return the updated {@link TaskDto} with finished status
     */
    @PatchMapping("/{id}/finish")
    public TaskDto finishTask(@PathVariable long id) {
        return taskService.finishTask(id);
    }

    /**
     * Marks a task as not finished.
     *
     * @param id the ID of the task
     * @return the updated {@link TaskDto} with unfinished status
     */
    @PatchMapping("/{id}/unfinish")
    public TaskDto unfinishTask(@PathVariable long id) {
        return taskService.unfinishTask(id);
    }


    /**
     * Filters tasks by category.
     *
     * @param category the task category
     * @return a list of {@link TaskDto}s matching the category
     */
    @GetMapping("/category/{category}")
    public List<TaskDto> filterByCategory(@PathVariable Task.Category category) {
        return taskService.filterByCategory(category);
    }

    /**
     * Filters tasks by priority.
     *
     * @param priority the task priority
     * @return a list of {@link TaskDto}s matching the priority
     */
    @GetMapping("/priority/{priority}")
    public List<TaskDto> filterByPriority(@PathVariable Task.Priority priority) {
        return taskService.filterByPriority(priority);
    }

    /**
     * Sorts tasks by due date.
     *
     * @param order the sort order ("asc" or "desc")
     * @return a list of {@link TaskDto}s sorted by due date
     */
    @GetMapping("/sort/dueDate")
    public List<TaskDto> sortByDueDate(@RequestParam(defaultValue = "asc") String order){
        boolean asc = order.equalsIgnoreCase("asc");
        return taskService.sortByDueDate(asc);
    }

    /**
     * Sorts tasks by priority.
     *
     * @param order the sort order ("asc" or "desc")
     * @return a list of {@link TaskDto}s sorted by priority
     */
    @GetMapping("/sort/priority")
    public List<TaskDto> sortByPriority(@RequestParam(defaultValue = "asc") String order){
        boolean asc = order.equalsIgnoreCase("asc");
        return taskService.sortByPriority(asc);
    }

    /**
     * Sorts tasks by due date and priority.
     *
     * @param dueDateOrder  the due date sort order ("asc" or "desc")
     * @param priorityOrder the priority sort order ("asc" or "desc")
     * @return a list of {@link TaskDto}s sorted by due date and priority
     */
    @GetMapping("/sort/dueDate/priority")
    public List<TaskDto> sortByDueDateAndPriority(@RequestParam(defaultValue = "asc") String dueDateOrder,
                                                  @RequestParam(defaultValue = "asc") String priorityOrder){
       boolean dueAsc = dueDateOrder.equalsIgnoreCase("asc");
       boolean priorityAsc = priorityOrder.equalsIgnoreCase("asc");
       return taskService.sortByDueDateAndPriority(dueAsc, priorityAsc);
    }

    /**
     * Sorts tasks by multiple criteria.
     *
     * @param sort  optional list of sort fields
     * @param order optional list of sort orders corresponding to each field
     * @return a list of {@link TaskDto}s sorted according to the specified criteria
     */
    @GetMapping("/tasks/sort")
    public List<TaskDto> sortTasks(
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) List<String> order) {
        return taskService.getSortedTasks(sort, order);
    }
}
