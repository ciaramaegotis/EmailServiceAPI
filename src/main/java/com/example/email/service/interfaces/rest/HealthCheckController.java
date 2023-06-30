package com.example.email.service.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  /** For beanstalk health check */
  @GetMapping("/")
  public ResponseEntity healthCheck() {
    return ResponseEntity.ok().build();
  }
}
