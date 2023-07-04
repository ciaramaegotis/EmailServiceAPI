package com.example.email.service.infrastructure.config;

import com.example.email.service.infrastructure.services.EmailService;
import com.example.email.service.infrastructure.services.impl.MailGunImpl;
import com.example.email.service.infrastructure.services.impl.SendGridImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class EmailChainConfig {
    private final SendGridImpl sendGrid;
    private final MailGunImpl mailGun;

    @Bean
    public EmailService emailService() {
        return sendGrid.link(sendGrid, mailGun);
    }
}
