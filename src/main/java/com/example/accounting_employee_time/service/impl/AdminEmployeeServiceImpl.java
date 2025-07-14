package com.example.accounting_employee_time.service.impl;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.repository.EmployeeRepository;
import com.example.accounting_employee_time.service.AdminEmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса {@link AdminEmployeeService} для управления сотрудниками администратором.
 * Использует {@link EmployeeRepository} для взаимодействия с базой данных.
 */
@Service
@RequiredArgsConstructor
public class AdminEmployeeServiceImpl implements AdminEmployeeService {

    private final EmployeeRepository repository;

    /**
     * Получение списка всех сотрудников из базы данных.
     *
     * @return список всех сотрудников
     */
    @Override
    public List<EmployeeEntity> findAll() {
        return repository.findAll();
    }

    /**
     * Поиск сотрудника по id.
     *
     * @param id сотрудника
     * @return найденный сотрудник
     * @throws EntityNotFoundException если сотрудник не найден
     */
    @Override
    public EmployeeEntity findById(Long id) {
        return repository.findById(id)
                         .orElseThrow(() -> new EntityNotFoundException(String.format("EmployeeEntity with id=%d not found", id)));
    }

    /**
     * Создание нового сотрудника и сохранение его в базе данных.
     *
     * @param employeeEntity объект сотрудника
     * @return сохранённый сотрудник с присвоенным id
     */
    @Override
    public EmployeeEntity create(EmployeeEntity employeeEntity) {
        return repository.save(employeeEntity);
    }

    /**
     * Обновление существующего сотрудника.
     *
     * @param employeeEntity объект с обновлёнными данными
     * @param id сотрудника для обновления
     * @return обновлённый сотрудник
     * @throws EntityNotFoundException если сотрудник не найден
     */
    @Override
    public EmployeeEntity update(EmployeeEntity employeeEntity, Long id) {

        EmployeeEntity employeeEntityFromDB = repository.findById(id)
                                                        .orElseThrow(() -> new EntityNotFoundException(String.format("EmployeeEntity with id=%d not found", id)));

        employeeEntityFromDB.setEmployeeName(employeeEntity.getUsername());
        employeeEntityFromDB.setPassword(employeeEntity.getPassword());
        employeeEntityFromDB.setRole(employeeEntity.getRole());

        return repository.save(employeeEntityFromDB);
    }

    /**
     * Удаление сотрудника по id.
     *
     * @param id сотрудника
     */
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

