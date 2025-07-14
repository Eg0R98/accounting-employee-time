package com.example.accounting_employee_time.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Департамент".
 * Содержит информацию о названии и сотрудниках, входящих в департамент.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departments")
public class DepartmentEntity {

    /**
     * Уникальный идентификатор департамента.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_seq")
    @SequenceGenerator(name = "department_seq", sequenceName = "department_seq", allocationSize = 1)
    private Long id;

    /**
     * Название департамента.
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Список сотрудников, входящих в департамент.
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<EmployeeEntity> employees = new ArrayList<>();

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

    public List<EmployeeEntity> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeEntity> employees) {
        this.employees = employees;
    }
}
