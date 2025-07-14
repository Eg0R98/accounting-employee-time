package com.example.accounting_employee_time.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO-сущность департамента.
 * Используется для передачи информации о департаменте.
 */
@Data
public class DepartmentDTO {

    /**
     * Уникальный идентификатор департамента.
     */
    private Long id;

    /**
     * Название департамента.
     */
    private String name;

    /**
     * Список идентификаторов сотрудников, входящих в департамент.
     */
    private List<Long> employeeIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }
}