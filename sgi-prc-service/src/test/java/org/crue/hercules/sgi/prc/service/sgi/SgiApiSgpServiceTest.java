package org.crue.hercules.sgi.prc.service.sgi;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.crue.hercules.sgi.prc.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiSgpServiceTest
 */
class SgiApiSgpServiceTest extends BaseServiceTest {

  private SgiApiSgpService sgiApiSgpService;

  private static final String PERSONA_REF = "23242342";

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
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findPersonaById(PERSONA_REF))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findDatosContactoByPersonaId_ReturnsException() throws Exception {
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findDatosContactoByPersonaId(PERSONA_REF))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findDatosAcademicosByPersonaId_ReturnsException() throws Exception {
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findDatosAcademicosByPersonaId(PERSONA_REF))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findVinculacionByPersonaId_ReturnsException() throws Exception {
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findVinculacionByPersonaId(PERSONA_REF))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findSexeniosByPersonaId_ReturnsException() throws Exception {
    Assertions.assertThatThrownBy(() -> sgiApiSgpService.findSexeniosByAnio(2021))
        .isInstanceOf(MicroserviceCallException.class);
  }

}
