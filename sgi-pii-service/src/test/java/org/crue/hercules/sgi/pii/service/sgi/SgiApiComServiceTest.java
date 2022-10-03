package org.crue.hercules.sgi.pii.service.sgi;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.config.RestApiProperties;
import org.crue.hercules.sgi.pii.dto.com.EmailOutput;
import org.crue.hercules.sgi.pii.dto.com.PiiComFechaLimiteProcedimientoData;
import org.crue.hercules.sgi.pii.dto.com.PiiComMesesHastaFinPrioridadSolicitudProteccionData;
import org.crue.hercules.sgi.pii.dto.com.Recipient;
import org.crue.hercules.sgi.pii.dto.com.Status;
import org.crue.hercules.sgi.pii.service.BaseServiceTest;
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

import com.fasterxml.jackson.databind.ObjectMapper;

class SgiApiComServiceTest extends BaseServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;
  @Mock
  private ObjectMapper objectMapper;

  private SgiApiComService emailService;

  @BeforeEach
  void setup() {
    BDDMockito.given(this.restApiProperties.getComUrl()).willReturn("smtp.gmail.com");
    this.emailService = new SgiApiComService(this.restApiProperties, this.restTemplate, this.objectMapper);
  }

  @Test
  void sendEmail_ReturnStatus() {
    Status mockStatus = new Status();
    mockStatus.setMessage("testing success");

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<Status>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(mockStatus));

    Status status = this.emailService.sendEmail(1L);

    Assertions.assertThat(status).isNotNull();
    Assertions.assertThat(status.getMessage()).isEqualTo(mockStatus.getMessage());
  }

  @Test
  void createComunicadoMesesHastaFinPrioridadSolicitudProteccion_ReturnsEmailOutput() throws Exception {
    PiiComMesesHastaFinPrioridadSolicitudProteccionData data = PiiComMesesHastaFinPrioridadSolicitudProteccionData
        .builder()
        .fechaFinPrioridad(Instant.now())
        .monthsBeforeFechaFinPrioridad(3)
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

    EmailOutput response = this.emailService.createComunicadoMesesHastaFinPrioridadSolicitudProteccion(data,
        recipients);

    Assertions.assertThat(response).isNotNull();
  }

  @Test
  void createComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccio_ReturnsEmailOutputn()
      throws Exception {
    PiiComMesesHastaFinPrioridadSolicitudProteccionData data = PiiComMesesHastaFinPrioridadSolicitudProteccionData
        .builder()
        .fechaFinPrioridad(Instant.now())
        .monthsBeforeFechaFinPrioridad(3)
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

    EmailOutput response = this.emailService
        .createComunicadoAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion(data,
            recipients);

    Assertions.assertThat(response).isNotNull();
  }

  @Test
  void createComunicadoFechaLimiteProcedimiento_ReturnsEmailOutput() throws Exception {
    PiiComFechaLimiteProcedimientoData data = PiiComFechaLimiteProcedimientoData.builder()
        .accionATomar("testing")
        .fechaLimite(Instant.now())
        .tipoProcedimiento("tipo procedimiento")
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

    EmailOutput response = this.emailService.createComunicadoFechaLimiteProcedimiento(data,
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