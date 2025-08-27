package com.adriangarciao.person_productivity_app.dto;

import com.adriangarciao.person_productivity_app.model.Person;
import com.adriangarciao.person_productivity_app.model.Task;
import java.time.LocalDateTime;

//DTO for Task Entity.
public record TaskDto (String title, String description, LocalDateTime creationTime, LocalDateTime dueDate,
                       LocalDateTime completionDate, boolean finished, Task.Priority priority, Task.Category category,
                       Person person){
}
