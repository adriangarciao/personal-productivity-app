package com.adriangarciao.person_productivity_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Represents a task in the system.
 *
 * <p>A task belongs to a {@link Person} and contains details such as a title,
 * description, due date, completion date, priority, and category.
 * It also tracks whether the task has been finished.</p>
 */
@Entity
@Table(name = "task")
public class Task {

    /**
     * Primary key for the task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Short title for the task (required, max 100 characters).
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot be longer than 100 characters")
    private String title;

    /**
     * Longer description for the task (max 500 characters).
     */
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    /**
     * Timestamp of when the task was created.
     * Automatically generated when persisted.
     */
    @CreationTimestamp
    private LocalDateTime creationDate;

    /**
     * The deadline by which the task should be completed.
     * Must be in the present or future.
     */
    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDateTime dueDate;

    /**
     * Timestamp when the task was completed.
     * May be null if the task is unfinished.
     */
    private LocalDateTime completionDate;

    /**
     * Indicates whether the task is finished.
     */
    private boolean finished;

    /**
     * The task's priority (required).
     */
    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    /**
     * The category the task belongs to (required).
     */
    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private Category category;


    /**
     * The person who owns this task.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public Task(){}


    /**
     * Creates a new {@code Task}.
     *
     * @param title short task title
     * @param description longer description
     * @param dueDate due date
     * @param completionDate date the task was completed (nullable)
     * @param finished whether the task is finished
     * @param priority task priority
     * @param category task category
     */
    public Task(String title, String description, LocalDateTime dueDate,
                LocalDateTime completionDate, boolean finished, Priority priority, Category category) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completionDate = completionDate;
        this.finished = finished;
        this.priority = priority;
        this.category = category;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public long getId(){
        return id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }


    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
        if(finished && completionDate == null){
            setCompletionDate(LocalDateTime.now());
        }
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    //ONLY FOR TESTING
    public void setCreationDate(LocalDateTime now) {
        this.creationDate = now;
    }

    //ONLY FOR TESTING
    public void setId(long taskId) {
        this.id = taskId;
    }

    /**
     * Represents the priority level of a task.
     */
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    /**
     * Represents the category of a task.
     */
    public enum Category {
        WORK, PERSONAL, STUDY, OTHER
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", dueDate=" + dueDate +
                ", completionDate=" + completionDate +
                ", finished=" + finished +
                ", priority=" + priority +
                ", category=" + category +
                ", person=" + person +
                '}';
    }
}
