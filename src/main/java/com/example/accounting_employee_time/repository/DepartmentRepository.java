package com.example.accounting_employee_time.repository;

import com.example.accounting_employee_time.entity.DepartmentEntity;
import com.example.accounting_employee_time.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link DepartmentEntity}.
 * Расширяет {@link JpaRepository} и предоставляет метод поиска по имени.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

    /**
     * Поиск департамента по названию.
     *
     * @param name название департамента
     * @return департамент в обёртке {@link Optional}
     */
    Optional<DepartmentEntity> findByName(String name);
}
