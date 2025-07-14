package com.example.accounting_employee_time.dto;

import com.example.accounting_employee_time.entity.Position;
import com.example.accounting_employee_time.security.Role;
import lombok.*;

import java.util.List;

/**
 * DTO-сущность сотрудника.
 * Используется для передачи данных о сотруднике между слоями приложения.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {

    /**
     * Уникальный идентификатор сотрудника.
     */
    private Long id;

    /**
     * Имя сотрудника (используется как username для аутентификации).
     */
    private String employeeName;

    /**
     * Должность сотрудника.
     */
    private Position position;

    /**
     * Департамент, к которому принадлежит сотрудник.
     */
    private DepartmentDTO department;

    /**
     * Роль сотрудника (например, ADMIN или USER).
     */
    private Role role;

    /**
     * Идентификатор начальника (может быть null, если сотрудник — директор).
     */
    private Long chiefId;

    /**
     * Список идентификаторов подчинённых сотрудников.
     * Заполняется только при чтении данных.
     */
    private List<Long> subordinateIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getChiefId() {
        return chiefId;
    }

    public void setChiefId(Long chiefId) {
        this.chiefId = chiefId;
    }

    public List<Long> getSubordinateIds() {
        return subordinateIds;
    }

    public void setSubordinateIds(List<Long> subordinateIds) {
        this.subordinateIds = subordinateIds;
    }
}
