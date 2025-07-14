package com.example.accounting_employee_time.service;

import com.example.accounting_employee_time.parseCSV.FileExtension;
import com.example.accounting_employee_time.parseCSV.data.ParsingData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Сервис для работы с файлами импорта и экспорта.
 * Обеспечивает парсинг и генерацию файлов с учётом заголовков и форматов.
 *
 * @param <P> тип данных, реализующий {@link ParsingData}, с которым работает сервис
 */
public interface FileProcessService {

    /**
     * Парсит файл с заголовком в список объектов заданного типа.
     *
     * @param dataClass класс объекта, в который маппится каждая запись
     * @param multipartFile файл для парсинга
     * @param fileExtension ожидаемое расширение файла (например, CSV)
     * @return список валидных объектов из файла
     */
    <P extends ParsingData> List<P> parseFileWithHeader(Class<P> dataClass,
                                                        MultipartFile multipartFile,
                                                        FileExtension fileExtension);

    /**
     * Парсит файл без заголовка в список объектов заданного типа.
     *
     * @param dataClass класс объекта, в который маппится каждая запись
     * @param multipartFile файл для парсинга
     * @param fileExtension ожидаемое расширение файла (например, CSV)
     * @return список валидных объектов из файла
     */
    <P extends ParsingData> List<P> parseFileWithoutHeader(Class<P> dataClass,
                                                           MultipartFile multipartFile,
                                                           FileExtension fileExtension);

    /**
     * Экспортирует список объектов в файл с заголовком.
     *
     * @param dataClass класс объекта, данные которого экспортируются
     * @param data список объектов для экспорта
     * @return содержимое файла в виде массива байт
     */
    <P extends ParsingData> byte[] exportFileWithHeader(Class<P> dataClass, List<P> data);

    /**
     * Экспортирует список объектов в файл без заголовка.
     *
     * @param dataClass класс объекта, данные которого экспортируются
     * @param data список объектов для экспорта
     * @return содержимое файла в виде массива байт
     */
    <P extends ParsingData> byte[] exportFileWithoutHeader(Class<P> dataClass, List<P> data);
}
