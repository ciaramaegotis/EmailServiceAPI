package com.example.email.service.infrastructure.services.impl;

import com.example.email.service.application.exceptions.Request4xxException;
import com.example.email.service.application.exceptions.Response5xxException;
import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import com.example.email.service.infrastructure.config.MailgunProperties;
import com.example.email.service.infrastructure.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailGunImpl extends EmailService {
    private final MailgunProperties mailgunProperties;

    private final WebClient mailGunClient;

    @Override
    public SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest) {
        String url = String.format("%s/%s", mailgunProperties.getSourceEmail(), "messages");
        try {
            MultiValueMap<String, String> formData = buildRequest(sendEmailRequest);
            mailGunClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class).map(ex -> new Request4xxException(ex, response.statusCode().value()))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class).map(ex -> new Response5xxException(ex, response.statusCode().value()))).
                    toBodilessEntity().block();

            SendEmailResponse response = new SendEmailResponse();
            response.setMessage("SUCCESS");
            response.setStatus(200);
            response.setHandler("MAILGUN");

            return response;
        } catch (Exception e) {
            log.error("The email was not sent. Error message: " + e.getMessage());
            if (nextProcessor != null) {
                log.info("Failed sending email via {}. Trying {}", this.getClass(), nextProcessor.getClass());
                return nextProcessor.sendEmail(sendEmailRequest);
            }
            throw e;
        }
    }

    private MultiValueMap<String, String> buildEmailList(String name, List<String> values) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        if (ObjectUtils.isEmpty(values)) {
            return form;
        }
        for (String val : values) {
            form.add(name, val);
        }

        return form;
    }

    private MultiValueMap<String, String> buildRequest(SendEmailRequest sendEmailRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("from", String.format("%s <mailgun@%s>", "MailGun", mailgunProperties.getSourceEmail()));
        formData.addAll(buildEmailList("to", sendEmailRequest.getRecipients()));
        MultiValueMap<String, String> ccList = buildEmailList("cc", sendEmailRequest.getCc());
        if (!ObjectUtils.isEmpty(ccList)) {
            formData.addAll(ccList);
        }
        MultiValueMap<String, String> bccList = buildEmailList("bcc", sendEmailRequest.getBcc());
        if (!ObjectUtils.isEmpty(bccList)) {
            formData.addAll(bccList);
        }
        formData.add("subject", sendEmailRequest.getSubject());
        formData.add("text", sendEmailRequest.getBody());

        return formData;
    }
}