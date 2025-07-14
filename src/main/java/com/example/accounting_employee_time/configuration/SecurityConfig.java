package com.example.accounting_employee_time.configuration;

import com.example.accounting_employee_time.security.JwtAuthenticationFilter;
import com.example.accounting_employee_time.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 *Настройка бинов для управления безопасностью
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final EmployeeService employeeService;

    /**
     *Создание фильтра безопасности
     * @param  http определяет доступ к URL, роли
     * @return securityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AccessDeniedHandler accessDeniedHandler,
                                                   AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            // Настройка доступа к конечным точкам
            .authorizeHttpRequests(request -> request
                    // Можно указать конкретный путь, * - 1 уровень вложенности, ** - любое количество уровней вложенности
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/entry").hasAnyRole("ADMIN", "USER")
                    .requestMatchers("/employees").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint)).build();
    }

    /**
     * Кодирование пароля
     * @return BCryptPasswordEncoder, реализация PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Класс проверяет логин, пароль, токен
     * @return DaoAuthenticationProvider, хранящий всю необходимую информацию для доступа к ресурсам
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(employeeService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Класс управляет всеми AuthenticationProvider, ищет подходящего
     * @param  config контролирует все зарегистрированные AuthenticationProvider
     * @return  AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}