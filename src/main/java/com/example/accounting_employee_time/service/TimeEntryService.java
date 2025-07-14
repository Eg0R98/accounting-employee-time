package com.example.accounting_employee_time.service;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.dto.TimeEntryDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервисный интерфейс для управления записями рабочего времени сотрудников.
 * Определяет основные операции создания, обновления, удаления и получения записей времени.
 */
@Component
public interface TimeEntryService {

    /**
     * Создаёт новую запись рабочего времени.
     * Проверяет, что текущий пользователь имеет права на создание записи для указанного сотрудника.
     *
     * @param dto DTO с данными записи
     * @param employeeName имя пользователя, создающего запись
     */
    void create(TimeEntryDTO dto, String employeeName);

    /**
     * Обновляет существующую запись рабочего времени по её ID.
     * Проверяет права текущего пользователя на обновление.
     *
     * @param id ID записи для обновления
     * @param dto DTO с новыми данными записи
     * @param employeeName имя пользователя, выполняющего обновление
     */
    void update(Long id, TimeEntryDTO dto, String employeeName);

    /**
     * Удаляет запись рабочего времени по ID.
     * Проверяет права текущего пользователя на удаление.
     *
     * @param id ID удаляемой записи
     * @param employeeName имя пользователя, выполняющего удаление
     */
    void delete(Long id, String employeeName);

    /**
     * Получает запись рабочего времени по ID.
     * Проверяет права текущего пользователя на просмотр.
     *
     * @param id ID записи
     * @param employeeName имя пользователя, запрашивающего данные
     * @return DTO записи рабочего времени
     */
    TimeEntryDTO getById(Long id, String employeeName);

    /**
     * Получает все записи рабочего времени для указанного сотрудника.
     * Проверяет права текущего пользователя на просмотр данных этого сотрудника.
     *
     * @param employeeId ID сотрудника, записи которого запрашиваются
     * @param employeeName имя пользователя, запрашивающего данные
     * @return список DTO записей рабочего времени
     */
    List<TimeEntryDTO> getAllByEmployee(Long employeeId, String employeeName);

    /**
     * Получает все доступные для текущего пользователя записи времени
     * по списку сотрудников и диапазону дат.
     * Если список сотрудников не указан, возвращает записи текущего пользователя и всех его подчинённых.
     *
     * @param actor текущий пользователь
     * @param employeeIds список ID сотрудников (может быть пустым или null)
     * @param startDate начальная дата фильтра (включительно)
     * @param endDate конечная дата фильтра (включительно)
     * @return список DTO записей рабочего времени, доступных пользователю
     */
    List<TimeEntryDTO> getAllAccessible(EmployeeEntity actor,
                                        List<Long> employeeIds,
                                        LocalDate startDate,
                                        LocalDate endDate);
}
