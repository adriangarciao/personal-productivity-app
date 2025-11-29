package com.adriangarciao.person_productivity_app.mapper;

import com.adriangarciao.person_productivity_app.dto.TaskCreateDto;
import com.adriangarciao.person_productivity_app.dto.TaskDto;
import com.adriangarciao.person_productivity_app.model.Task;
import org.mapstruct.Mapper;
import com.adriangarciao.person_productivity_app.mapper.PersonMapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper interface for converting between Task entities and DTOs.
 * Uses MapStruct to automatically generate the mapping implementations.
 */
@Mapper(componentModel = "spring", uses = {PersonMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    /**
     * Converts a Task entity to its corresponding TaskDto.
     *
    @Mapper(componentModel = "spring", uses = PersonMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     * @return the mapped TaskDto
     */
    @Mapping(source = "creationDate", target = "creationTime")
    TaskDto taskToTaskDTO(Task task);

    /**
     * Converts a TaskDto to a Task entity.
     * Ignores the 'person' field during mapping.
     *
     * @param taskDto the TaskDto
     * @return the mapped Task entity
     */
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    Task TaskDtoToTask(TaskDto taskDto);

    /**
     * Updates an existing Task entity with values from a TaskDto.
     * Ignores the 'person' field during update.
     *
     * @param taskDto the TaskDto containing updated values
     * @param task    the Task entity to update
     */
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateTask(TaskDto taskDto, @MappingTarget Task task);

    /**
     * Converts a list of Task entities to a list of TaskDto objects.
     *
     * @param tasks the list of Task entities
     * @return the list of mapped TaskDto objects
     */
    List<TaskDto> tasksToTaskDtos(List<Task> tasks);

    /**
     * Converts a TaskCreateDto to a Task entity.
     * Ignores the 'person' field during mapping.
     *
     * @param taskCreateDto the TaskCreateDto
     * @return the mapped Task entity
     */
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    Task TaskCreateDtoToTask(TaskCreateDto taskCreateDto);

//    Object writeValueAsString(TaskCreateDto sampleTaskCreateDto);
}
