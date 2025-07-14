package com.example.accounting_employee_time.service.impl;


import com.example.accounting_employee_time.entity.EmployeeEntity;
import com.example.accounting_employee_time.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Реализация {@link JwtService}, обеспечивающая создание, проверку и извлечение данных из JWT-токенов.
 * Использует библиотеку io.jsonwebtoken (jjwt) для работы с токенами.
 */
@Service
public class JwtServiceImpl implements JwtService {

    /**
     * Секретный ключ для подписи JWT, берется из настроек приложения.
     */
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    /**
     * Извлекает имя пользователя (subject) из JWT-токена.
     *
     * @param token JWT-токен
     * @return имя пользователя
     */
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерирует JWT-токен для пользователя.
     * В токен добавляются дополнительные клеймы (например, id и роль), если пользователь является {@link EmployeeEntity}.
     *
     * @param userDetails объект с информацией о пользователе
     * @return сгенерированный JWT-токен
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof EmployeeEntity employee) {
            claims.put("id", employee.getId());
            claims.put("role", employee.getRole().name());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверяет, что токен действителен для данного пользователя.
     * Токен считается действительным, если имя пользователя совпадает и токен не истек.
     *
     * @param token JWT-токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден, иначе false
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлекает конкретный клейм из токена с помощью функции-резолвера.
     *
     * @param token JWT-токен
     * @param claimsResolver функция для извлечения данных из клеймов
     * @param <T> тип возвращаемого значения
     * @return извлечённые данные из токена
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Генерирует JWT-токен с указанными клеймами и данными пользователя.
     * Устанавливает дату выпуска и дату истечения токена, подписывает его.
     *
     * @param extraClaims дополнительные данные для токена
     * @param userDetails данные пользователя
     * @return сгенерированный JWT-токен
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                   .setClaims(extraClaims)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24)) // пример времени жизни
                   .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                   .compact();
    }

    /**
     * Проверяет, истек ли срок действия токена.
     *
     * @param token JWT-токен
     * @return true, если токен просрочен, иначе false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения срока действия токена.
     *
     * @param token JWT-токен
     * @return дата истечения срока действия
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все клеймы из JWT-токена.
     *
     * @param token JWT-токен
     * @return объект {@link Claims} с клеймами
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    /**
     * Получает секретный ключ для подписи JWT из закодированной строки.
     *
     * @return ключ для подписи JWT
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
