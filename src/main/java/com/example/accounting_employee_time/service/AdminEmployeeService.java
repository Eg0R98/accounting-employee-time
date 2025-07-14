package com.example.accounting_employee_time.service;

import com.example.accounting_employee_time.entity.EmployeeEntity;

import java.util.List;

/**
 * Сервис для администрирования сотрудников.
 * Предоставляет базовые CRUD-операции: создание, чтение, обновление и удаление сотрудников.
 */
public interface AdminEmployeeService {

    /**
     * Получение списка всех сотрудников.
     *
     * @return список всех сотрудников в системе
     */
    List<EmployeeEntity> findAll();

    /**
     * Поиск сотрудника по id.
     *
     * @param id сотрудника
     * @return найденный сотрудник
     */
    EmployeeEntity findById(Long id);

    /**
     * Создание нового сотрудника.
     *
     * @param employeeEntity объект сотрудника
     * @return созданный сотрудник с присвоенным id
     */
    EmployeeEntity create(EmployeeEntity employeeEntity);

    /**
     * Обновление существующего сотрудника.
     *
     * @param employeeEntity обновлённые данные сотрудника
     * @param id сотрудника, которого нужно обновить
     * @return обновлённый сотрудник
     */
    EmployeeEntity update(EmployeeEntity employeeEntity, Long id);

    /**
     * Удаление сотрудника по его id.
     *
     * @param id сотрудника
     */
    void delete(Long id);
}
