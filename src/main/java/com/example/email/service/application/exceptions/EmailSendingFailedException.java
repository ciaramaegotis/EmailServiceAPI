package com.example.email.service.application.exceptions;

public class EmailSendingFailedException extends RuntimeException {
    public EmailSendingFailedException(Exception e) {
        super(e);
    }
}
