package com.example.accounting_employee_time.parseCSV;

import lombok.Getter;

/**
 * Перечисление возможных разделителей CSV-файлов.
 */
@Getter
public enum CsvSeparator {
    COMMA(','),
    SEMICOLON(';'),
    TAB('\t');

    private final char separator;

    CsvSeparator(char separator) {
        this.separator = separator;
    }
}

