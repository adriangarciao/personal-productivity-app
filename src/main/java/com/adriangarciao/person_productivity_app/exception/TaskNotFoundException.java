package com.adriangarciao.person_productivity_app.exception;
import com.adriangarciao.person_productivity_app.model.Task;

/**
 * Exception thrown when a requested {@link Task} cannot be found in the database.
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code TaskNotFoundException} for the specified task ID.
     *
     * @param id the ID of the task that was not found
     */
    public TaskNotFoundException(Long id) {
      super("Task with ID " + id + " not found.");
    }
}
