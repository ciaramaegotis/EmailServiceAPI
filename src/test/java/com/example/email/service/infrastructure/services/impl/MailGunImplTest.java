package com.example.email.service.infrastructure.services.impl;

import com.example.email.service.TestFixtures;
import com.example.email.service.application.exceptions.Request4xxException;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.config.MailgunProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class MailGunImplTest {
    @Mock
    private SendGridImpl sendGrid;
    @InjectMocks
    private MailGunImpl mailGun;
    @Mock
    private WebClient mailGunClient;
    @Mock
    private MailgunProperties mailgunProperties;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Test
    void sendEmail_ok() {
        when(mailgunProperties.getSourceEmail()).thenReturn("source-email");
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(mailGunClient.post()).thenReturn(requestBodyUriSpec);


        SendEmailResponse response = mailGun.sendEmail(TestFixtures.sendEmailRequest());
        assertEquals(200, response.getStatus());
        assertEquals("SUCCESS", response.getMessage());
        assertEquals("MAILGUN", response.getHandler());
    }

    @Test
    void sendEmail_4xx() {
        when(mailgunProperties.getSourceEmail()).thenReturn("source-email");
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(responseSpec.onStatus(any(), any())).thenThrow(new Request4xxException("message", 400));
        when(mailGunClient.post()).thenReturn(requestBodyUriSpec);

        assertThrows(Request4xxException.class, () -> mailGun.sendEmail(TestFixtures.sendEmailRequest()));
    }

    @Test
    void sendEmail_ErrorWithSuccess() {
        mailGun.setNextProcessor(sendGrid);
        when(mailgunProperties.getSourceEmail()).thenReturn("source-email");
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(responseSpec.onStatus(any(), any())).thenThrow(new Request4xxException("message", 400));
        when(mailGunClient.post()).thenReturn(requestBodyUriSpec);

        assertDoesNotThrow(() -> mailGun.sendEmail(TestFixtures.sendEmailRequest()));
    }
}
