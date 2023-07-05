package com.example.email.service.infrastructure.services.impl;

import com.example.email.service.application.exceptions.Request4xxException;
import com.example.email.service.application.exceptions.Response5xxException;
import com.example.email.service.domain.models.valueobjects.*;
import com.example.email.service.infrastructure.config.SendgridProperties;
import com.example.email.service.infrastructure.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendGridImpl extends EmailService {
    private final SendgridProperties sendgridProperties;
    private final WebClient sendGridClient;

    @Override
    public SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest) {
        try {
            String url = "mail/send";
            SendGridEmailRequest request = buildRequest(sendEmailRequest);
            sendGridClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(request))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class).map(ex -> new Request4xxException(ex, response.statusCode().value()))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class).map(ex -> new Response5xxException(ex, response.statusCode().value())))
                    .toBodilessEntity().block();

            SendEmailResponse sendEmailResponse = new SendEmailResponse();
            sendEmailResponse.setStatus(HttpStatus.OK.value());
            sendEmailResponse.setMessage("SUCCESS");
            sendEmailResponse.setHandler("SENDGRID");

            return sendEmailResponse;
        } catch (Exception ex) {
            log.error("The email was not sent. Error message: " + ex.getMessage());
            if (nextProcessor != null) {
                log.info("Failed sending email via {}. Trying {}", this.getClass(), nextProcessor.getClass());
                return nextProcessor.sendEmail(sendEmailRequest);
            }
            throw ex;
        }
    }

    private List<Email> convertEmail(List<String> emails) {
        if (ObjectUtils.isEmpty(emails)) {
            return null;
        }
        return emails.stream().map(em -> Email.builder().email(em).build()).collect(Collectors.toList());
    }

    private SendGridEmailRequest buildRequest(SendEmailRequest sendEmailRequest) {
        SendGridEmailRequest request = new SendGridEmailRequest();
        request.setSubject(sendEmailRequest.getSubject());
        request.setFrom(Email.builder().email(sendgridProperties.getSourceEmail()).build());
        request.setContent(List.of(Content.builder().type("text/plain").value(sendEmailRequest.getBody()).build()));
        request.setPersonalizations(List.of(Personalization.builder()
                .to(convertEmail(sendEmailRequest.getRecipients()))
                .cc(convertEmail(sendEmailRequest.getCc()))
                .bcc(convertEmail(sendEmailRequest.getBcc())).build()));

        return request;
    }
}
