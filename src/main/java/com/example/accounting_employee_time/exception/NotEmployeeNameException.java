package com.example.accounting_employee_time.exception;

public class NotEmployeeNameException extends RuntimeException {
    public NotEmployeeNameException(String message) {
        super(message);
    }

    public NotEmployeeNameException(Throwable cause) {
        super(cause);
    }
}
