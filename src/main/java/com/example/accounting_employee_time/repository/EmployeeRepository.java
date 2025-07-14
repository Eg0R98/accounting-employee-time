package com.example.accounting_employee_time.repository;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link EmployeeEntity}.
 * Расширяет {@link JpaRepository} и содержит методы поиска и проверки существования сотрудников.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    /**
     * Поиск сотрудника по имени.
     *
     * @param employeeName имя сотрудника
     * @return сотрудник в обёртке {@link Optional}
     */
    Optional<EmployeeEntity> findByEmployeeName(String employeeName);

    /**
     * Проверка существования сотрудника по имени.
     *
     * @param employeeName имя сотрудника
     * @return {@code true}, если сотрудник с таким именем существует
     */
    boolean existsByEmployeeName(String employeeName);

    /**
     * Удаление сотрудника по id.
     *
     * @param id идентификатор сотрудника
     */
    void deleteById(@NonNull Long id);

    /**
     * Поиск сотрудника по id.
     *
     * @param id идентификатор сотрудника
     * @return сотрудник в обёртке {@link Optional}
     */
    @NonNull
    Optional<EmployeeEntity> findById(@NonNull Long id);
}
