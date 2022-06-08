package org.crue.hercules.sgi.rep.service.eti;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * EvaluacionServiceTest
 */
class EvaluacionServiceTest extends BaseReportEtiServiceTest {

  private EvaluacionService evaluacionService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    evaluacionService = new EvaluacionService(restApiProperties, restTemplate);
  }

  @Test
  void findByEvaluacionIdGestor_ReturnsException() throws Exception {
    Long idEvaluacion = 1L;

    Assertions.assertThatThrownBy(() -> evaluacionService.findByEvaluacionIdGestor(idEvaluacion))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void countByEvaluacionIdAndTipoComentarioId_ReturnsException() throws Exception {
    Long idEvaluacion = 1L;

    Assertions.assertThatThrownBy(() -> evaluacionService.countByEvaluacionIdAndTipoComentarioId(idEvaluacion, 1L))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findIdPresidenteByIdEvaluacion_ReturnsNullPointerException() throws Exception {
    Long idEvaluacion = 1L;

    Assertions.assertThatThrownBy(() -> evaluacionService.findIdPresidenteByIdEvaluacion(idEvaluacion))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findFirstFechaEnvioSecretariaByIdEvaluacion_ReturnsException() throws Exception {
    Long idEvaluacion = 1L;

    Assertions.assertThatThrownBy(() -> evaluacionService.findFirstFechaEnvioSecretariaByIdEvaluacion(idEvaluacion))
        .isInstanceOf(GetDataReportException.class);
  }

}
