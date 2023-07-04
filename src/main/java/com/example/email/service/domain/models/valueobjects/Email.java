package com.example.email.service.domain.models.valueobjects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Email {
    private String email;
}
