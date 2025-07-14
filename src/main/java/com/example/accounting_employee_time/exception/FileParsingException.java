package com.example.accounting_employee_time.exception;

public class FileParsingException extends RuntimeException {
    public FileParsingException(String message) {
        super(message);
    }

    public FileParsingException(Throwable cause) {
        super(cause);
    }

    public FileParsingException(String s, Exception e) {
    }
}
