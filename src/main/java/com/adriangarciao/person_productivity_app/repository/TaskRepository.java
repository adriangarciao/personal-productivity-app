package com.adriangarciao.person_productivity_app.repository;

import com.adriangarciao.person_productivity_app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link Task} entities.
 * Provides methods to query tasks by different fields such as person, due date, category, and priority.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks assigned to a specific person.
     *
     * @param personId the ID of the person
     * @return a list of tasks for the given person
     */
    List<Task> findByPersonId(long personId);

    /**
     * Find all tasks with an exact due date.
     *
     * @param dueDate the due date
     * @return a list of tasks due on the given date
     */
    List<Task> findByDueDate(LocalDateTime dueDate);

    /**
     * Find all tasks with due dates between two dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of tasks with due dates in the given range
     */
// Get tasks between two dates
    List<Task> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all tasks for a specific person with due dates between two dates.
     *
     * @param personId  the ID of the person
     * @param startDate the start date
     * @param endDate   the end date
     * @return a list of tasks matching the criteria
     */
    @Query("SELECT t FROM Task t WHERE t.person.id = :personId AND t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksForPersonBetweenDates(
            @Param("personId") Long personId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find all tasks by category.
     *
     * @param category the task category
     * @return a list of tasks with the given category
     */
    List<Task> findByCategory(Task.Category category);

    /**
     * Find all tasks by priority.
     *
     * @param priority the task priority
     * @return a list of tasks with the given priority
     */
    List<Task> findByPriority(Task.Priority priority);
}
