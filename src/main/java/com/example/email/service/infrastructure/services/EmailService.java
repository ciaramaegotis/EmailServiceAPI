package com.example.email.service.infrastructure.services;

import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;

public abstract class EmailService {
  protected EmailService nextProcessor;

  public abstract SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest);

  public void setNextProcessor(EmailService service) {
    this.nextProcessor = service;
  }
}
