package com.example.email.service.domain.models.valueobjects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Content {
    private String type;
    private String value;
}
