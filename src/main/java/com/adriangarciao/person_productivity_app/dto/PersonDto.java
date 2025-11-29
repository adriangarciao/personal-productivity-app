package com.adriangarciao.person_productivity_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO for Person Entity with validation annotations.
public record PersonDto(
	Long id,
	@NotBlank(message = "Name must not be blank") @Size(max = 255) String name,
	@NotBlank(message = "Email must not be blank") @Email(message = "Invalid email address") String email
) {}
