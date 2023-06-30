package com.example.email.service.domain.models.valueobjects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SendEmailRequest {
  @NotEmpty private List<@Email(message = "should be a valid email address") @NotBlank String> recipients;

  private List<@Email(message = "should be a valid email address") @NotBlank  String> cc;

  private List<@Email(message = "should be a valid email address") @NotBlank String> bcc;

  @NotNull(message = "should not be null")
  private String body;

  @NotNull(message = "should not be null")
  private String subject;
}
