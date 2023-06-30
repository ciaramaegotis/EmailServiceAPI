package com.example.email.service;

import com.example.email.service.domain.models.valueobjects.SendEmailRequest;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;

import java.util.List;

public class TestFixtures {
  public static List<String> emailList() {
    return List.of("email1@domail.com", "email2@domain.com");
  }

  public static SendEmailRequest sendEmailRequest() {
    SendEmailRequest sendEmailRequest = new SendEmailRequest();
    sendEmailRequest.setSubject("Test Subject");
    sendEmailRequest.setBody("Test Body");
    sendEmailRequest.setRecipients(emailList());
    sendEmailRequest.setCc(emailList());
    sendEmailRequest.setBcc(emailList());

    return sendEmailRequest;
  }

  public static SendEmailResponse sendEmailResponse() {
    SendEmailResponse response = new SendEmailResponse();
    response.setHandler("Amazon SES");
    response.setStatus(200);
    response.setMessage("SUCESS");

    return response;
  }
}
