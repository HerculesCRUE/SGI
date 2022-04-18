package org.crue.hercules.sgi.csp.service.sgi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComInicioPresentacionGastoData;
import org.crue.hercules.sgi.csp.dto.com.EmailInput.Deferrable;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SgiApiComServiceTest extends BaseServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;
  @Mock
  private ObjectMapper objectMapper;

  private SgiApiComService emailService;

  @BeforeEach
  public void setup() {
    BDDMockito.given(this.restApiProperties.getComUrl()).willReturn("smtp.gmail.com");
    this.emailService = new SgiApiComService(this.restApiProperties, this.restTemplate, this.objectMapper);
  }

  @Test
  void createGenericEmailText_ReturnsEmailOutput() {
    String subject = "Asunto";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();
    Deferrable deferrableRecipients = Deferrable.builder().build();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    EmailOutput response = this.emailService.createGenericEmailText(subject, content, recipients,
        deferrableRecipients);

    Assertions.assertThat(response).isNotNull();
  }

  @Test
  void updateGenericEmailText_ReturnsEmailOutput() {
    String subject = "Asunto test";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();
    Deferrable deferrableRecipients = Deferrable.builder().build();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    EmailOutput response = this.emailService.updateGenericEmailText(emailOutput.getId(), subject, content, recipients,
        deferrableRecipients);

    Assertions.assertThat(response).isNotNull();
  }

  @Test
  void deleteEmail_VerifyCallDeleteApiService() {

    this.emailService.deleteEmail(1L);

    verify(this.restTemplate, times(1)).exchange(ArgumentMatchers
        .<String>any(), ArgumentMatchers.<HttpMethod>any(),
        ArgumentMatchers.<HttpEntity<Object>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<Void>>any(),
        ArgumentMatchers.<Object>any());
  }

  @Test
  void createConvocatoriaHitoEmail_ReturnsLong() {
    String subject = "Asunto test";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();

    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    Long response = this.emailService.createConvocatoriaHitoEmail(1L, subject, content, recipients);

    Assertions.assertThat(response).isEqualTo(1L);
  }

  @Test
  void updateConvocatoriaHitoEmail_VerifyCallApiRestUpdate() {
    String subject = "Asunto test";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    this.emailService.updateConvocatoriaHitoEmail(1L, 1L, subject, content, recipients);

    verify(this.restTemplate, times(1)).exchange(ArgumentMatchers
        .<String>any(), ArgumentMatchers.<HttpMethod>any(),
        ArgumentMatchers.<HttpEntity<Object>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
        ArgumentMatchers.<Object>any());
  }

  @Test
  void createComunicadoInicioPresentacionJustificacionGastosEmail_ReturnsEmailOutput() throws JsonProcessingException {
    CspComInicioPresentacionGastoData data = CspComInicioPresentacionGastoData.builder()
        .fecha(LocalDate.now())
        .build();
    List<Recipient> recipients = this.buildMockRecipients();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito.given(this.objectMapper.writeValueAsString(data)).willReturn("/test");

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    EmailOutput response = this.emailService.createComunicadoInicioPresentacionJustificacionGastosEmail(data,
        recipients);

    Assertions.assertThat(response).isNotNull();
  }

  private EmailOutput buildMockEmailOutput(Long id) {
    return EmailOutput.builder()
        .id(id)
        .build();
  }

  private List<Recipient> buildMockRecipients() {
    return Arrays.asList(Recipient.builder()
        .address("test@gmail.com")
        .name("test")
        .build());
  }
}
