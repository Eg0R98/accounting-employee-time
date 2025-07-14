package com.example.accounting_employee_time.service.impl;


import com.example.accounting_employee_time.entity.DepartmentEntity;
import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.repository.DepartmentRepository;
import com.example.accounting_employee_time.repository.EmployeeRepository;
import com.example.accounting_employee_time.security.Role;
import com.example.accounting_employee_time.service.AuthenticationService;
import com.example.accounting_employee_time.service.EmployeeService;
import com.example.accounting_employee_time.service.JwtService;
import com.example.accounting_employee_time.dto.AuthRequest;
import com.example.accounting_employee_time.dto.JwtAuthenticationResponse;
import com.example.accounting_employee_time.dto.RegRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Реализация {@link AuthenticationService} для регистрации, аутентификации и обновления JWT-токенов сотрудников.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Регистрация нового сотрудника.
     *
     * @param request данные сотрудника для регистрации
     * @return объект с JWT-токеном
     * @throws EntityNotFoundException если указанный департамент или начальник не найдены
     */
    @Override
    public JwtAuthenticationResponse registration(RegRequest request) {

        DepartmentEntity department = departmentRepository.findById(request.getDepartmentId())
                                                          .orElseThrow(() -> new EntityNotFoundException("Департамент не найден"));

        EmployeeEntity chief = null;
        if (request.getChiefId() != null) {
            chief = employeeRepository.findById(request.getChiefId())
                                      .orElseThrow(() -> new EntityNotFoundException("Начальник не найден"));
        }

        var employee = EmployeeEntity.builder()
                                     .employeeName(request.getEmployeeName())
                                     .password(passwordEncoder.encode(request.getPassword()))
                                     .position(request.getPosition())
                                     .department(department)
                                     .chief(chief)
                                     .role(Role.USER)
                                     .build();

        employeeService.create(employee);

        var jwt = jwtService.generateToken(employee);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация сотрудника по имени и паролю.
     *
     * @param request данные для аутентификации
     * @return объект с JWT-токеном
     */
    @Override
    public JwtAuthenticationResponse authentication(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmployeeName(),
                request.getPassword()
        ));

        var user = employeeService
                .userDetailsService()
                .loadUserByUsername(request.getEmployeeName());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Обновление JWT-токена сотрудника.
     * Проверяет действительность текущего токена и выдает новый.
     *
     * @param token текущий JWT-токен
     * @return объект с новым JWT-токеном
     * @throws RuntimeException если токен недействителен
     */
    @Override
    public JwtAuthenticationResponse refreshToken(String token) {
        String username = jwtService.extractUserName(token);

        UserDetails userDetails = employeeService
                .userDetailsService()
                .loadUserByUsername(username);

        if (jwtService.isTokenValid(token, userDetails)) {
            String newToken = jwtService.generateToken(userDetails);
            return new JwtAuthenticationResponse(newToken);
        }

        throw new RuntimeException("Недопустимый токен");
    }
}
