package org.crue.hercules.sgi.rep.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableRatificacion;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.eti.InformeFavorableRatificacionReportService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeFavorableRatificacionReportServiceTest
 */
class InformeFavorableRatificacionReportServiceTest extends BaseReportServiceTest {

  private InformeFavorableRatificacionReportService informeFavorableRatificacionReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private EvaluacionService evaluacionService;

  @Mock
  private SgiApiSgpService personaService;

  @BeforeEach
  public void setUp() throws Exception {
    informeFavorableRatificacionReportService = new InformeFavorableRatificacionReportService(sgiConfigProperties,
        personaService, evaluacionService);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeFavorableRatificacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));

    ReportInformeFavorableRatificacion report = new ReportInformeFavorableRatificacion();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeFavorableRatificacionReportService.getReportInformeFavorableRatificacion(report,
        idEvaluacion);
    assertNotNull(reportContent);

  }

}
