package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluador;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeEvaluadorReportServiceTest
 */
class InformeEvaluadorReportServiceTest extends BaseReportEtiServiceTest {

  private InformeEvaluadorReportService informeEvaluadorReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

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
    informeEvaluadorReportService = new InformeEvaluadorReportService(sgiConfigProperties,
        sgiApiConfService, personaService, bloqueService,
        apartadoService, sgiFormlyService, respuestaService, evaluacionService);
  }

  @Test
  void getInformeEvaluacion_ReturnsMemoriaValidationException() throws Exception {

    ReportInformeEvaluador report = new ReportInformeEvaluador();
    report.setOutputType(OutputType.PDF);

    Assertions
        .assertThatThrownBy(() -> informeEvaluadorReportService.getReportInformeEvaluadorEvaluacion(report, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeEvaluador_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(
        evaluacionService.findByEvaluacionIdEvaluador(idEvaluacion)).willReturn((generarMockComentarios()));
    BDDMockito.given(bloqueService.getBloqueComentariosGenerales())
        .willReturn(generarMockBloque(idEvaluacion, 0L));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/prpt/rep-eti-ficha-evaluador.prpt"));
    BDDMockito.given(sgiApiConfService.getServiceBaseURL()).willReturn("");

    ReportInformeEvaluador report = new ReportInformeEvaluador();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeEvaluadorReportService.getReportInformeEvaluadorEvaluacion(report, idEvaluacion);
    assertNotNull(reportContent);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeEvaluacionTipoActividad_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion))
        .willAnswer((InvocationOnMock invocation) -> {
          EvaluacionDto evaluacion = generarMockEvaluacion(idEvaluacion);
          evaluacion.getMemoria().getPeticionEvaluacion().setTipoInvestigacionTutelada(null);
          return evaluacion;
        });
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/prpt/rep-eti-ficha-evaluador.prpt"));
    BDDMockito.given(sgiApiConfService.getServiceBaseURL()).willReturn("");

    ReportInformeEvaluador report = new ReportInformeEvaluador();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeEvaluadorReportService.getReportInformeEvaluadorEvaluacion(report, idEvaluacion);
    assertNotNull(reportContent);
  }

}
