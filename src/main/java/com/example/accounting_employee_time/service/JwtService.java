package com.example.accounting_employee_time.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Сервис для работы с JWT-токенами.
 * Предоставляет методы для извлечения информации из токена,
 * генерации новых токенов и проверки их валидности.
 */
public interface JwtService {

    /**
     * Извлекает имя пользователя (subject) из переданного JWT-токена.
     *
     * @param token JWT-токен
     * @return имя пользователя (subject)
     */
    String extractUserName(String token);

    /**
     * Генерирует JWT-токен на основе информации о пользователе.
     * В токен также могут быть добавлены дополнительные сведения (например, роль, id).
     *
     * @param userDetails объект с данными пользователя
     * @return сгенерированный JWT-токен в виде строки
     */
    String generateToken(UserDetails userDetails);

    /**
     * Проверяет валидность токена для указанного пользователя.
     * Валидность подразумевает соответствие имени пользователя в токене и отсутствие истечения срока действия.
     *
     * @param token JWT-токен
     * @param userDetails объект с деталями пользователя
     * @return true, если токен действителен, иначе false
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
