package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacion;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeEvaluacionReportServiceTest
 */
class InformeEvaluacionReportServiceTest extends BaseReportEtiServiceTest {

  private InformeEvaluacionReportService informeEvaluacionReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @MockBean
  private ConfiguracionService configuracionService;

  @MockBean
  private EvaluacionService evaluacionService;

  @Mock
  private SgiApiSgpService personaService;

  @Mock
  private BloqueService bloqueService;

  @Mock
  private ApartadoService apartadoService;

  @Mock
  private SgiFormlyService sgiFormlyService;

  @Mock
  private RespuestaService respuestaService;

  @BeforeEach
  public void setUp() throws Exception {
    informeEvaluacionReportService = new InformeEvaluacionReportService(sgiConfigProperties,
        personaService, bloqueService,
        apartadoService, sgiFormlyService, respuestaService, evaluacionService, configuracionService);
  }

  @Test
  void getInformeEvaluacion_ReturnsMemoriaValidationException() throws Exception {

    ReportInformeEvaluacion report = new ReportInformeEvaluacion();
    report.setOutputType(OutputType.PDF);

    Assertions
        .assertThatThrownBy(() -> informeEvaluacionReportService.getReportInformeEvaluadorEvaluacion(report, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeEvaluacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(configuracionService.findConfiguracion()).willReturn((generarMockConfiguracion()));
    BDDMockito.given(
        evaluacionService.findByEvaluacionIdGestor(idEvaluacion)).willReturn((generarMockComentarios()));

    ReportInformeEvaluacion report = new ReportInformeEvaluacion();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeEvaluacionReportService.getReportInformeEvaluadorEvaluacion(report, idEvaluacion);

    assertNotNull(reportContent);
  }

}
