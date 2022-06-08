package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluadorDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeActa;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * InformeActaReportServiceTest
 */
class InformeActaReportServiceTest extends BaseReportEtiServiceTest {

  private InformeActaReportService informeActaReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiSgpService personaService;

  @Mock
  private ConvocatoriaReunionService convocatoriaReunionService;

  @Mock
  private ActaService actaService;

  @BeforeEach
  public void setUp() throws Exception {
    informeActaReportService = new InformeActaReportService(sgiConfigProperties,
        personaService, convocatoriaReunionService, actaService);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeActa_ReturnsResource() throws Exception {
    Long idActa = 1L;

    BDDMockito.given(actaService.findById(idActa)).willReturn((generarMockActa(idActa, 2)));
    BDDMockito.given(actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(idActa))
        .willReturn((generarMockMemoriasEvaluadas(idActa)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(convocatoriaReunionService
        .findAsistentesByConvocatoriaReunionId(1L)).willReturn((generarMockAsistentes("123456F")));

    ReportInformeActa report = new ReportInformeActa();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeActaReportService.getReportInformeActa(report, idActa);
    assertNotNull(reportContent);

  }

  private List<AsistentesDto> generarMockAsistentes(String string) {
    List<AsistentesDto> memorias = new ArrayList<>();
    memorias.add(AsistentesDto.builder()
        .evaluador(EvaluadorDto.builder().personaRef(null).build())
        .motivo("motivo")
        .build());
    return memorias;
  }

  private List<MemoriaEvaluadaDto> generarMockMemoriasEvaluadas(Long idActa) {
    List<MemoriaEvaluadaDto> memorias = new ArrayList<>();
    memorias.add(MemoriaEvaluadaDto.builder()
        .numReferencia("numReferencia")
        .personaRef(null)
        .dictamen("dictamen")
        .version(1)
        .tipoEvaluacion("tipoEvaluacion")
        .build());
    return memorias;
  }

}
