package com.example.accounting_employee_time.security;

import com.example.accounting_employee_time.entity.DepartmentEntity;
import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.entity.Position;
import com.example.accounting_employee_time.repository.DepartmentRepository;
import com.example.accounting_employee_time.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Создание админа на старте приложения, если его не существует
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final EmployeeRepository repository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    /**
     * Поиск админа в бд.
     * Если не найден, создается новый
     */
    @Override
    public void run(String... args) {
        if (repository.findByEmployeeName(adminProperties.getUsername()).isEmpty()) {

            // Найдем или создадим департамент
            DepartmentEntity department = departmentRepository.findByName("Development")
                                                              .orElseGet(() -> {
                                                                  DepartmentEntity dep = new DepartmentEntity();
                                                                  dep.setName("Development");
                                                                  return departmentRepository.save(dep);
                                                              });

            EmployeeEntity admin = EmployeeEntity.builder()
                                                 .department(department)
                                                 .employeeName(adminProperties.getUsername())
                                                 .password(passwordEncoder.encode(adminProperties.getPassword()))
                                                 .position(Position.DEVELOPER)
                                                 .role(Role.ADMIN).build();

            repository.save(admin);
            log.info("Администратор создан");
        } else {
            log.info("Администратор уже существует");
        }
    }
}
