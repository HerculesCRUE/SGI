package org.crue.hercules.sgi.rep.service.sgi;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseReportServiceTest;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * PersonaServiceTest
 */
class PersonaServiceTest extends BaseReportServiceTest {

  private PersonaService sgiApiSgpService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiSgpService = new PersonaService(restApiProperties, restTemplate);
  }

  @Test
  void findById_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findById(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void getDatosContacto_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.getDatosContacto(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void getDatosAcademicos_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.getDatosAcademicos(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void getVinculacion_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.getVinculacion(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

}
