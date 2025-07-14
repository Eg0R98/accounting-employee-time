package com.example.accounting_employee_time.mapper;

import com.example.accounting_employee_time.entity.TimeEntryEntity;
import com.example.accounting_employee_time.dto.TimeEntryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Маппер для преобразования между {@link TimeEntryEntity} и {@link TimeEntryDTO}.
 * Также выполняет пересчёт часов в минуты и обратно.
 */
@Mapper(componentModel = "spring", uses = EmployeeMapper.class)
public interface TimeEntryMapper {

    /**
     * Преобразует DTO записи времени в сущность.
     * Переводит часы в минуты.
     *
     * @param dto DTO {@link TimeEntryDTO}
     * @return сущность {@link TimeEntryEntity}
     */
    @Mapping(target = "workedMinutes", expression = "java(toMinutes(dto.getHoursWorked()))")
    TimeEntryEntity toEntity(TimeEntryDTO dto);

    /**
     * Преобразует сущность записи времени в DTO.
     * Переводит минуты в часы.
     *
     * @param entity сущность {@link TimeEntryEntity}
     * @return DTO {@link TimeEntryDTO}
     */
    @Mapping(target = "hoursWorked", expression = "java(toHours(entity.getWorkedMinutes()))")
    TimeEntryDTO toDTO(TimeEntryEntity entity);

    /**
     * Перевод часов в минуты.
     *
     * @param hours количество часов
     * @return округлённое до минут значение
     */
    default Integer toMinutes(BigDecimal hours) {
        if (hours == null) return null;
        return hours.multiply(BigDecimal.valueOf(60))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
    }

    /**
     * Перевод минут в часы (2 знака после запятой).
     *
     * @param minutes количество минут
     * @return значение в часах
     */
    default BigDecimal toHours(Integer minutes) {
        if (minutes == null) return null;
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
