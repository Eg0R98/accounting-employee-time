package com.example.accounting_employee_time.mapper;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.service.EmployeeService;
import com.example.accounting_employee_time.dto.EmployeeDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования между {@link EmployeeEntity} и {@link EmployeeDTO}.
 * Учитывает начальника и подчинённых.
 */
@Mapper(componentModel = "spring", uses = DepartmentMapper.class)
public interface EmployeeMapper {

    /**
     * Преобразует сущность сотрудника в DTO.
     * Начальник отображается как его id, подчинённые — как список id.
     *
     * @param entity сущность {@link EmployeeEntity}
     * @return DTO {@link EmployeeDTO}
     */
    @Mapping(target = "chiefId", expression = "java(entity.getChief() != null ? entity.getChief().getId() : null)")
    @Mapping(target = "subordinateIds", source = "subordinates", qualifiedByName = "mapSubordinateIds")
    EmployeeDTO toDTO(EmployeeEntity entity);

    /**
     * Преобразует DTO сотрудника в сущность.
     * Начальник подгружается из сервиса, подчинённые игнорируются.
     *
     * @param dto DTO {@link EmployeeDTO}
     * @param employeeService сервис для загрузки начальника
     * @return сущность {@link EmployeeEntity}
     */
    @Mapping(target = "chief", expression = "java(mapToChief(dto.getChiefId(), employeeService))")
    @Mapping(target = "subordinates", ignore = true)
    EmployeeEntity toEntity(EmployeeDTO dto, @Context EmployeeService employeeService);

    /**
     * Получает начальника по id через сервис.
     *
     * @param chiefId идентификатор начальника
     * @param employeeService сервис сотрудников
     * @return сущность начальника
     */
    default EmployeeEntity mapToChief(Long chiefId, @Context EmployeeService employeeService) {
        return employeeService.getReferenceById(chiefId);
    }

    /**
     * Преобразует список подчинённых в список их id.
     *
     * @param subordinates список подчинённых
     * @return список id
     */
    @Named("mapSubordinateIds")
    default List<Long> mapSubordinateIds(List<EmployeeEntity> subordinates) {
        return subordinates.stream()
                           .map(EmployeeEntity::getId)
                           .collect(Collectors.toList());
    }
}

