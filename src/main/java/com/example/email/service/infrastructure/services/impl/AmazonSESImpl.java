package com.example.email.service.infrastructure.services.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.example.email.service.application.exceptions.EmailSendingFailedException;
import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonSESImpl extends EmailService {
  @Value("${cloud.aws.source-email}")
  private String from;

  private final AmazonSimpleEmailService amazonSimpleEmailService;

  @Override
  public SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest) {
    try {
      com.amazonaws.services.simpleemail.model.SendEmailRequest request =
          new com.amazonaws.services.simpleemail.model.SendEmailRequest()
              .withDestination(
                  new Destination()
                      .withToAddresses(sendEmailRequest.getRecipients())
                      .withCcAddresses(sendEmailRequest.getCc())
                      .withBccAddresses(sendEmailRequest.getBcc()))
              .withMessage(
                  new Message()
                      .withBody(
                          new Body()
                              .withText(
                                  new Content()
                                      .withCharset("UTF-8")
                                      .withData(sendEmailRequest.getBody())))
                      .withSubject(
                          new Content()
                              .withCharset("UTF-8")
                              .withData(sendEmailRequest.getSubject())))
              .withSource(from);
      SendEmailResult result = amazonSimpleEmailService.sendEmail(request);
      SendEmailResponse response = new SendEmailResponse();
      response.setMessage("SUCCESS");
      response.setStatus(result.getSdkHttpMetadata().getHttpStatusCode());
      response.setHandler("AMAZON SES");

      return response;
    } catch (AmazonSimpleEmailServiceException ex) {
      log.error("The email was not sent. Error message: " + ex.getMessage());
      if (nextProcessor != null) {
        log.info("Failed sending email via Amazon SES Trying Mail Gun..");
        return nextProcessor.sendEmail(sendEmailRequest);
      }
      throw new EmailSendingFailedException(ex);
    }
  }
}
