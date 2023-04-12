package org.crue.hercules.sgi.rep.service.prc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.rep.dto.prc.ReportInformeDetalleGrupo;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenCosteIndirectoOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenSexenioOutput;
import org.crue.hercules.sgi.rep.service.BaseReportServiceTest;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiPrcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * InformeDetalleGrupoReportServiceTest
 */
class InformeDetalleGrupoReportServiceTest extends BaseReportServiceTest {

  private InformeDetalleGrupoReportService informeDetalleGrupoReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @Mock
  private SgiApiPrcService sgiApiPrcService;

  @BeforeEach
  public void setUp() throws Exception {
    informeDetalleGrupoReportService = new InformeDetalleGrupoReportService(
        sgiConfigProperties, sgiApiConfService, sgiApiPrcService);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeDetalleGrupo_empty_ReturnsResource() throws Exception {

    // TODO comprobar permisos

    Long grupoId = 1L;
    Integer anio = 2021;

    BDDMockito.given(
        sgiApiPrcService.getDataReportDetalleGrupo(anio, grupoId)).willReturn((generarMockDetalleGrupo(anio, grupoId)));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("prc/prpt/rep-prc-detalle-grupo.prpt"));
    BDDMockito.given(sgiApiConfService.getServiceBaseURL()).willReturn("");

    ReportInformeDetalleGrupo report = new ReportInformeDetalleGrupo();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeDetalleGrupoReportService.getReportDetalleGrupo(report, anio, grupoId);
    assertNotNull(reportContent);

  }

  private DetalleGrupoInvestigacionOutput generarMockDetalleGrupo(Integer anio, Long grupoId) {

    return DetalleGrupoInvestigacionOutput.builder()
        .investigadores(new ArrayList<>())
        .produccionesCientificas(new ArrayList<>())
        .sexenios(ResumenSexenioOutput.builder().build())
        .costesIndirectos(ResumenCosteIndirectoOutput.builder().build())
        .totales(new ArrayList<>())
        .anio(anio)
        .grupo(grupoId.toString())
        .precioPuntoProduccion(new BigDecimal("0.00"))
        .precioPuntoCostesIndirectos(new BigDecimal("0.00"))
        .precioPuntoSexenio(new BigDecimal("0.00"))
        .build();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-EVAL", "ETI-EVC-INV-EVALR" })
  void getInformeDetalleGrupo_from_json_ReturnsResource() throws Exception {

    // TODO comprobar permisos

    Long grupoId = 1L;
    Integer anio = 2021;

    BDDMockito.given(
        sgiApiPrcService.getDataReportDetalleGrupo(anio, grupoId))
        .willReturn((generarMockDetalleGrupoFromJson("prc/informeDetalleGrupo.json")));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("prc/prpt/rep-prc-detalle-grupo.prpt"));
    BDDMockito.given(sgiApiConfService.getServiceBaseURL()).willReturn("");

    ReportInformeDetalleGrupo report = new ReportInformeDetalleGrupo();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = informeDetalleGrupoReportService.getReportDetalleGrupo(report, anio, grupoId);
    assertNotNull(reportContent);

  }

  private DetalleGrupoInvestigacionOutput generarMockDetalleGrupoFromJson(String outputJsonPath) throws Exception {
    ObjectMapper objectMapper = getObjectMapper();
    return objectMapper.readValue(getJsonFromResources(outputJsonPath), DetalleGrupoInvestigacionOutput.class);
  }

}
