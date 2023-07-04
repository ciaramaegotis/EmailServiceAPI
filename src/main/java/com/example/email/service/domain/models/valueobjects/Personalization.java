package com.example.email.service.domain.models.valueobjects;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Personalization {
    private List<Email> to;
    private List<Email> cc;
    private List<Email> bcc;
}
