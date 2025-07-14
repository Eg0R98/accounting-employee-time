package com.example.accounting_employee_time.entity;

import com.example.accounting_employee_time.security.Role;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Сущность "Сотрудник".
 * Используется для регистрации, аутентификации и авторизации пользователей.
 * Также содержит иерархию начальник–подчинённые.
 */
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employees")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class EmployeeEntity implements UserDetails {

    /**
     * Уникальный идентификатор сотрудника.
     * Генерируется с помощью SEQUENCE.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    /**
     * Имя сотрудника (используется как логин).
     */
    @Column(name = "name", unique = true, nullable = false)
    private String employeeName;

    /**
     * Хэш пароля сотрудника.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Должность сотрудника.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;

    /**
     * Департамент, в котором работает сотрудник.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    /**
     * Роль сотрудника (например, ADMIN или USER).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Список подчинённых сотрудников.
     */
    @OneToMany(mappedBy = "chief", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EmployeeEntity> subordinates = new ArrayList<>();

    /**
     * Начальник текущего сотрудника.
     * Может быть null, если сотрудник — директор.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chief_id")
    private EmployeeEntity chief;

    @Override
    public String getUsername() {
        return this.employeeName;
    }

    /*Метод сообщает, какие роли есть у текущего пользователя*/
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    /*Возвращает true, если учётная запись не просрочена*/
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /*Возвращает true, если аккаунт не заблокирован*/
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /*Возвращает true, если учётные данные ещё действительны*/
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /*Возвращает true, если аккаунт активен*/
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    /*Переопределение equals с помощью Hibernate-прокси*/
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EmployeeEntity employeeEntity = (EmployeeEntity) o;
        return getId() != null && Objects.equals(getId(), employeeEntity.getId());
    }

    /*Переопределение hashCode с помощью Hibernate-прокси*/
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "employeeName = " + employeeName + ")";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentEntity department) {
        this.department = department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<EmployeeEntity> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<EmployeeEntity> subordinates) {
        this.subordinates = subordinates;
    }

    public EmployeeEntity getChief() {
        return chief;
    }

    public void setChief(EmployeeEntity chief) {
        this.chief = chief;
    }
}
