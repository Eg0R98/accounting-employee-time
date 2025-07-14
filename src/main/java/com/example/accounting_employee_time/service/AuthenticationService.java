package com.example.accounting_employee_time.service;


import com.example.accounting_employee_time.dto.AuthRequest;
import com.example.accounting_employee_time.dto.JwtAuthenticationResponse;
import com.example.accounting_employee_time.dto.RegRequest;

/**
 * Сервис для работы с аутентификацией и регистрацией сотрудников.
 */
public interface AuthenticationService {

    /**
     * Регистрация нового сотрудника.
     *
     * @param request данные для регистрации
     * @return JWT-ответ с токеном
     */
    JwtAuthenticationResponse registration(RegRequest request);

    /**
     * Аутентификация сотрудника.
     *
     * @param request данные для входа
     * @return JWT-ответ с токеном
     */
    JwtAuthenticationResponse authentication(AuthRequest request);

    /**
     * Обновление JWT-токена.
     *
     * @param token текущий токен
     * @return новый JWT-токен
     */
    JwtAuthenticationResponse refreshToken(String token);
}
