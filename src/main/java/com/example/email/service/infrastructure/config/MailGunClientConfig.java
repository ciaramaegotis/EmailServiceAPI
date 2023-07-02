package com.example.email.service.infrastructure.config;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MailGunClientConfig {
  @Value("${mail-gun.api-key}")
  private String apiKey;

  @Bean
  public MailgunMessagesApi mailgunMessagesApi() {
    return MailgunClient.config(apiKey).createApi(MailgunMessagesApi.class);
  }
}
