package com.example.email.service.infrastructure.services.impl;

import com.example.email.service.TestFixtures;
import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailProcessorChainImplTest {
    @Mock
    private EmailService emailService;
    @InjectMocks
    private EmailProcessorChainImpl emailProcessorChain;

    @Test
    public void startChain_ok() {
        when(emailService.sendEmail(any(SendEmailRequest.class)))
                .thenReturn(TestFixtures.sendEmailResponse());
        SendEmailResponse response = emailProcessorChain.startChain(TestFixtures.sendEmailRequest());
        assertEquals(TestFixtures.sendEmailResponse(), response);
    }
}
