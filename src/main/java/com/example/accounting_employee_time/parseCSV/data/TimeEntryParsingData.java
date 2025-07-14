package com.example.accounting_employee_time.parseCSV.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Модель строки CSV-файла для импорта/экспорта записей времени.
 * Используется при преобразовании между CSV и внутренними DTO.
 */
@Data
public class TimeEntryParsingData implements ParsingData {

    /**
     * Дата, за которую указывается рабочее время.
     */
    @JsonProperty("Дата")
    private LocalDate workDate;

    /**
     * Отработанное время в часах (может содержать дробную часть).
     */
    @JsonProperty("Часы")
    private BigDecimal hoursWorked;

    /**
     * Идентификатор сотрудника, за которого вносится запись.
     */
    @JsonProperty("ID сотрудника")
    private Long employeeId;

    /**
     * Имя сотрудника, за которого вносится запись.
     */
    @JsonProperty("Имя сотрудника")
    private String employeeName;

    /**
     * Идентификатор создателя записи (для экспорта).
     */
    @JsonProperty("ID создателя")
    private Long createdById;

    /**
     * Имя создателя записи (для экспорта).
     */
    @JsonProperty("Имя создателя")
    private String createdByName;
}

