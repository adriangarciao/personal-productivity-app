package com.adriangarciao.person_productivity_app.mapper;

import com.adriangarciao.person_productivity_app.dto.PersonDto;
import com.adriangarciao.person_productivity_app.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper interface for converting between {@link Person} entities and {@link PersonDto} objects.
 *
 * Uses MapStruct to automatically generate mapping implementations.
 * Null values in DTOs are ignored when updating existing entities.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {


    /**
     * Converts a {@link Person} entity to a {@link PersonDto}.
     *
     * @param person the person entity to convert
     * @return the corresponding {@link PersonDto}
     */
    PersonDto personToPersonDto(Person person);

    /**
     * Converts a {@link PersonDto} to a {@link Person} entity.
     *
     * <p>The ID field is ignored when mapping from DTO to entity to allow
     * automatic generation of IDs on save.</p>
     *
     * @param dto the person DTO to convert
     * @return the corresponding {@link Person} entity
     */
    @Mapping(target = "id", ignore = true)
    Person personDtoToPerson(PersonDto dto);

    /**
     * Updates an existing {@link Person} entity with values from a {@link PersonDto}.
     *
     * <p>Null properties in the DTO are ignored, leaving existing entity values unchanged.</p>
     *
     * @param personDto the DTO containing updated data
     * @param person    the existing entity to update
     */
    void updatePerson(PersonDto personDto, @MappingTarget Person person);


    /**
     * Converts a list of {@link Person} entities to a list of {@link PersonDto}s.
     *
     * @param persons the list of person entities
     * @return the list of corresponding {@link PersonDto}s
     */
    List<PersonDto> personsToPersonDtos(List<Person> persons);
}
