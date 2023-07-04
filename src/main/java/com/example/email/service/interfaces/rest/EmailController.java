package com.example.email.service.interfaces.rest;

import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.services.EmailProcessorChain;
import com.example.email.service.infrastructure.services.impl.MailGunImpl;
import com.example.email.service.infrastructure.services.impl.SendGridImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EmailController {
    private final EmailProcessorChain emailProcessorChain;

    @PostMapping("/send-email")
    public ResponseEntity<SendEmailResponse> sendEmail(
            @Valid @RequestBody SendEmailRequest sendEmailRequest) {
        log.info("Received send email request={}", sendEmailRequest);

        return ResponseEntity.ok(emailProcessorChain.startChain(sendEmailRequest));
    }
}
