package com.adriangarciao.person_productivity_app.exception;

import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.model.Task;

/**
 * Exception thrown when a {@link Task} is not owned by the specified {@link Person}.
 */
public class TaskOwnershipException extends RuntimeException {

    /**
     * Constructs a new {@code TaskOwnershipException} for the specified person ID and task ID.
     *
     * @param person_id the ID of the person attempting to access the task
     * @param task_id   the ID of the task that is not owned by the person
     */
    public TaskOwnershipException(Long person_id, Long task_id) {

      super("Task with ID " + task_id + " not owned by Person with ID " + person_id);
    }
}
