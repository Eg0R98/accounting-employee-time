package com.example.accounting_employee_time.service.impl;

import com.example.accounting_employee_time.exception.FileParsingException;
import com.example.accounting_employee_time.parseCSV.CsvSeparator;
import com.example.accounting_employee_time.parseCSV.CustomBigDecimalSerializer;
import com.example.accounting_employee_time.parseCSV.FileExtension;
import com.example.accounting_employee_time.parseCSV.data.ParsingData;
import com.example.accounting_employee_time.service.FileProcessService;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


/**
 * Реализация сервиса для импорта и экспорта файлов.
 * Использует Jackson CsvMapper для обработки CSV файлов с поддержкой валидации,
 * кастомной сериализации числовых значений и настройки кодировки и разделителей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessServiceImpl implements FileProcessService {

    /**
     * Валидация Java Bean объектов при парсинге.
     */
    private final Validator validator;

    /**
     * Кодировка по умолчанию для чтения и записи файлов (UTF-8).
     */
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    /**
     * Разделитель по умолчанию для CSV файлов (точка с запятой).
     */
    private static final CsvSeparator DEFAULT_SEPARATOR = CsvSeparator.SEMICOLON;

    /**
     * Символ десятичного разделителя для сериализации чисел ('.' или ',').
     */
    private static final char DECIMAL_SEPARATOR = '.';

    /**
     * Парсит файл с учётом наличия заголовка.
     * Проверяет расширение, обрабатывает CSV файл, валидирует объекты и возвращает список.
     *
     * @param dataClass класс DTO для парсинга
     * @param multipartFile загружаемый файл
     * @param fileExtension ожидаемое расширение файла
     * @return список валидных DTO из файла
     * @throws FileParsingException при ошибках чтения или валидации
     */
    @Override
    public <P extends ParsingData> List<P> parseFileWithHeader(Class<P> dataClass,
                                                               MultipartFile multipartFile,
                                                               FileExtension fileExtension) {
        return parseFile(dataClass, multipartFile, fileExtension, true);
    }

    /**
     * Парсит файл без заголовка.
     *
     * @param dataClass класс DTO
     * @param multipartFile файл
     * @param fileExtension расширение
     * @return список DTO
     */
    @Override
    public <P extends ParsingData> List<P> parseFileWithoutHeader(Class<P> dataClass,
                                                                  MultipartFile multipartFile,
                                                                  FileExtension fileExtension) {
        return parseFile(dataClass, multipartFile, fileExtension, false);
    }

    /**
     * Экспортирует список объектов в CSV с заголовком.
     *
     * @param dataClass класс DTO
     * @param data список данных
     * @return CSV в байтах
     */
    @Override
    public <P extends ParsingData> byte[] exportFileWithHeader(Class<P> dataClass,
                                                               List<P> data) {
        return collectDataIntoFile(dataClass, data, true);
    }

    /**
     * Экспортирует список объектов в CSV без заголовка.
     *
     * @param dataClass класс DTO
     * @param data список данных
     * @return CSV в байтах
     */
    @Override
    public <P extends ParsingData> byte[] exportFileWithoutHeader(Class<P> dataClass,
                                                                  List<P> data) {
        return collectDataIntoFile(dataClass, data, false);
    }

    /**
     * Универсальный метод для парсинга файла.
     * Проверяет расширение и делегирует обработку конкретному парсеру.
     *
     * @param dataClass класс DTO
     * @param multipartFile файл
     * @param fileExtension расширение
     * @param withHeader флаг наличия заголовка
     * @return список DTO
     */
    private <P extends ParsingData> List<P> parseFile(Class<P> dataClass,
                                                      MultipartFile multipartFile,
                                                      FileExtension fileExtension,
                                                      boolean withHeader) {

        validateFileExtension(fileExtension, multipartFile);

        return switch (fileExtension) {
            case CSV -> parseCsvFile(dataClass, multipartFile, withHeader);
            default -> throw new FileParsingException("Неподдерживаемое расширение: " + fileExtension);
        };
    }

    /**
     * Парсит CSV файл в список объектов.
     * Использует Jackson CsvMapper с настройкой схемы и валидацией.
     *
     * @param dataClass класс DTO
     * @param multipartFile файл CSV
     * @param withHeader наличие заголовка
     * @return список валидных DTO
     */
    private <P extends ParsingData> List<P> parseCsvFile(Class<P> dataClass,
                                                         MultipartFile multipartFile,
                                                         boolean withHeader) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(multipartFile.getInputStream(), DEFAULT_ENCODING)) {
            CsvMapper csvMapper = new CsvMapper();
            csvMapper.registerModule(new JavaTimeModule());
            csvMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            CsvSchema schema = csvMapper.schemaFor(dataClass)
                                        .withUseHeader(withHeader)
                                        .withColumnSeparator(DEFAULT_SEPARATOR.getSeparator());

            MappingIterator<P> iterator = csvMapper.readerFor(dataClass)
                                                   .with(schema)
                                                   .readValues(inputStreamReader);

            List<P> parsedData = iterator.readAll();

            // Фильтрация по валидации bean-валидатора
            return parsedData.stream()
                             .filter(data -> validator.validate(data).isEmpty())
                             .toList();

        } catch (IOException e) {
            throw new FileParsingException("Ошибка при парсинге файла: " + e.getMessage(), e);
        }
    }

    /**
     * Формирует CSV-файл из списка DTO.
     * Поддерживает опцию вывода заголовка и настройку разделителей.
     *
     * @param dataClass класс DTO
     * @param data список данных для экспорта
     * @param withHeader выводить заголовок или нет
     * @return CSV-файл в виде массива байт
     */
    private <P extends ParsingData> byte[] collectDataIntoFile(Class<P> dataClass,
                                                               List<P> data,
                                                               boolean withHeader) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CsvMapper csvMapper = CsvMapper.csvBuilder().build();
            csvMapper.registerModule(new JavaTimeModule());
            csvMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            SimpleModule module = new SimpleModule();
            module.addSerializer(BigDecimal.class, new CustomBigDecimalSerializer(DECIMAL_SEPARATOR));
            csvMapper.registerModule(module);

            CsvSchema schema = csvMapper.schemaFor(dataClass)
                                        .withUseHeader(withHeader)
                                        .withColumnSeparator(DEFAULT_SEPARATOR.getSeparator());

            ObjectWriter writer = csvMapper.writer(schema);

            writer.writeValue(outputStream, data);
            return outputStream.toString(DEFAULT_ENCODING).getBytes(DEFAULT_ENCODING);

        } catch (IOException e) {
            throw new FileParsingException("Ошибка при формировании CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Проверяет расширение файла по имени и сравнивает с ожидаемым.
     *
     * @param expected ожидаемое расширение
     * @param file загружаемый файл
     * @throws FileParsingException если расширение отсутствует или не совпадает
     */
    private void validateFileExtension(FileExtension expected, MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            throw new FileParsingException("Не удалось определить расширение файла");
        }

        String actualExt = name.substring(name.lastIndexOf('.') + 1);
        FileExtension actual = FileExtension.fromString(actualExt);

        if (!expected.equals(actual)) {
            throw new FileParsingException("Ожидался файл с расширением " + expected + ", но получен " + actual);
        }
    }
}
