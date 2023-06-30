package com.example.email.service.domain.models.valueobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SendEmailResponse {
  private int status;
  private String message;
  private String handler;
  private String details;
}
