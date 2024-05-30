package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacion;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

  @Mock
  private SgiApiConfService sgiApiConfService;

  @MockBean
  private ConfiguracionService configuracionService;

  @MockBean
  private EvaluacionService evaluacionService;

  @Mock
  private PersonaService personaService;

  @Mock
  private BaseApartadosRespuestasReportService baseApartadosrespuestasService;

  @BeforeEach
  public void setUp() throws Exception {
    informeEvaluacionReportService = new InformeEvaluacionReportService(sgiConfigProperties,
        sgiApiConfService, personaService, evaluacionService, configuracionService,
        baseApartadosrespuestasService);
  }

  @Test
  void getInformeEvaluacion_ReturnsEvaluacionMemoriaValidationException() throws Exception {

    ReportInformeEvaluacion report = new ReportInformeEvaluacion();
    report.setOutputType(OutputType.PDF);

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/docx/rep-eti-evaluacion.docx"));

    Assertions
        .assertThatThrownBy(() -> informeEvaluacionReportService.getReportInformeEvaluacion(report, null))
        .isInstanceOf(GetDataReportException.class);
  }

  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeEvaluacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));
    BDDMockito.given(personaService.findById("123456F")).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(configuracionService.findConfiguracion()).willReturn((generarMockConfiguracion()));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/docx/rep-eti-evaluacion.docx"));

    ReportInformeEvaluacion report = new ReportInformeEvaluacion();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeEvaluacionReportService.getReportInformeEvaluacion(report, idEvaluacion);

    assertNotNull(reportContent);
  }

}
