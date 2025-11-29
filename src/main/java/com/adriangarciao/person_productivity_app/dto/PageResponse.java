package com.adriangarciao.person_productivity_app.dto;

import java.util.List;

/**
 * Stable wrapper for paginated responses to avoid serializing Spring's PageImpl directly.
 *
 * @param <T> the content type
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}
