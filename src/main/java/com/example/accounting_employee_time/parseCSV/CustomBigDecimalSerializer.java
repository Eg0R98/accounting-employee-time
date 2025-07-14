package com.example.accounting_employee_time.parseCSV;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Пользовательский сериализатор {@link BigDecimal},
 * позволяющий заменить точку на запятую в дробной части.
 * Используется при сериализации чисел в CSV.
 */
public class CustomBigDecimalSerializer extends JsonSerializer<BigDecimal> {

    private final char decimalSeparator;

    /**
     * Конструктор с указанием разделителя дробной части.
     *
     * @param decimalSeparator символ-разделитель (обычно '.' или ',')
     */
    public CustomBigDecimalSerializer(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String formatted = value.toPlainString();
        if (decimalSeparator == ',') {
            formatted = formatted.replace('.', ',');
        }
        gen.writeString(formatted);
    }
}

