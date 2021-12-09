package org.crue.hercules.sgi.rep.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * PersonaServiceTest
 */
class PersonaServiceTest extends BaseReportServiceTest {

  private PersonaService personaService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    personaService = new PersonaService(restApiProperties, restTemplate);
  }

  @Test
  void findById_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> personaService.findById(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findDatosContactoByPersonaId_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> personaService.findDatosContactoByPersonaId(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findDatosAcademicosByPersonaId_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> personaService.findDatosAcademicosByPersonaId(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findVinculacionByPersonaId_ReturnsException() throws Exception {
    String personaRef = "23242342";
    Assertions.assertThatThrownBy(() -> personaService.findVinculacionByPersonaId(personaRef))
        .isInstanceOf(GetDataReportException.class);
  }

}
