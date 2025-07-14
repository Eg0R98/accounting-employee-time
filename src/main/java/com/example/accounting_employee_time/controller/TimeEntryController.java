package com.example.accounting_employee_time.controller;

import com.example.accounting_employee_time.service.TimeEntryService;
import com.example.accounting_employee_time.dto.TimeEntryDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Контроллер для выполнения операций CRUD над записями учёта рабочего времени.
 * Доступ к методам контроллера имеют сотрудники с ролями ADMIN или USER.
 */
@RestController
@RequestMapping("/entry")
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    /**
     * Получение всех записей времени указанного сотрудника.
     * Доступ разрешён, если текущий сотрудник — это указанный сотрудник или его начальник.
     *
     * @param employeeId идентификатор сотрудника
     * @param principal текущий аутентифицированный сотрудник
     * @return HTTP 200 OK и список записей времени сотрудника
     */
    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<List<TimeEntryDTO>> getAllByEmployee(@PathVariable Long employeeId, Principal principal) {
        return ResponseEntity.ok(timeEntryService.getAllByEmployee(employeeId, principal.getName()));
    }

    /**
     * Получение записи времени по идентификатору.
     * Доступ разрешён сотруднику, к которому относится запись, либо его начальнику.
     *
     * @param id идентификатор записи
     * @param principal текущий аутентифицированный сотрудник
     * @return HTTP 200 OK и найденная запись
     */
    @GetMapping("/{id}")
    public ResponseEntity<TimeEntryDTO> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(timeEntryService.getById(id, principal.getName()));
    }

    /**
     * Создание новой записи времени.
     * Сотрудник может создать запись только для себя.
     *
     * @param dto данные новой записи
     * @param principal текущий аутентифицированный сотрудник
     * @return HTTP 200 OK при успешном создании
     */
    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid TimeEntryDTO dto, Principal principal) {
        timeEntryService.create(dto, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Обновление существующей записи времени по идентификатору.
     * Разрешено сотруднику, к которому относится запись, либо его начальнику.
     *
     * @param id идентификатор записи
     * @param dto обновлённые данные
     * @param principal текущий аутентифицированный сотрудник
     * @return HTTP 200 OK при успешном обновлении
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid TimeEntryDTO dto, Principal principal) {
        timeEntryService.update(id, dto, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление записи времени по идентификатору.
     * Разрешено сотруднику, к которому относится запись, либо его начальнику.
     *
     * @param id идентификатор записи
     * @param principal текущий аутентифицированный сотрудник
     * @return HTTP 200 OK при успешном удалении
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        timeEntryService.delete(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}

