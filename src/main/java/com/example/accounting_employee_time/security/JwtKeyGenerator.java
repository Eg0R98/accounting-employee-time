package com.example.accounting_employee_time.security;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Класс-генератор секретного ключа для подписи токенов
 */
public class JwtKeyGenerator {
    public static void main(String[] args) {
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        String secret = Base64.getEncoder().encodeToString(key);
        System.out.println(secret);
    }
}