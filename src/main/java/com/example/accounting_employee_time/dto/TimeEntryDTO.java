package com.example.accounting_employee_time.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeEntryDTO {

    private Long id;

    /**
     * Дата, за которую указывается время
     */
    private LocalDate workDate;

    /**
     * Количество отработанного времени в часах
     */
    private BigDecimal hoursWorked;

    /**
     * Сотрудник, для которого записано время
     */
    private EmployeeDTO employee;

    /**
     * Кто внёс запись: сам сотрудник или его руководитель
     */
    private EmployeeDTO createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(BigDecimal hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public EmployeeDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(EmployeeDTO createdBy) {
        this.createdBy = createdBy;
    }
}
