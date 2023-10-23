package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableModificacion;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeFavorableModificacionReportServiceTest
 */
class InformeFavorableModificacionReportServiceTest extends BaseReportEtiServiceTest {

  private InformeFavorableModificacionReportService informeFavorableModificacionReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @Mock
  private EvaluacionService evaluacionService;

  @Mock
  private PersonaService personaService;

  @Mock
  private ConvocatoriaReunionService convocatoriaReunionService;

  @BeforeEach
  public void setUp() throws Exception {
    informeFavorableModificacionReportService = new InformeFavorableModificacionReportService(
        sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, convocatoriaReunionService);
  }

  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeFavorableModificacion_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(convocatoriaReunionService.findConvocatoriaUltimaEvaluacionTipoMemoria(idEvaluacion, 1L))
        .willReturn((generarMockConvocatoriaReunion(idEvaluacion)));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/docx/rep-eti-evaluacion-favorable-memoria-modificacion.docx"));
    BDDMockito.given(sgiApiConfService.getServiceBaseURL()).willReturn("");

    ReportInformeFavorableModificacion report = new ReportInformeFavorableModificacion();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeFavorableModificacionReportService.getReportInformeFavorableModificacion(report,
        idEvaluacion);
    assertNotNull(reportContent);

  }

}
