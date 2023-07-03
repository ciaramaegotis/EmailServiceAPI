package com.example.email.service.infrastructure.services.impl;

import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.services.EmailProcessorChain;
import com.example.email.service.infrastructure.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorChainImpl implements EmailProcessorChain {
    private final EmailService emailService;

    @Override
    public SendEmailResponse startChain(SendEmailRequest sendEmailRequest) {

        SendEmailResponse response = emailService.sendEmail(sendEmailRequest);
        log.info("Email/s sent via " + response.getHandler());
        return response;
    }
}
