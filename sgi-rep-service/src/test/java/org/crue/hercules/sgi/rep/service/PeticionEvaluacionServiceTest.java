package org.crue.hercules.sgi.rep.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.eti.PeticionEvaluacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * PeticionEvaluacionServiceTest
 */
class PeticionEvaluacionServiceTest extends BaseReportServiceTest {

  private PeticionEvaluacionService peticionEvaluacionService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    peticionEvaluacionService = new PeticionEvaluacionService(restApiProperties, restTemplate);
  }

  @Test
  void findMemoriaByPeticionEvaluacionMaxVersion_ReturnsException() throws Exception {
    Long idPeticionEvaluacion = 1L;

    Assertions
        .assertThatThrownBy(
            () -> peticionEvaluacionService.findMemoriaByPeticionEvaluacionMaxVersion(idPeticionEvaluacion))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findTareasEquipoTrabajo_ReturnsException() throws Exception {
    Long idPeticionEvaluacion = 1L;

    Assertions
        .assertThatThrownBy(
            () -> peticionEvaluacionService.findTareasEquipoTrabajo(idPeticionEvaluacion))
        .isInstanceOf(GetDataReportException.class);
  }

}
