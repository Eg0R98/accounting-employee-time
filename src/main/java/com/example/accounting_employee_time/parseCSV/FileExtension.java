package com.example.accounting_employee_time.parseCSV;

/**
 * Поддерживаемые расширения файлов для импорта/экспорта.
 * В данный момент поддерживается только CSV.
 */
public enum FileExtension {
    CSV;

    /**
     * Преобразует строку в {@link FileExtension}.
     *
     * @param ext расширение (например, "csv")
     * @return перечисление FileExtension
     * @throws IllegalArgumentException если расширение не поддерживается
     */
    public static FileExtension fromString(String ext) {
        try {
            return FileExtension.valueOf(ext.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неподдерживаемое расширение файла: " + ext);
        }
    }
}

