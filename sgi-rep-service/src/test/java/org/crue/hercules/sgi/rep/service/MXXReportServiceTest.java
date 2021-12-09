package org.crue.hercules.sgi.rep.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportMXX;
import org.crue.hercules.sgi.rep.dto.eti.TipoEstadoMemoriaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.eti.ApartadoService;
import org.crue.hercules.sgi.rep.service.eti.BloqueService;
import org.crue.hercules.sgi.rep.service.eti.MXXReportService;
import org.crue.hercules.sgi.rep.service.eti.MemoriaService;
import org.crue.hercules.sgi.rep.service.eti.PeticionEvaluacionService;
import org.crue.hercules.sgi.rep.service.eti.RespuestaService;
import org.crue.hercules.sgi.rep.service.eti.SgiFormlyService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * MXXReportServiceTest
 */
class MXXReportServiceTest extends BaseReportServiceTest {

  private MXXReportService mxxReportService;

  @MockBean
  private SgiConfigProperties sgiConfigProperties;

  @MockBean
  private MemoriaService memoriaService;

  @MockBean
  private PeticionEvaluacionService peticionEvaluacionService;

  @Mock
  private PersonaService personaService;

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
    mxxReportService = new MXXReportService(sgiConfigProperties, memoriaService,
        peticionEvaluacionService, personaService, bloqueService,
        apartadoService, sgiFormlyService, respuestaService);
  }

  @Test
  void getMXX_ReturnsMemoriaValidationException() throws Exception {

    ReportMXX report = new ReportMXX();
    report.setOutputType(OutputType.PDF);

    Assertions.assertThatThrownBy(() -> mxxReportService.getReportMXX(report, null, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getMXX_ReturnsFormularioValidationException() throws Exception {

    ReportMXX report = new ReportMXX();
    report.setOutputType(OutputType.PDF);

    Assertions.assertThatThrownBy(() -> mxxReportService.getReportMXX(report, 1L, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getMXX_ReturnsFormularioGetDataReportException() throws Exception {

    Long idMemoria = 26L;
    Long idFormulario = 3L;

    ReportMXX report = new ReportMXX();
    report.setOutputType(OutputType.PDF);

    BDDMockito.given(memoriaService.findById(idMemoria)).willReturn((generarMockMemoria(idMemoria, idFormulario)));
    BDDMockito.given(personaService.findById("")).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(personaService.findDatosContactoByPersonaId("")).willReturn((generarMockDatosContacto()));
    BDDMockito.given(personaService.findVinculacionByPersonaId("")).willReturn((generarMockVinculacion()));
    BDDMockito.given(bloqueService.findByFormularioId(idFormulario)).willReturn((generarMockBloques()));
    BDDMockito.given(apartadoService.findByBloqueId(1L)).willReturn((generarMockApartados(Arrays.asList(1L, 2L),
        1L)));
    BDDMockito.given(apartadoService.findByBloqueId(2L)).willReturn((generarMockApartados(Arrays.asList(3L, 4L),
        1L)));
    BDDMockito.given(apartadoService.findByPadreId(1L)).willReturn((generarMockApartados(Arrays.asList(10L, 11L), 1L)));
    BDDMockito.given(
        peticionEvaluacionService.findMemoriaByPeticionEvaluacionMaxVersion(1L))
        .willReturn(generarMockMemoriaPeticionEvaluacionDto(idMemoria));

    Assertions.assertThatThrownBy(() -> mxxReportService.getReportMXX(report,
        idMemoria,
        idFormulario))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-MEM-INV-ESCR", "ETI-MEM-INV-ERTR" })
  void getMXX_ReturnsResource() throws Exception {
    Long idMemoria = 26L;
    Long idFormulario = 3L;

    BDDMockito.given(memoriaService.findById(idMemoria)).willReturn((generarMockMemoria(idMemoria, idFormulario)));
    BDDMockito.given(personaService.findById(null)).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(personaService.findById("")).willReturn((generarMockPersona("123456F")));
    BDDMockito.given(personaService.findDatosContactoByPersonaId(null)).willReturn((generarMockDatosContacto()));
    BDDMockito.given(personaService.findVinculacionByPersonaId(null)).willReturn((generarMockVinculacion()));
    BDDMockito.given(
        peticionEvaluacionService.findMemoriaByPeticionEvaluacionMaxVersion(1L))
        .willReturn(generarMockMemoriaPeticionEvaluacionDto(idMemoria));

    ReportMXX report = new ReportMXX();
    report.setOutputType(OutputType.PDF);

    byte[] reportContent = mxxReportService.getReportMXX(report, idMemoria, idFormulario);
    assertNotNull(reportContent);

  }

  private List<MemoriaPeticionEvaluacionDto> generarMockMemoriaPeticionEvaluacionDto(Long idMemoria) {
    List<MemoriaPeticionEvaluacionDto> memorias = new ArrayList<>();
    memorias.add(MemoriaPeticionEvaluacionDto.builder()
        .comite(generarMockComite(1L, "CEI"))
        .estadoActual(TipoEstadoMemoriaDto.builder().id(1L).nombre("BORRADOR").build())
        .build());
    return memorias;
  }

}
