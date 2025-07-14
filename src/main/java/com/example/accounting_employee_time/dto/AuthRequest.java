package com.example.accounting_employee_time.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * ДТО, обладающее необходимыми для авторизации данными
 */
@Data
@RequiredArgsConstructor
public class AuthRequest {

    @Size(min = 5, max = 50, message = "Имя сотрудника должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя сотрудника не может быть пустыми")
    private final String employeeName;

    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private final String password;
}