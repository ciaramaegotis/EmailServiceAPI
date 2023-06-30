package com.example.email.service.infrastructure.services;

import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;

public interface EmailProcessorChain {
  SendEmailResponse startChain(SendEmailRequest sendEmailRequest);
}
