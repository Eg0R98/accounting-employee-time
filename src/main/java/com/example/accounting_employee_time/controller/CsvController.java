package com.example.accounting_employee_time.controller;

import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.parseCSV.FileExtension;
import com.example.accounting_employee_time.parseCSV.CsvTimeEntryParsingMapper;
import com.example.accounting_employee_time.parseCSV.data.TimeEntryParsingData;
import com.example.accounting_employee_time.service.EmployeeService;
import com.example.accounting_employee_time.service.FileProcessService;
import com.example.accounting_employee_time.service.TimeEntryService;
import com.example.accounting_employee_time.dto.TimeEntryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для импорта и экспорта записей учёта времени в формате CSV.
 * Доступ к методам контроллера имеют пользователи с ролями ADMIN или USER.
 */
@RestController
@RequestMapping("/csv")
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@RequiredArgsConstructor
@Slf4j
public class CsvController {

    private final TimeEntryService timeEntryService;
    private final EmployeeService employeeService;
    private final FileProcessService fileProcessService;
    private final CsvTimeEntryParsingMapper parsingMapper;

    /**
     * Импорт записей времени из CSV-файла.
     * Каждая строка проверяется по бизнес-правилам: можно импортировать только свои записи.
     *
     * @param file CSV-файл, содержащий записи времени
     * @return HTTP 200 OK при успешном импорте, или HTTP 403 Forbidden, если попытка импортировать чужие записи
     */
    @PostMapping("/import")
    public ResponseEntity<String> importTimeEntries(@RequestPart MultipartFile file) {
        log.debug("Importing time entries started");
        EmployeeEntity currentUser = employeeService.getCurrentEmployee();

        List<TimeEntryParsingData> parsedList = fileProcessService.parseFileWithHeader(
                TimeEntryParsingData.class,
                file,
                FileExtension.CSV
        );

        List<TimeEntryDTO> dtoList = new ArrayList<>();

        try {
            for (TimeEntryParsingData data : parsedList) {
                TimeEntryDTO dto = parsingMapper.toDto(data, currentUser); // может выбросить AccessDeniedException
                log.info("Импортируем: сотрудник={}, дата={}, часы={}", dto.getEmployee().getId(), dto.getWorkDate(), dto.getHoursWorked());
                dtoList.add(dto);
            }
        } catch (AccessDeniedException ex) {
            log.warn("Попытка импортировать чужую запись: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }

        dtoList.forEach(dto -> timeEntryService.create(dto, currentUser.getEmployeeName()));
        return ResponseEntity.ok("Импорт успешно завершён: загружено " + dtoList.size() + " записей");
    }

    /**
     * Экспорт записей времени в CSV-файл.
     * Пользователь получает доступ только к тем записям, к которым у него есть права.
     *
     * @param employeeIds список ID сотрудников для фильтрации (необязательно)
     * @param startDate дата начала периода (необязательно)
     * @param endDate дата окончания периода (необязательно)
     * @return HTTP 200 OK и CSV-файл со строками, или пустой файл, если данных нет
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTimeEntries(
            @RequestParam(required = false) List<Long> employeeIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.debug("Exporting time entries started for employeeIds={}, startDate={}, endDate={}", employeeIds, startDate, endDate);
        EmployeeEntity currentUser = employeeService.getCurrentEmployee();

        // Получение доступных пользователю записей
        List<TimeEntryDTO> timeEntries = timeEntryService.getAllAccessible(currentUser, employeeIds, startDate, endDate);

        if (timeEntries.isEmpty()) {
            log.debug("No time entries found for export");
            return ResponseEntity.ok()
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=time-entries.csv")
                                 .contentType(MediaType.parseMediaType("text/csv"))
                                 .body(new byte[0]);
        }

        // Преобразование в промежуточную структуру
        List<TimeEntryParsingData> csvData = timeEntries.stream()
                                                        .map(parsingMapper::toParsingData)
                                                        .toList();

        // Генерация CSV-файла
        byte[] fileBytes = fileProcessService.exportFileWithHeader(TimeEntryParsingData.class, csvData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "time-entries.csv");

        log.debug("Exporting time entries completed, {} entries exported", timeEntries.size());
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}

