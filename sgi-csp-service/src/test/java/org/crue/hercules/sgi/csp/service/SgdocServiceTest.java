package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyString;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.DocumentoOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class SgdocServiceTest extends BaseServiceTest {

  @Mock
  private RestApiProperties restApiProperties;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private HttpServletRequest httpServletRequest;

  private SgdocService sgdocService;

  @BeforeEach
  public void setup() {
    this.sgdocService = new SgdocService(this.restApiProperties, this.restTemplate);
    
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    
  }

  @Test
  void uploadInforme_ReturnsDocumentoOutput() throws IOException {
    String filename = "application.yml";
    final Resource docFile = new ClassPathResource("application.yml");

    final ResponseEntity<DocumentoOutput>expectedDocumento = ResponseEntity.ok(DocumentoOutput.builder()
    .archivo(IOUtils.toByteArray(docFile.getInputStream()))
    .documentoRef("application.yml")
    .autorRef("user")
    .fechaCreacion(LocalDateTime.now())
    .nombre("application.yml")
    .build());

    BDDMockito.given(this.restTemplate.exchange(anyString(), ArgumentMatchers.<HttpMethod>any(), ArgumentMatchers.<HttpEntity<Object>>any(), ArgumentMatchers.<Class<DocumentoOutput>>any())).willReturn(expectedDocumento);

    DocumentoOutput documento = sgdocService.uploadInforme(filename, docFile);

    Assertions.assertThat(documento).isNotNull().isEqualTo(expectedDocumento.getBody());
  }
  
  @Test
  void uploadInforme_ThrowsNullPointerException() {
    String filename = "application.yml";
    final Resource docFile = new ClassPathResource("application.yml");

    final ResponseEntity<DocumentoOutput>expectedDocumento = ResponseEntity.ok(null);

    BDDMockito.given(this.restTemplate.exchange(anyString(), ArgumentMatchers.<HttpMethod>any(), ArgumentMatchers.<HttpEntity<Object>>any(), ArgumentMatchers.<Class<DocumentoOutput>>any())).willReturn(expectedDocumento);

    Assertions.assertThatThrownBy(() -> sgdocService.uploadInforme(filename, docFile))
    .isInstanceOf(NullPointerException.class);
  }
}