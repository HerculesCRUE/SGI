package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableMemoria;
import org.crue.hercules.sgi.rep.dto.eti.TareaDto;
import org.crue.hercules.sgi.rep.dto.eti.TareaDto.EquipoTrabajoDto;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeFavorableMemoriaReportServiceTest
 */
class InformeFavorableMemoriaReportServiceTest extends BaseReportEtiServiceTest {

  private InformeFavorableMemoriaReportService informeFavorableMemoriaReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @MockBean
  private EvaluacionService evaluacionService;

  @Mock
  private PersonaService personaService;

  @Mock
  private PeticionEvaluacionService peticionEvaluacionService;

  @BeforeEach
  public void setUp() throws Exception {
    informeFavorableMemoriaReportService = new InformeFavorableMemoriaReportService(
        sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, peticionEvaluacionService);
  }

  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeFavorableMemoria_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion)).willReturn((generarMockEvaluacion(idEvaluacion)));
    BDDMockito.given(peticionEvaluacionService.getTareasEquipoTrabajo(idEvaluacion))
        .willReturn((generarMockTareas(idEvaluacion)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/docx/rep-eti-evaluacion-favorable-memoria-nueva.docx"));
    BDDMockito.given(sgiApiConfService.getServiceBaseURL()).willReturn("");

    ReportInformeFavorableMemoria report = new ReportInformeFavorableMemoria();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeFavorableMemoriaReportService.getReportInformeFavorableMemoria(report, idEvaluacion);
    assertNotNull(reportContent);

  }

  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeFavorableMemoriaGeneroF_ReturnsResource() throws Exception {
    Long idEvaluacion = 1L;

    BDDMockito.given(evaluacionService.findById(idEvaluacion))
        .willAnswer((InvocationOnMock invocation) -> {
          EvaluacionDto evaluacion = generarMockEvaluacion(idEvaluacion);
          evaluacion.getMemoria().getComite().setGenero(Genero.F);
          return evaluacion;
        });
    BDDMockito.given(peticionEvaluacionService.getTareasEquipoTrabajo(idEvaluacion))
        .willReturn((generarMockTareas(idEvaluacion)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("eti/prpt/rep-eti-evaluacion-favorable-memoria-nueva.prpt"));
    BDDMockito.given(sgiApiConfService.getServiceBaseURL()).willReturn("");

    ReportInformeFavorableMemoria report = new ReportInformeFavorableMemoria();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeFavorableMemoriaReportService.getReportInformeFavorableMemoria(report, idEvaluacion);
    assertNotNull(reportContent);

  }

  private List<TareaDto> generarMockTareas(Long idEvaluacion) {
    List<TareaDto> tareas = new ArrayList<>();
    tareas.add(TareaDto.builder()
        .equipoTrabajo(EquipoTrabajoDto.builder().personaRef(null).build())
        .build());
    return tareas;
  }

}
