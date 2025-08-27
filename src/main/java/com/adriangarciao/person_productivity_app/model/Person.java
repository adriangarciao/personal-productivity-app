package com.adriangarciao.person_productivity_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Represents a person in the system.
 *
 * <p>Each person has a unique {@code id}, an {@code email}, and a {@code name}.
 * A person may also own multiple tasks, managed via the {@link Task} entity.</p>
 */
@Entity
@Table(name = "people")
public class Person {

    /**
     * Primary key for the person.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The person's email.
     * Must be unique and valid.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The person's full name.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * The list of tasks associated with this person.
     */
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    public Person(){}

    /**
     * Creates a new {@code Person} with the given email and name.
     *
     * @param email the email address
     * @param name the full name
     */
    public Person(String email, String name){
        this.email = email;
        this.name = name;
    }

    public long getId() {
        return id;
    }


    //ONLY FOR TESTING
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
