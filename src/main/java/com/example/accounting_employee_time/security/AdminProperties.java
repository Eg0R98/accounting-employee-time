package com.example.accounting_employee_time.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс, хранящий данные админа, который создается на старте приложения
 */
@Component
@ConfigurationProperties(prefix = "app.admin")
@Data
public class AdminProperties {
    private String username;
    private String email;
    private String password;
}
