package com.example.email.service.interfaces.error;

import com.example.email.service.application.exceptions.Request4xxException;
import com.example.email.service.application.exceptions.Response5xxException;
import com.example.email.service.domain.models.valueobjects.SendEmailResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
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

    @ExceptionHandler({Request4xxException.class})
    public ResponseEntity<SendEmailResponse> exception4xxHandler(
            Request4xxException ex) {
        SendEmailResponse response = new SendEmailResponse();
        response.setStatus(ex.getStatusCode());
        response.setMessage("FAILED");
        response.setDetails(ex.getMessage());

        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler({Response5xxException.class})
    public ResponseEntity<SendEmailResponse> exception5xxHandler(
            Response5xxException ex) {
        SendEmailResponse response = new SendEmailResponse();
        response.setStatus(ex.getStatusCode());
        response.setMessage("FAILED");
        response.setDetails(ex.getMessage());

        return ResponseEntity.status(ex.getStatusCode()).body(response);
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
