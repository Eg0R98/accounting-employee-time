package com.example.accounting_employee_time.service;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Сервисный интерфейс для работы с сотрудниками.
 * Содержит методы для управления сотрудниками и получения информации о текущем сотруднике.
 */
@Component
public interface EmployeeService {

    /**
     * Создание нового сотрудника.
     *
     * @param employeeEntity объект сотрудника
     * @return созданный сотрудник
     */
    EmployeeEntity create(EmployeeEntity employeeEntity);

    /**
     * Получение сотрудника по его имени.
     *
     * @param employeeName имя сотрудника
     * @return сотрудник
     */
    EmployeeEntity getByEmployeeName(String employeeName);

    /**
     * Возвращение реализации {@link UserDetailsService}, основанной на методе получения сотрудника по имени.
     *
     * @return реализация UserDetailsService
     */
    UserDetailsService userDetailsService();

    /**
     * Получение текущего аутентифицированного сотрудника из контекста безопасности.
     *
     * @return текущий сотрудник
     */
    EmployeeEntity getCurrentEmployee();

    /**
     * Получение сотрудника по его идентификатору.
     *
     * @param id идентификатор сотрудника
     * @return сотрудник
     */
    EmployeeEntity findById(Long id);

    /**
     * Получение идентификатора текущего сотрудника.
     *
     * @return id текущего сотрудника
     */
    Long getCurrentEmployeeId();

    /**
     * Проверка, является ли employee непосредственным подчинённым manager.
     *
     * @param manager руководитель
     * @param employee сотрудник
     * @return true, если employee напрямую подчинён manager
     */
    boolean isDirectSubordinate(EmployeeEntity manager, EmployeeEntity employee);

    /**
     * Проверка, является ли manager начальником employee по иерархии (включая цепочку подчинённости).
     *
     * @param manager руководитель
     * @param employee сотрудник
     * @return true, если manager является начальником employee
     */
    boolean isSuperior(EmployeeEntity manager, EmployeeEntity employee);

    /**
     * Получение прокси-ссылки на сотрудника по id для использования в мапперах.
     *
     * @param chiefId идентификатор начальника
     * @return прокси-сущность сотрудника или null, если chiefId равен null
     */
    EmployeeEntity getReferenceById(Long chiefId);

    /**
     * Рекурсивное получение всех подчинённых сотрудника по всей иерархии.
     *
     * @param manager сотрудник-руководитель
     * @return список всех подчинённых (прямых и непрямых)
     */
    List<EmployeeEntity> getAllSubordinatesRecursive(EmployeeEntity manager);

    /**
     * Проверка, является ли employee непосредственным подчинённым potentialChief.
     *
     * @param potentialChief потенциальный начальник
     * @param employee сотрудник
     * @return true, если employee напрямую подчинён potentialChief
     */
    boolean isDirectSubordinateOf(EmployeeEntity potentialChief, EmployeeEntity employee);
}
