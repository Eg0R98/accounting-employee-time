package com.example.accounting_employee_time.repository;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.entity.TimeEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link TimeEntryEntity}.
 * Расширяет {@link JpaRepository} и предоставляет методы для фильтрации записей времени.
 */
@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntryEntity, Long> {

    /**
     * Получение всех записей времени по id сотрудника.
     *
     * @param id идентификатор сотрудника
     * @return список записей времени
     */
    List<TimeEntryEntity> findByEmployeeId(Long id);

    /**
     * Получение записей времени по нескольким сотрудникам и диапазону дат.
     *
     * @param employeeIds список идентификаторов сотрудников
     * @param start начальная дата (включительно)
     * @param end конечная дата (включительно)
     * @return список записей времени
     */
    List<TimeEntryEntity> findByEmployeeIdInAndWorkDateBetween(List<Long> employeeIds, LocalDate start, LocalDate end);

    /**
     * Проверка существования записи времени по сотруднику и дате.
     *
     * @param id идентификатор сотрудника
     * @param workDate дата работы
     * @return {@code true}, если запись существует
     */
    boolean existsByEmployeeIdAndWorkDate(Long id, LocalDate workDate);
}

