package org.crue.hercules.sgi.rep.service.eti;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * ActaServiceTest
 */
class ActaServiceTest extends BaseReportEtiServiceTest {

  private ActaService actaService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    actaService = new ActaService(restApiProperties, restTemplate);
  }

  @Test
  void countEvaluacionesNuevas_ReturnsException() throws Exception {
    Long idActa = 1L;

    Assertions.assertThatThrownBy(() -> actaService.countEvaluacionesNuevas(idActa))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void countEvaluacionesRevisionSinMinima_ReturnsException() throws Exception {
    Long idActa = 1L;

    Assertions.assertThatThrownBy(() -> actaService.countEvaluacionesRevisionSinMinima(idActa))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findAllMemoriasEvaluadasSinRevMinimaByActaId_ReturnsException() throws Exception {
    Long idActa = 1L;

    Assertions.assertThatThrownBy(() -> actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(idActa))
        .isInstanceOf(GetDataReportException.class);
  }

}
