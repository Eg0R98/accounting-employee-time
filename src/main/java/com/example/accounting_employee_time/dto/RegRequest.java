package com.example.accounting_employee_time.dto;

import com.example.accounting_employee_time.entity.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * ДТО, обладающее необходимыми для регистрации данными
 */
@Data
@RequiredArgsConstructor
public class RegRequest {

    @Size(min = 5, max = 50, message = "Имя сотрудника должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя сотрудника не может быть пустыми")
    private final String employeeName;

    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    private final String password;

    @NotNull(message = "Должность обязательна")
    private Position position;

    private Long chiefId; // может быть null

    @NotNull(message = "Департамент обязателен")
    private Long departmentId;

}
