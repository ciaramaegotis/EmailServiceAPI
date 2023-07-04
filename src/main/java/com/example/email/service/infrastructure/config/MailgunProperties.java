package com.example.email.service.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mail-gun")
@Data
public class MailgunProperties {

    private String baseUrl;

    private String username;

    private String apiKey;

    private String sourceEmail;
}
