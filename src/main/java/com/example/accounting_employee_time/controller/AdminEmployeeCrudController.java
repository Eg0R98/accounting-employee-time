package com.example.accounting_employee_time.controller;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.service.AdminEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
/**
 * Контроллер для выполнения операций CRUD над сотрудниками.
 * Доступ к методам контроллера разрешён только администраторам.
 */
@RestController
@RequestMapping("/employees")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEmployeeCrudController {

    private final AdminEmployeeService serviceAdmin;

    /**
     * Получение списка всех сотрудников.
     *
     * @return HTTP 200 OK и список всех сотрудников
     */
    @GetMapping
    public ResponseEntity<List<EmployeeEntity>> getAll() {
        List<EmployeeEntity> userEntities = serviceAdmin.findAll();
        return ResponseEntity.ok(userEntities);
    }

    /**
     * Получение сотрудника по идентификатору.
     *
     * @param id идентификатор сотрудника
     * @return HTTP 200 OK и найденный сотрудник
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeEntity> getById(@PathVariable Long id) {
        EmployeeEntity employeeEntity = serviceAdmin.findById(id);
        return ResponseEntity.ok(employeeEntity);
    }

    /**
     * Создание нового сотрудника.
     *
     * @param employeeEntity данные нового сотрудника
     * @return HTTP 201 Created и созданный сотрудник
     */
    @PostMapping("/create")
    public ResponseEntity<EmployeeEntity> create(@RequestBody EmployeeEntity employeeEntity) {
        EmployeeEntity createdEmployeeEntity = serviceAdmin.create(employeeEntity);
        URI location = URI.create(String.format("/employees/%d", createdEmployeeEntity.getId()));
        return ResponseEntity.created(location).body(createdEmployeeEntity);
    }

    /**
     * Обновление данных сотрудника по идентификатору.
     *
     * @param id идентификатор сотрудника
     * @param employeeEntity новые данные сотрудника
     * @return HTTP 200 OK и обновлённый сотрудник
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeEntity> update(@PathVariable Long id, @RequestBody EmployeeEntity employeeEntity) {
        EmployeeEntity updatedEmployeeEntity = serviceAdmin.update(employeeEntity, id);
        return ResponseEntity.ok(updatedEmployeeEntity);
    }

    /**
     * Удаление сотрудника по идентификатору.
     *
     * @param id идентификатор сотрудника
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceAdmin.delete(id);
        return ResponseEntity.noContent().build();
    }
}

