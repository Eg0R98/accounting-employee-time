package com.example.accounting_employee_time.service.impl;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.exception.NotEmployeeNameException;
import com.example.accounting_employee_time.repository.EmployeeRepository;
import com.example.accounting_employee_time.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация {@link EmployeeService}, обеспечивающая взаимодействие с репозиторием сотрудников
 * и контекстом безопасности Spring Security.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    /**
     * Создание сотрудника с проверкой уникальности имени.
     *
     * @param employeeEntity объект сотрудника
     * @return сохранённый сотрудник
     * @throws RuntimeException если имя сотрудника уже занято
     */
    public EmployeeEntity create(EmployeeEntity employeeEntity) {
        if (repository.existsByEmployeeName(employeeEntity.getUsername())) {
            throw new NotEmployeeNameException("Сотрудник с таким именем уже существует");
        }
        return repository.save(employeeEntity);
    }

    /**
     * Получение сотрудника по ID.
     *
     * @param id идентификатор сотрудника
     * @return найденный сотрудник
     * @throws EntityNotFoundException если сотрудник не найден
     */
    public EmployeeEntity findById(Long id) {
        return repository.findById(id)
                         .orElseThrow(() -> new EntityNotFoundException("Сотрудник с id=" + id + " не найден"));
    }

    /**
     * Получение сотрудника по имени.
     *
     * @param employeeName имя сотрудника
     * @return сотрудник
     * @throws UsernameNotFoundException если сотрудник не найден
     */
    public EmployeeEntity getByEmployeeName(String employeeName) {
        return repository.findByEmployeeName(employeeName)
                         .orElseThrow(() -> new UsernameNotFoundException("Сотрудник не найден"));
    }

    /**
     * Предоставляет реализацию {@link UserDetailsService} для Spring Security.
     *
     * @return реализация UserDetailsService
     */
    public UserDetailsService userDetailsService() {
        return this::getByEmployeeName;
    }

    /**
     * Получение текущего аутентифицированного сотрудника из контекста безопасности.
     *
     * @return текущий сотрудник
     * @throws UsernameNotFoundException если сотрудник не найден
     */
    public EmployeeEntity getCurrentEmployee() {
        var employeeName = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmployeeName(employeeName);
    }

    /**
     * Получение идентификатора текущего сотрудника из контекста безопасности.
     *
     * @return id текущего сотрудника
     * @throws IllegalStateException если principal не является экземпляром EmployeeEntity
     */
    public Long getCurrentEmployeeId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof EmployeeEntity employeeEntity) {
            return employeeEntity.getId();
        }
        throw new IllegalStateException("Сотрудник не найден в контексте безопасности");
    }

    /**
     * Проверка, является ли employee непосредственным подчинённым manager.
     *
     * @param manager руководитель
     * @param employee сотрудник
     * @return true, если employee напрямую подчинён manager
     */
    public boolean isDirectSubordinate(EmployeeEntity manager, EmployeeEntity employee) {
        return employee.getChief() != null &&
                employee.getChief().getId().equals(manager.getId());
    }

    /**
     * Проверка, является ли manager начальником employee по иерархии (прямой или цепочкой).
     *
     * @param manager руководитель
     * @param employee сотрудник
     * @return true, если manager является начальником employee
     */
    public boolean isSuperior(EmployeeEntity manager, EmployeeEntity employee) {
        EmployeeEntity current = employee.getChief();
        while (current != null) {
            if (current.getId().equals(manager.getId())) {
                return true;
            }
            current = current.getChief();
        }
        return false;
    }

    /**
     * Получение прокси-объекта сотрудника по id для использования в мапперах.
     *
     * @param id идентификатор сотрудника
     * @return прокси-объект EmployeeEntity или null, если id равен null
     */
    public EmployeeEntity getReferenceById(Long id) {
        if (id == null) return null;
        return repository.getReferenceById(id);
    }

    /**
     * Рекурсивно получает всех подчинённых сотрудника (прямых и косвенных).
     *
     * @param manager руководитель
     * @return список всех подчинённых
     */
    public List<EmployeeEntity> getAllSubordinatesRecursive(EmployeeEntity manager) {
        List<EmployeeEntity> result = new ArrayList<>();
        collectSubordinates(manager, result);
        return result;
    }

    private void collectSubordinates(EmployeeEntity manager, List<EmployeeEntity> result) {
        for (EmployeeEntity subordinate : manager.getSubordinates()) {
            result.add(subordinate);
            collectSubordinates(subordinate, result); // рекурсивный обход
        }
    }

    /**
     * Проверка, является ли employee непосредственным подчинённым potentialChief.
     *
     * @param potentialChief потенциальный начальник
     * @param employee сотрудник
     * @return true, если employee напрямую подчинён potentialChief
     */
    public boolean isDirectSubordinateOf(EmployeeEntity potentialChief, EmployeeEntity employee) {
        return employee.getChief() != null &&
                employee.getChief().getId().equals(potentialChief.getId());
    }
}

