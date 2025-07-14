package com.example.accounting_employee_time.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Сущность "Запись времени".
 * Отражает учёт рабочего времени сотрудника за определённую дату.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "time_entries")
public class TimeEntryEntity {

    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "time_entry_seq")
    @SequenceGenerator(name = "time_entry_seq", sequenceName = "time_entry_seq", allocationSize = 1)
    private Long id;

    /**
     * Дата, за которую указывается рабочее время.
     */
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    /**
     * Отработанное время в минутах (например: 4 ч 35 мин = 275).
     */
    @Column(name = "worked_minutes", nullable = false)
    private Integer workedMinutes;

    /**
     * Сотрудник, для которого создаётся запись времени.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    /**
     * Сотрудник, создавший запись (сам сотрудник или его начальник).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private EmployeeEntity createdBy;

    /**
     * Дата и время создания записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Устанавливает дату создания автоматически перед сохранением.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

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

    public Integer getWorkedMinutes() {
        return workedMinutes;
    }

    public void setWorkedMinutes(Integer workedMinutes) {
        this.workedMinutes = workedMinutes;
    }

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }

    public EmployeeEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(EmployeeEntity createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
