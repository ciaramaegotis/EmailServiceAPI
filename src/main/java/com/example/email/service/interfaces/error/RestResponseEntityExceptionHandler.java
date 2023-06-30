package com.example.email.service.interfaces.error;

import com.amazonaws.services.simpleemail.model.AmazonSimpleEmailServiceException;
import com.example.email.service.application.exceptions.EmailSendingFailedException;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import feign.FeignException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({EmailSendingFailedException.class})
  public ResponseEntity<SendEmailResponse> emailSendingFailedException(
      EmailSendingFailedException ex) {
    SendEmailResponse response = new SendEmailResponse();
    response.setStatus(resolveStatusCode(ex.getCause()));
    response.setMessage("FAILED");
    response.setDetails(ex.getMessage());
    return ResponseEntity.internalServerError().body(response);
  }

  private int resolveStatusCode(Throwable cause) {
    if (cause instanceof AmazonSimpleEmailServiceException) {
      return ((AmazonSimpleEmailServiceException) cause).getStatusCode();
    } else if (cause instanceof FeignException) {
      return ((FeignException) cause).status();
    }
    return HttpStatus.INTERNAL_SERVER_ERROR.value();
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    SendEmailResponse response = new SendEmailResponse();
    response.setStatus(400);
    response.setMessage(
        ex.getFieldErrors().stream()
            .map(entry -> String.format("%s %s", entry.getField(), entry.getDefaultMessage()))
            .collect(Collectors.joining(", ")));
    return ResponseEntity.badRequest().body(response);
  }
}
