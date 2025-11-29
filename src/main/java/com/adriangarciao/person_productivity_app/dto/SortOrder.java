package com.adriangarciao.person_productivity_app.dto;

/**
 * Sort order DTO for sorting tasks. Simple immutable record with field and direction ("asc"|"desc").
 */
public record SortOrder(String field, String direction) {}
