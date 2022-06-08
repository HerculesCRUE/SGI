package org.crue.hercules.sgi.rep.service.sgi;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseReportServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiSgpServiceTest
 */
class SgiApiSgpServiceTest extends BaseReportServiceTest {

  private SgiApiSgpService sgiApiSgpService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiSgpService = new SgiApiSgpService(restApiProperties, restTemplate);
  }

  @Test
  void findById_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findById(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findDatosContactoByPersonaId_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findDatosContactoByPersonaId(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findDatosAcademicosByPersonaId_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findDatosAcademicosByPersonaId(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findVinculacionByPersonaId_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findVinculacionByPersonaId(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

}
