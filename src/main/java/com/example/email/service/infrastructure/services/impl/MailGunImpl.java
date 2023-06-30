package com.example.email.service.infrastructure.services.impl;

import com.example.email.service.application.exceptions.EmailSendingFailedException;
import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.services.EmailService;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailGunImpl extends EmailService {
  @Value("${mail-gun.source-email}")
  private String sendingDomain;

  private final MailgunMessagesApi mailgunMessagesApi;

  @Override
  public SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest) {
    try {
      Message message =
          Message.builder()
              .from(String.format("mailgun@%s", sendingDomain))
              .to(sendEmailRequest.getRecipients())
              .cc(sendEmailRequest.getCc())
              .bcc(sendEmailRequest.getBcc())
              .subject(sendEmailRequest.getSubject())
              .text(sendEmailRequest.getBody())
              .build();
      mailgunMessagesApi.sendMessage(sendingDomain, message);
      SendEmailResponse response = new SendEmailResponse();
      response.setMessage("SUCCESS");
      response.setStatus(200);
      response.setHandler("MAILGUN");

      return response;
    } catch (Exception e) {
      log.error("The email was not sent. Error message: " + e.getMessage());
      if (nextProcessor != null) {
        log.info("Failed sending email via Amazon SES Trying Mail Gun..");
        return nextProcessor.sendEmail(sendEmailRequest);
      }
      throw new EmailSendingFailedException(e);
    }
  }
}
