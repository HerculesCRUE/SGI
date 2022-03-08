package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPartidaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

class ConvocatoriaClonerServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository;
  @Mock
  private ConvocatoriaAreaTematicaRepository convocatoriaAreaTematicaRepository;
  @Mock
  private ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  @Mock
  private ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  @Mock
  private ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  @Mock
  private ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  @Mock
  private RequisitoIPRepository requisitoIPRepository;
  @Mock
  private RequisitoIPNivelAcademicoRepository requisitoIPNivelAcademicoRepository;
  @Mock
  private RequisitoIPCategoriaProfesionalRepository requisitoIPCategoriaProfesionalRepository;
  @Mock
  private RequisitoEquipoRepository requisitoEquipoRepository;
  @Mock
  private RequisitoEquipoNivelAcademicoRepository requisitoEquipoNivelAcademicoRepository;
  @Mock
  private RequisitoEquipoCategoriaProfesionalRepository requisitoEquipoCategoriaProfesionalRepository;
  @Mock
  private ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;
  @Mock
  private ConvocatoriaPartidaRepository convocatoriaPartidaRepository;

  private ConvocatoriaClonerService service;

  @BeforeEach
  public void setUp() {
    service = new ConvocatoriaClonerService(
        convocatoriaEntidadGestoraRepository,
        convocatoriaAreaTematicaRepository,
        convocatoriaEntidadConvocanteRepository,
        convocatoriaEntidadFinanciadoraRepository,
        convocatoriaPeriodoJustificacionRepository,
        convocatoriaPeriodoSeguimientoCientificoRepository,
        requisitoIPRepository,
        requisitoIPNivelAcademicoRepository,
        requisitoIPCategoriaProfesionalRepository,
        requisitoEquipoRepository,
        requisitoEquipoNivelAcademicoRepository,
        requisitoEquipoCategoriaProfesionalRepository,
        convocatoriaConceptoGastoRepository,
        convocatoriaConceptoGastoCodigoEcRepository,
        convocatoriaPartidaRepository);
  }


  @Test
  void cloneConvocatoriaAreasTematicas_ShouldCloneConvocatoriaAreaTematica() {
    Long convocatoriaId = 1L;

    ConvocatoriaAreaTematica area = buildMockConvocatoriaAreaTematica(convocatoriaId);

    BDDMockito.given(convocatoriaAreaTematicaRepository.findByConvocatoriaId(anyLong())).willReturn(Optional.of(area));

    ConvocatoriaAreaTematica clonedArea = ConvocatoriaAreaTematica.builder().areaTematica(area.getAreaTematica())
        .convocatoriaId(area.getId()).observaciones(area.getObservaciones()).build();

    BDDMockito.given(convocatoriaAreaTematicaRepository.save(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willReturn(clonedArea);

    service.cloneConvocatoriaAreasTematicas(1L,
        buildMockConvocatoria(convocatoriaId, "testing clone"));

    verify(convocatoriaAreaTematicaRepository, times(1)).save(ArgumentMatchers.<ConvocatoriaAreaTematica>any());
  }

  @Test
  void cloneConvocatoriasEntidadesConvocantes_ShouldCloneConvocatoriaEntidadConvocanteList() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaCloned = 2L;

    List<ConvocatoriaEntidadConvocante> entidades = Arrays.asList(buildMockConvocatoriaEntidadConvocante(1L,
        convocatoriaCloned));

    BDDMockito.given(convocatoriaEntidadConvocanteRepository.findByProgramaIsNotNullAndConvocatoriaId(anyLong()))
        .willReturn(entidades);

    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .save(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any())).willReturn(entidades.get(0));

    service.cloneConvocatoriasEntidadesConvocantes(convocatoriaToCloneId, convocatoriaCloned);
    verify(convocatoriaEntidadConvocanteRepository, times(1))
        .save(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any());
  }

  @Test
  void cloneConvocatoriasEntidadesFinanciadoras_ShouldCloneConvocatoriaEntidadFinanciadoraList() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    List<ConvocatoriaEntidadFinanciadora> entidades = Arrays
        .asList(buildMockConvocatoriaEntidadFinanciadora(convocatoriaClonedId, "entidad-ref-01"));

    BDDMockito.given(convocatoriaEntidadFinanciadoraRepository.findByConvocatoriaId(convocatoriaToCloneId))
        .willReturn(entidades);

    BDDMockito
        .given(convocatoriaEntidadFinanciadoraRepository.save(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any()))
        .willReturn(entidades.get(0));

    service.cloneConvocatoriasEntidadesFinanciadoras(convocatoriaToCloneId, convocatoriaClonedId);

    verify(convocatoriaEntidadFinanciadoraRepository, times(1))
        .save(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any());

  }

  @Test
  void clonePeriodosJustificacion_ShouldCloneConvocatoriaPeriodoJustificacionList() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    List<ConvocatoriaPeriodoJustificacion> justificaciones = Arrays
        .asList(buildMockConvocatoriaPeriodoJustificacion(convocatoriaClonedId));

    BDDMockito.given(convocatoriaPeriodoJustificacionRepository.findAllByConvocatoriaId(anyLong()))
        .willReturn(justificaciones);

    BDDMockito
        .given(
            convocatoriaPeriodoJustificacionRepository.save(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any()))
        .willReturn(justificaciones.get(0));

    service.clonePeriodosJustificacion(convocatoriaToCloneId, convocatoriaClonedId);

    verify(convocatoriaPeriodoJustificacionRepository, times(1)).save(
        ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any());
  }

  @Test
  void cloneConvocatoriaPeriodosSeguimientoCientifico_ShouldCloneConvocatoriaPeriodoSeguimientoCientificoList() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    List<ConvocatoriaPeriodoSeguimientoCientifico>periodos = Arrays.asList(buildMockConvocatoriaPeriodoSeguimientoCientifico(convocatoriaClonedId));
    
    BDDMockito.given(convocatoriaPeriodoSeguimientoCientificoRepository
    .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaToCloneId)).willReturn(periodos);
    BDDMockito.given(convocatoriaPeriodoSeguimientoCientificoRepository.save(periodos.get(0))).willReturn(periodos.get(0));

    service.cloneConvocatoriaPeriodosSeguimientoCientifico(convocatoriaToCloneId, convocatoriaClonedId);

    verify(convocatoriaPeriodoSeguimientoCientificoRepository, times(1)).save(periodos.get(0));

  }

  @Test
  void cloneRequisitoIP_ShouldCloneRequisitoIP() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    RequisitoIP requisitoIp = buildMockRequisitoIP(convocatoriaClonedId);

    List<RequisitoIPNivelAcademico>niveles = Arrays.asList(buildMockRequisitoIPNivelAcademico());
    List<RequisitoIPCategoriaProfesional>categorias = Arrays.asList(buildMockRequisitoIPCategoriaProfesional());

    BDDMockito.given(this.requisitoIPRepository.findById(convocatoriaToCloneId)).willReturn(Optional.of(requisitoIp));
    BDDMockito.given(this.requisitoIPRepository.save(ArgumentMatchers.<RequisitoIP>any())).willReturn(requisitoIp);

    BDDMockito.given(this.requisitoIPNivelAcademicoRepository.findByRequisitoIPId(anyLong())).willReturn(niveles);
    BDDMockito.given(this.requisitoIPNivelAcademicoRepository.save(ArgumentMatchers.<RequisitoIPNivelAcademico>any())).willReturn(niveles.get(0));

    BDDMockito.given(this.requisitoIPCategoriaProfesionalRepository.findByRequisitoIPId(anyLong())).willReturn(categorias);
    BDDMockito.given(this.requisitoIPCategoriaProfesionalRepository.save(ArgumentMatchers.<RequisitoIPCategoriaProfesional>any())).willReturn(categorias.get(0));

    service.cloneRequisitoIP(convocatoriaToCloneId, convocatoriaClonedId);

    verify(this.requisitoIPRepository, times(1)).save(ArgumentMatchers.<RequisitoIP>any());
    verify(this.requisitoIPNivelAcademicoRepository, times(1)).save(ArgumentMatchers.<RequisitoIPNivelAcademico>any());
    verify(this.requisitoIPCategoriaProfesionalRepository, times(1)).save(ArgumentMatchers.<RequisitoIPCategoriaProfesional>any());
  }

  @Test
  void cloneRequisitosEquipo_ShouldCloneRequisitoEquipo() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;
    RequisitoEquipo requisito = buildMockRequisitoEquipo(convocatoriaClonedId);

    BDDMockito.given(this.requisitoEquipoRepository.findByConvocatoriaId(convocatoriaToCloneId)).willReturn(Optional.of(requisito));
    BDDMockito.given(this.requisitoEquipoRepository.save(ArgumentMatchers.<RequisitoEquipo>any())).willReturn(requisito);

    service.cloneRequisitosEquipo(convocatoriaToCloneId, convocatoriaClonedId);

    verify(this.requisitoEquipoRepository, times(1)).save(ArgumentMatchers.<RequisitoEquipo>any());
  }

  @Test
  void cloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc_ShouldCloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;
    List<ConvocatoriaConceptoGasto>gastos = Arrays.asList(buildMockConvocatoriaConceptoGasto(convocatoriaClonedId));
    List<ConvocatoriaConceptoGastoCodigoEc>gastosCodEc = Arrays.asList(buildMockConvocatoriaConceptoGastoCodigoEc(gastos.get(0)));
    
    BDDMockito.given(this.convocatoriaConceptoGastoRepository.findByConvocatoriaId(anyLong())).willReturn(gastos);
    BDDMockito.given(this.convocatoriaConceptoGastoRepository.save(ArgumentMatchers.<ConvocatoriaConceptoGasto>any())).willReturn(gastos.get(0));

    BDDMockito.given(this.convocatoriaConceptoGastoCodigoEcRepository
    .findAllByConvocatoriaConceptoGastoId(anyLong())).willReturn(gastosCodEc);
    BDDMockito.given(this.convocatoriaConceptoGastoCodigoEcRepository.save(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any())).willReturn(gastosCodEc.get(0));

    service.cloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc(convocatoriaToCloneId, convocatoriaClonedId);

    verify(this.convocatoriaConceptoGastoRepository, times(1)).save(ArgumentMatchers.<ConvocatoriaConceptoGasto>any());
    verify(this.convocatoriaConceptoGastoCodigoEcRepository, times(1)).save(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any());
  }

  @Test
  void clonePartidasPresupuestarias_ShouldCloneConvocatoriaPartida() {
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;
    List<ConvocatoriaPartida> partidas = Arrays.asList(buildMockConvocatoriaPartida(convocatoriaClonedId));

    BDDMockito.given(this.convocatoriaPartidaRepository.findByConvocatoriaId(anyLong())).willReturn(partidas);
    BDDMockito.given(this.convocatoriaPartidaRepository.save(ArgumentMatchers.<ConvocatoriaPartida>any())).willReturn(partidas.get(0));

    service.clonePartidasPresupuestarias(convocatoriaToCloneId, convocatoriaClonedId);

    verify(this.convocatoriaPartidaRepository, times(1)).save(ArgumentMatchers.<ConvocatoriaPartida>any());

  }

  private ConvocatoriaAreaTematica buildMockConvocatoriaAreaTematica(Long convocatoriaId) {
    return ConvocatoriaAreaTematica.builder()
        .areaTematica(AreaTematica.builder()
            .activo(Boolean.TRUE)
            .descripcion("Testing")
            .nombre("area-01-test")
            .id(1L)
            .build())
        .convocatoriaId(convocatoriaId)
        .observaciones("Observaciones convocatoria area test")
        .build();
  }

  private Convocatoria buildMockConvocatoria(Long id, String observaciones) {
    return Convocatoria.builder()
        .id(id)
        .observaciones(observaciones)
        .build();
  }

  private ConvocatoriaEntidadConvocante buildMockConvocatoriaEntidadConvocante(Long id, Long convocatoriaId) {
    return ConvocatoriaEntidadConvocante.builder()
        .convocatoriaId(convocatoriaId)
        .entidadRef("entidad01")
        .id(id)
        .programa(Programa.builder().build())
        .build();
  }

  private ConvocatoriaEntidadFinanciadora buildMockConvocatoriaEntidadFinanciadora(Long convocatoriaClonedId,
      String entidadRef) {
    // @formatter: off
    return ConvocatoriaEntidadFinanciadora.builder()
        .convocatoriaId(convocatoriaClonedId)
        .entidadRef(entidadRef)
        .fuenteFinanciacion(FuenteFinanciacion.builder().build())
        .tipoFinanciacion(TipoFinanciacion.builder().build())
        .porcentajeFinanciacion(new BigDecimal(100))
        .importeFinanciacion(new BigDecimal(12000))
        .build();
    // @formatter: on
  }

  private ConvocatoriaPeriodoJustificacion buildMockConvocatoriaPeriodoJustificacion(Long convocatoriaClonedId) {
    //@formatter:off
    return ConvocatoriaPeriodoJustificacion.builder()
        .convocatoriaId(convocatoriaClonedId)
        .fechaFinPresentacion(Instant.now().plusSeconds(35000000))
        .numPeriodo(1)
        .fechaInicioPresentacion(Instant.now())
        .mesFinal(3)
        .mesInicial(1)
        .observaciones("testing")
        .tipo(TipoJustificacion.PERIODICO).build();
    //@formatter:off
  }

  private ConvocatoriaPeriodoSeguimientoCientifico buildMockConvocatoriaPeriodoSeguimientoCientifico(Long convocatoriaClonedId) {
    //@formatter:off
    return ConvocatoriaPeriodoSeguimientoCientifico.builder()
    .convocatoriaId(convocatoriaClonedId)
    .fechaInicioPresentacion(Instant.now())
    .fechaFinPresentacion(Instant.now().plusSeconds(3600000))
    .mesInicial(1)
    .mesFinal(11)
    .numPeriodo(1)
    .tipoSeguimiento(TipoSeguimiento.PERIODICO)
    .observaciones("testing")
    .build();
    //@formatter:on
  }

  private RequisitoIP buildMockRequisitoIP(Long convocatoriaClonedId) {
    //@formatter:off
    return RequisitoIP.builder()
        .id(convocatoriaClonedId)
        .numMaximoIP(10)
        .edadMaxima(55)
        .sexoRef("h")
        .vinculacionUniversidad(Boolean.FALSE)
        .numMinimoCompetitivos(1)
        .numMinimoNoCompetitivos(2)
        .numMaximoCompetitivosActivos(9)
        .numMaximoNoCompetitivosActivos(8)
        .otrosRequisitos("TESTING").build();
    //@formatter:on    
  }

  private RequisitoIPNivelAcademico buildMockRequisitoIPNivelAcademico() {
      return RequisitoIPNivelAcademico.builder()
      .requisitoIPId(1L)
      .nivelAcademicoRef("GRADO")
      .build();
  }

  private RequisitoIPCategoriaProfesional buildMockRequisitoIPCategoriaProfesional() {
      return RequisitoIPCategoriaProfesional.builder()
      .requisitoIPId(1L)
      .categoriaProfesionalRef("GRADO")
      .build();
  }

  private RequisitoEquipo buildMockRequisitoEquipo(Long convocatoriaClonedId) {

    return RequisitoEquipo.builder()
        .edadMaxima(55)
        .sexoRef("M")
        .ratioSexo(2)
        .vinculacionUniversidad(Boolean.FALSE)
        .numMinimoCompetitivos(2)
        .numMinimoNoCompetitivos(2)
        .numMaximoCompetitivosActivos(10)
        .numMaximoNoCompetitivosActivos(3)
        .otrosRequisitos("NO DEFINIDOS")
        .id(convocatoriaClonedId)
        .build();
  }

  private ConvocatoriaConceptoGasto buildMockConvocatoriaConceptoGasto(Long convocatoriaClonedId) {
    return ConvocatoriaConceptoGasto.builder()
        .convocatoriaId(convocatoriaClonedId)
        .id(1L)
        .mesInicial(1)
        .mesFinal(11)
        .importeMaximo(new BigDecimal(11000000).doubleValue())
        .observaciones("testing")
        .permitido(Boolean.TRUE)
        .conceptoGasto(ConceptoGasto.builder().build())
        .build();
  }

  private ConvocatoriaConceptoGastoCodigoEc buildMockConvocatoriaConceptoGastoCodigoEc(
      ConvocatoriaConceptoGasto clonedConvocatoriaConceptoGasto) {
    return ConvocatoriaConceptoGastoCodigoEc.builder()
        .convocatoriaConceptoGastoId(clonedConvocatoriaConceptoGasto.getId())
        .codigoEconomicoRef("AA.AAAA.BBBB.AAAA")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now().plusSeconds(3600000))
        .observaciones("Testing")
        .id(0L)
        .build();
  }

  private ConvocatoriaPartida buildMockConvocatoriaPartida(Long convocatoriaClonedId) {
      return ConvocatoriaPartida.builder()
        .convocatoriaId(convocatoriaClonedId)
        .codigo("COD-001")
        .descripcion("TESTING")
        .tipoPartida(TipoPartida.GASTO)
        .id(1L)
        .build();
  }
}