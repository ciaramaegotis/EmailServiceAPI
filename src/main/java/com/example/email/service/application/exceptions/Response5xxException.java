package com.example.email.service.application.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response5xxException extends RuntimeException {
    private int statusCode;

    public Response5xxException(String e) {
        super(e);
    }

    public Response5xxException(String e, int statusCode) {
        super(e);
        this.statusCode = statusCode;
    }
}
