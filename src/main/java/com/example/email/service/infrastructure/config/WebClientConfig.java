package com.example.email.service.infrastructure.config;

import io.netty.handler.logging.LogLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    private final MailgunProperties mailgunProperties;

    private final SendgridProperties sendgridProperties;

    @Bean
    public WebClient mailGunClient() {
        return webClientBuilder(mailgunProperties.getBaseUrl()).defaultHeaders(header -> header.setBasicAuth(mailgunProperties.getUsername(), mailgunProperties.getApiKey())).build();
    }

    @Bean
    public WebClient sendGridClient() {
        return webClientBuilder(sendgridProperties.getBaseUrl()).defaultHeaders(header -> header.setBearerAuth(sendgridProperties.getBearer())).build();
    }

    private WebClient.Builder webClientBuilder(String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).clientConnector(httpConnector()).filters(this::exchangeFilters);
    }

    private ClientHttpConnector httpConnector() {
        HttpClient httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG);
        return new ReactorClientHttpConnector(httpClient);
    }

    private void exchangeFilters(List<ExchangeFilterFunction> exchangeFilterFunctions) {
        exchangeFilterFunctions.add(logRequest());
        exchangeFilterFunctions.add(logResponse());
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("HTTP Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("HTTTP Response {}\nHeaders {}", clientResponse.statusCode(), clientResponse.headers().asHttpHeaders());
            return Mono.just(clientResponse);
        });
    }
}
