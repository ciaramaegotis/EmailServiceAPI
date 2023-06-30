package com.example.email.service.infrastructure.services.impl;

import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.services.EmailProcessorChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorChainImpl implements EmailProcessorChain {
  private final AmazonSESImpl amazonSES;
  private final MailGunImpl mailGun;

  @Override
  public SendEmailResponse startChain(SendEmailRequest sendEmailRequest) {
    amazonSES.setNextProcessor(mailGun);
    mailGun.setNextProcessor(null);

    SendEmailResponse response = amazonSES.sendEmail(sendEmailRequest);
    log.info("Email/s sent via " + response.getHandler());
    return response;
  }
}
