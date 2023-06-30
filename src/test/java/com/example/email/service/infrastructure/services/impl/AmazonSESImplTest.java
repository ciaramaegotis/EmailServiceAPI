package com.example.email.service.infrastructure.services.impl;

import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.example.email.service.TestFixtures;
import com.example.email.service.application.exceptions.EmailSendingFailedException;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmazonSESImplTest {
  @Mock private AmazonSimpleEmailService amazonSimpleEmailService;
  @Mock private MailGunImpl mailGun;
  @InjectMocks private AmazonSESImpl amazonSES;

  @Test
  void sendEmail_ok() {
    SendEmailResult result = mock(SendEmailResult.class);
    SdkHttpMetadata metadata = mock(SdkHttpMetadata.class);
    when(result.getSdkHttpMetadata()).thenReturn(metadata);
    when(metadata.getHttpStatusCode()).thenReturn(200);

    when(amazonSimpleEmailService.sendEmail(any(SendEmailRequest.class))).thenReturn(result);
    SendEmailResponse response = amazonSES.sendEmail(TestFixtures.sendEmailRequest());
    assertEquals(200, response.getStatus());
    assertEquals("SUCCESS", response.getMessage());
    assertEquals("AMAZON SES", response.getHandler());
  }

  @Test
  void sendEmail_exceptionWithNoSuccessor() {
    when(amazonSimpleEmailService.sendEmail(any(SendEmailRequest.class)))
        .thenThrow(new MessageRejectedException("Unverified address"));
    assertThrows(
        EmailSendingFailedException.class,
        () -> amazonSES.sendEmail(TestFixtures.sendEmailRequest()));
  }

  @Test
  void sendEmail_exceptionWithSuccessor() {
    amazonSES.setNextProcessor(mailGun);
    when(amazonSimpleEmailService.sendEmail(any(SendEmailRequest.class)))
        .thenThrow(new MessageRejectedException("Unverified address"));
    when(mailGun.sendEmail(
            any(com.example.email.service.domain.models.valueobjects.SendEmailRequest.class)))
        .thenReturn(TestFixtures.sendEmailResponse());
    SendEmailResponse response = amazonSES.sendEmail(TestFixtures.sendEmailRequest());
    assertEquals(TestFixtures.sendEmailResponse(), response);
  }
}
