package com.example.email.service.interfaces.rest;

import com.example.email.service.TestFixtures;
import com.example.email.service.infrastructure.services.EmailProcessorChain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {
  @Mock private EmailProcessorChain emailProcessorChain;
  @InjectMocks private EmailController controller;

  @Test
  void sendEmail_ok() throws Exception {
    mockMvc()
        .perform(
            MockMvcRequestBuilders.post("/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(TestFixtures.sendEmailRequest())))
        .andExpect(status().isOk());
    verify(emailProcessorChain).startChain(TestFixtures.sendEmailRequest());
  }

  private MockMvc mockMvc() {
    return MockMvcBuilders.standaloneSetup(controller).build();
  }

  private static String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
