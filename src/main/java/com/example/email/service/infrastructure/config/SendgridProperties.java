package com.example.email.service.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sendgrid")
@Data
public class SendgridProperties {
    private String bearer;

    private String baseUrl;

    private String sourceEmail;
}
