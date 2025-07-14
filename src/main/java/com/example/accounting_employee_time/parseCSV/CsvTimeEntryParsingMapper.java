package com.example.accounting_employee_time.parseCSV;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.parseCSV.data.TimeEntryParsingData;
import com.example.accounting_employee_time.service.EmployeeService;
import com.example.accounting_employee_time.dto.EmployeeDTO;
import com.example.accounting_employee_time.dto.TimeEntryDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования между {@link TimeEntryParsingData} (CSV-модель)
 * и {@link TimeEntryDTO} (внутреннее представление).
 * Также выполняет проверку прав на импорт записей.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CsvTimeEntryParsingMapper {

    private final EmployeeService employeeService;

    /**
     * Преобразует CSV-строку в DTO и выполняет проверку доступа.
     * Разрешено только для своих записей или записей подчинённых.
     *
     * @param data данные из CSV
     * @param currentUser текущий сотрудник
     * @return DTO записи времени
     * @throws AccessDeniedException если попытка создать запись за чужого сотрудника
     */
    public TimeEntryDTO toDto(TimeEntryParsingData data, EmployeeEntity currentUser) {
        log.info("CSV: дата={}, сотрудник={}, часы={}",
                data.getWorkDate(), data.getEmployeeId(), data.getHoursWorked());

        EmployeeEntity employee = employeeService.findById(data.getEmployeeId());

        // createdBy — всегда текущий пользователь
        EmployeeEntity createdBy = currentUser;

        if (!currentUser.getId().equals(employee.getId()) &&
                !employeeService.isDirectSubordinateOf(currentUser, employee)) {
            throw new AccessDeniedException("Нет прав на создание записи за " + employee.getEmployeeName());
        }

        return TimeEntryDTO.builder()
                           .workDate(data.getWorkDate())
                           .hoursWorked(data.getHoursWorked())
                           .employee(EmployeeDTO.builder().id(employee.getId()).build())
                           .createdBy(EmployeeDTO.builder().id(createdBy.getId()).build())
                           .build();
    }

    /**
     * Преобразует DTO записи времени в CSV-модель для экспорта.
     *
     * @param dto DTO записи
     * @return CSV-структура
     */
    public TimeEntryParsingData toParsingData(TimeEntryDTO dto) {
        TimeEntryParsingData data = new TimeEntryParsingData();
        data.setWorkDate(dto.getWorkDate());
        data.setHoursWorked(dto.getHoursWorked());
        data.setEmployeeId(dto.getEmployee().getId());
        data.setEmployeeName(employeeService.getReferenceById(dto.getEmployee().getId()).getEmployeeName());
        data.setCreatedById(dto.getCreatedBy().getId());
        data.setCreatedByName(employeeService.getReferenceById(dto.getCreatedBy().getId()).getEmployeeName());
        return data;
    }
}
