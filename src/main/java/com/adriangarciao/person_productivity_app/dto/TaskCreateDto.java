package com.adriangarciao.person_productivity_app.dto;
import com.adriangarciao.person_productivity_app.model.Task;
import java.time.LocalDateTime;

//DTO with data client passes in to create Task Entity.
public record TaskCreateDto (String title, String description, LocalDateTime creationTime, LocalDateTime dueDate,
                       LocalDateTime completionDate, boolean finished, Task.Priority priority, Task.Category category){
}