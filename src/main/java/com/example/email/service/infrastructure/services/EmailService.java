package com.example.email.service.infrastructure.services;

import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import org.springframework.util.ObjectUtils;

public abstract class EmailService {
    protected EmailService nextProcessor;

    public abstract SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest);

    public void setNextProcessor(EmailService service) {
        this.nextProcessor = service;
    }

    public EmailService link(EmailService first, EmailService... chain) {
        EmailService head = first;

        if (ObjectUtils.isEmpty(chain)) {
            return head;
        }
        for (EmailService nextInChain : chain) {
            head.setNextProcessor(nextInChain);
            head = nextInChain;
        }
        return first;
    }
}
