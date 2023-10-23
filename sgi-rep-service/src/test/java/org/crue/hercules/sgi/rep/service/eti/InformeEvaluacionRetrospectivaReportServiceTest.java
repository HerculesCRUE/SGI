package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacionRetrospectiva;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeEvaluacionRetrospectivaReportServiceTest
 */
class InformeEvaluacionRetrospectivaReportServiceTest extends BaseReportEtiServiceTest {

  private InformeEvaluacionRetrospectivaReportService informeEvaluacionRetrospectivaReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @Mock
  private EvaluacionService evaluacionService;

  @Mock
  private PersonaService personaService;

  @BeforeEach
  public void setUp() throws Exception {
    informeEvaluacionRetrospectivaReportService = new InformeEvaluacionRetrospectivaReportService(
        sgiConfigProperties, sgiApiConfService, personaService, evaluacionService);
  }

  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeEvaluacionRetrospectiva_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;
    InformeEvaluacionReportInput input = InformeEvaluacionReportInput.builder().idEvaluacion(idEvaluacion)
        .fecha(Instant.now()).build();

    BDDMockito.given(evaluacionService.findById(idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/docx/rep-eti-evaluacion-retrospectiva.docx"));

    ReportInformeEvaluacionRetrospectiva report = new ReportInformeEvaluacionRetrospectiva();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeEvaluacionRetrospectivaReportService.getReportInformeEvaluacionRetrospectiva(report,
        input);
    assertNotNull(reportContent);

  }

}
