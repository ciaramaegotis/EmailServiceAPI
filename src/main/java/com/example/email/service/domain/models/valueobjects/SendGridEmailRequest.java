package com.example.email.service.domain.models.valueobjects;

import lombok.Data;

import java.util.List;

@Data
public class SendGridEmailRequest {
    private List<Personalization> personalizations;
    private Email from;
    private String subject;
    private List<Content> content;
}


