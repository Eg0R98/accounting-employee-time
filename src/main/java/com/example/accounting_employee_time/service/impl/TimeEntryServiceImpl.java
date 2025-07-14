package com.example.accounting_employee_time.service.impl;

import com.example.accounting_employee_time.dto.TimeEntryDTO;
import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.entity.TimeEntryEntity;
import com.example.accounting_employee_time.mapper.TimeEntryMapper;
import com.example.accounting_employee_time.repository.EmployeeRepository;
import com.example.accounting_employee_time.repository.TimeEntryRepository;
import com.example.accounting_employee_time.service.EmployeeService;
import com.example.accounting_employee_time.service.TimeEntryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Реализация {@link TimeEntryService}, обеспечивающая логику управления записями рабочего времени.
 * Включает проверку прав доступа пользователя и работу с репозиториями и мапперами.
 */
@Service
@RequiredArgsConstructor
public class TimeEntryServiceImpl implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final TimeEntryMapper mapper;

    /**
     * Создаёт новую запись времени для сотрудника, если пользователь имеет права.
     * Проверяет, что запись на дату не дублируется.
     *
     * @param dto DTO записи времени
     * @param employeeName имя текущего пользователя, создающего запись
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws EntityNotFoundException если целевой сотрудник не найден
     * @throws AccessDeniedException если у пользователя нет прав на создание записи
     * @throws IllegalStateException если запись за указанную дату уже существует
     */
    public void create(TimeEntryDTO dto, String employeeName) {
        EmployeeEntity actor = employeeRepository.findByEmployeeName(employeeName)
                                                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        EmployeeEntity target = employeeRepository.findById(dto.getEmployee().getId())
                                                  .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if (!actor.getId().equals(target.getId()) &&
                !employeeService.isDirectSubordinate(actor, target)) {
            throw new AccessDeniedException("Нет доступа на создание записи");
        }

        boolean alreadyExists = timeEntryRepository.existsByEmployeeIdAndWorkDate(target.getId(), dto.getWorkDate());
        if (alreadyExists) {
            throw new IllegalStateException("Запись за " + dto.getWorkDate() + " уже существует для сотрудника " + target.getEmployeeName());
        }

        TimeEntryEntity entity = mapper.toEntity(dto);
        entity.setEmployee(target);
        entity.setCreatedBy(actor);
        timeEntryRepository.save(entity);
    }

    /**
     * Обновляет существующую запись времени, если пользователь имеет права на это.
     *
     * @param id ID обновляемой записи
     * @param dto DTO с новыми данными
     * @param employeeName имя текущего пользователя
     * @throws EntityNotFoundException если запись не найдена
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException если нет прав на обновление записи
     */
    public void update(Long id, TimeEntryDTO dto, String employeeName) {
        TimeEntryEntity entity = timeEntryRepository.findById(id)
                                                    .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        EmployeeEntity actor = employeeRepository.findByEmployeeName(employeeName)
                                                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!actor.getId().equals(entity.getEmployee().getId()) &&
                !employeeService.isDirectSubordinate(actor, entity.getEmployee())) {
            throw new AccessDeniedException("Нет доступа на обновление записи");
        }

        entity.setWorkDate(dto.getWorkDate());
        entity.setWorkedMinutes(mapper.toMinutes(dto.getHoursWorked()));
        timeEntryRepository.save(entity);
    }

    /**
     * Удаляет запись времени, если у пользователя есть права на удаление.
     *
     * @param id ID удаляемой записи
     * @param employeeName имя текущего пользователя
     * @throws EntityNotFoundException если запись не найдена
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException если нет прав на удаление записи
     */
    public void delete(Long id, String employeeName) {
        TimeEntryEntity entity = timeEntryRepository.findById(id)
                                                    .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        EmployeeEntity actor = employeeRepository.findByEmployeeName(employeeName)
                                                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!actor.getId().equals(entity.getEmployee().getId()) &&
                !employeeService.isDirectSubordinate(actor, entity.getEmployee())) {
            throw new AccessDeniedException("Нет доступа на удаление записи");
        }

        timeEntryRepository.delete(entity);
    }

    /**
     * Возвращает запись времени по ID, если у пользователя есть права на просмотр.
     *
     * @param id ID записи
     * @param employeeName имя текущего пользователя
     * @return DTO записи времени
     * @throws EntityNotFoundException если запись не найдена
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException если нет прав на просмотр записи
     */
    public TimeEntryDTO getById(Long id, String employeeName) {
        TimeEntryEntity entity = timeEntryRepository.findById(id)
                                                    .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        EmployeeEntity actor = employeeRepository.findByEmployeeName(employeeName)
                                                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!actor.getId().equals(entity.getEmployee().getId()) &&
                !employeeService.isSuperior(actor, entity.getEmployee())) {
            throw new AccessDeniedException("Нет доступа к просмотру");
        }

        return mapper.toDTO(entity);
    }

    /**
     * Получает все записи рабочего времени для указанного сотрудника, если у пользователя есть права на просмотр.
     *
     * @param employeeId ID сотрудника, чьи записи запрашиваются
     * @param employeeName имя текущего пользователя
     * @return список DTO записей рабочего времени
     * @throws EntityNotFoundException если сотрудник не найден
     * @throws UsernameNotFoundException если пользователь не найден
     * @throws AccessDeniedException если нет прав на просмотр
     */
    public List<TimeEntryDTO> getAllByEmployee(Long employeeId, String employeeName) {
        EmployeeEntity target = employeeRepository.findById(employeeId)
                                                  .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        EmployeeEntity actor = employeeRepository.findByEmployeeName(employeeName)
                                                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!actor.getId().equals(employeeId) &&
                !employeeService.isSuperior(actor, target)) {
            throw new AccessDeniedException("Нет доступа к просмотру");
        }

        return timeEntryRepository.findByEmployeeId(employeeId)
                                  .stream()
                                  .map(mapper::toDTO)
                                  .toList();
    }

    /**
     * Получает все записи времени, доступные для просмотра текущему пользователю,
     * с возможностью фильтрации по списку сотрудников и диапазону дат.
     * Если список сотрудников не указан, возвращаются записи текущего пользователя и всех его подчинённых.
     *
     * @param actor текущий пользователь
     * @param employeeIds список ID сотрудников (может быть null или пуст)
     * @param startDate дата начала диапазона (включительно), или null
     * @param endDate дата окончания диапазона (включительно), или null
     * @return список DTO записей времени, доступных пользователю
     */
    public List<TimeEntryDTO> getAllAccessible(EmployeeEntity actor,
                                               List<Long> employeeIds,
                                               LocalDate startDate,
                                               LocalDate endDate) {

        Set<Long> accessibleEmployeeIds = new HashSet<>();
        accessibleEmployeeIds.add(actor.getId());

        if (employeeIds == null || employeeIds.isEmpty()) {
            List<EmployeeEntity> subordinates = employeeService.getAllSubordinatesRecursive(actor);
            accessibleEmployeeIds.addAll(subordinates.stream()
                                                     .map(EmployeeEntity::getId)
                                                     .toList());
        } else {
            for (Long targetId : employeeIds) {
                EmployeeEntity target = employeeService.getReferenceById(targetId);
                if (target != null && (target.getId().equals(actor.getId()) || employeeService.isSuperior(actor, target))) {
                    accessibleEmployeeIds.add(targetId);
                }
            }
        }

        if (accessibleEmployeeIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<TimeEntryEntity> entries = timeEntryRepository
                .findByEmployeeIdInAndWorkDateBetween(
                        new ArrayList<>(accessibleEmployeeIds),
                        startDate != null ? startDate : LocalDate.of(1970, 1, 1),
                        endDate != null ? endDate : LocalDate.now()
                );

        return entries.stream()
                      .map(mapper::toDTO)
                      .toList();
    }
}
