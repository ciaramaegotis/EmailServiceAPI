package com.example.email.service.application.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Request4xxException extends RuntimeException {
    private int statusCode;

    public Request4xxException(String e) {
        super(e);
    }

    public Request4xxException(String e, int statusCode) {
        super(e);
        this.statusCode = statusCode;
    }
}
