package com.example.accounting_employee_time.mapper;

import com.example.accounting_employee_time.entity.DepartmentEntity;
import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.dto.DepartmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования между {@link DepartmentEntity} и {@link DepartmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    /**
     * Преобразует сущность департамента в DTO.
     *
     * @param entity сущность {@link DepartmentEntity}
     * @return DTO {@link DepartmentDTO}
     */
    @Mapping(target = "employeeIds", source = "employees", qualifiedByName = "mapEmployeeIds")
    DepartmentDTO toDTO(DepartmentEntity entity);

    /**
     * Преобразует DTO департамента в сущность.
     *
     * @param dto DTO {@link DepartmentDTO}
     * @return сущность {@link DepartmentEntity}
     */
    DepartmentEntity toEntity(DepartmentDTO dto);

    /**
     * Преобразует список сотрудников в список их идентификаторов.
     *
     * @param employees список сущностей сотрудников
     * @return список их id
     */
    @Named("mapEmployeeIds")
    default List<Long> mapEmployeeIds(List<EmployeeEntity> employees) {
        return employees.stream()
                        .map(EmployeeEntity::getId)
                        .collect(Collectors.toList());
    }
}
