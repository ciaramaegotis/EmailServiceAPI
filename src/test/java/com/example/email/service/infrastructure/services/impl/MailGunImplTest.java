package com.example.email.service.infrastructure.services.impl;

import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.example.email.service.TestFixtures;
import com.example.email.service.application.exceptions.EmailSendingFailedException;
import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MailGunImplTest {
  @Mock private MailgunMessagesApi mailgunMessagesApi;
  @InjectMocks private MailGunImpl mailGun;
  @Mock private AmazonSESImpl amazonSES;

  @BeforeEach
  public void setEnvVar() {
    ReflectionTestUtils.setField(mailGun, "sendingDomain", "sample@domail.com");
  }

  @Test
  void sendEmail_ok() {
    MessageResponse messageResponse = mock(MessageResponse.class);

    when(mailgunMessagesApi.sendMessage(any(String.class), any(Message.class)))
        .thenReturn(messageResponse);
    SendEmailResponse response = mailGun.sendEmail(TestFixtures.sendEmailRequest());
    assertEquals(200, response.getStatus());
    assertEquals("SUCCESS", response.getMessage());
    assertEquals("MAILGUN", response.getHandler());
  }

  @Test
  void sendEmail_exceptionWithNoSuccessor() {
    when(mailgunMessagesApi.sendMessage(any(String.class), any(Message.class)))
        .thenThrow(new MessageRejectedException("Unverified address"));
    assertThrows(
        EmailSendingFailedException.class,
        () -> mailGun.sendEmail(TestFixtures.sendEmailRequest()));
  }

  @Test
  void sendEmail_exceptionWithSuccessor() {
    mailGun.setNextProcessor(amazonSES);
    when(mailgunMessagesApi.sendMessage(any(String.class), any(Message.class)))
        .thenThrow(new MessageRejectedException("Unverified address"));
    when(amazonSES.sendEmail(
            any(SendEmailRequest.class)))
        .thenReturn(TestFixtures.sendEmailResponse());
    SendEmailResponse response = mailGun.sendEmail(TestFixtures.sendEmailRequest());
    assertEquals(TestFixtures.sendEmailResponse(), response);
  }
}
