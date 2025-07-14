package com.example.accounting_employee_time.controller;


import com.example.accounting_employee_time.service.AuthenticationService;
import com.example.accounting_employee_time.dto.AuthRequest;
import com.example.accounting_employee_time.dto.JwtAuthenticationResponse;
import com.example.accounting_employee_time.dto.RegRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для регистрации, авторизации сотрудника и обновления токена.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    /**
     * Регистрация нового сотрудника
     * @param request содержит нужные данные для регистрации
     * @return JwtAuthenticationResponse, хранящий токен в виде строки
     */
    @PostMapping("/reg")
    public JwtAuthenticationResponse registration(@RequestBody @Valid RegRequest request) {
        return authenticationService.registration(request);
    }

    /**
     * Авторизация сотрудника
     * @param request вмещает необходимые данные для авторизации
     * @return JwtAuthenticationResponse, хранящий токен в виде строки
     */
    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody @Valid AuthRequest request) {
        return authenticationService.authentication(request);
    }

    /**
     * Обновление токена
     * @param authHeader хранить данные из перехваченного заголовка Authorization
     * @return JwtAuthenticationResponse, хранящий токен в виде строки
     */
    @GetMapping("/token/refresh")
    public JwtAuthenticationResponse refreshToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Отсутствует токен");
        }

        String token = authHeader.substring(7);
        return authenticationService.refreshToken(token);
    }
}