package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseAvisoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseInput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.EstadoProyectoComentario;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.model.ProyectoFaseObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoFaseServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoFaseServiceTest
 */

class ProyectoFaseServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoFaseRepository repository;

  @Mock
  private ProyectoRepository proyectoRepository;

  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;

  @Mock
  private TipoFaseRepository tipoFaseRepository;

  @Mock
  private ProyectoFaseAvisoService proyectoFaseAvisoService;

  private ProyectoFaseService service;

  @BeforeEach
  void setUp() {
    service = new ProyectoFaseServiceImpl(repository, proyectoRepository, modeloTipoFaseRepository, tipoFaseRepository,
        proyectoFaseAvisoService);
  }

  @Test
  void create_ReturnsProyectoFase() {
    // given: Un nuevo ProyectoFase
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFase = this.generarMockProyectoFase(1L);
    proyectoFase.setId(1L);
    proyectoFase.setProyectoFaseAviso1(this.buildMockProyectoFaseAviso(1L));

    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE);
    TipoFase tipoFase = modeloTipoFase.getTipoFase();

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setAviso2(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          return new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
        });

    BDDMockito.given(this.tipoFaseRepository.findById(1L)).willReturn(Optional.of(tipoFase));

    BDDMockito.given(this.proyectoFaseAvisoService.create(1L, input.getAviso1()))
        .willReturn(proyectoFase.getProyectoFaseAviso1());

    BDDMockito.given(this.proyectoFaseAvisoService.create(1L, input.getAviso2()))
        .willReturn(null);

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoFase>any())).willReturn(proyectoFase);

    // when: Creamos el ProyectoFase
    ProyectoFase proyectoFaseCreado = service.create(input);

    // then: El ProyectoFase se crea correctamente
    Assertions.assertThat(proyectoFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoFase.getProyectoId());
    Assertions.assertThat(proyectoFaseCreado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoFase.getFechaInicio());
    Assertions.assertThat(proyectoFaseCreado.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoFase.getFechaFin());
    Assertions.assertThat(proyectoFaseCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoFase.getObservaciones());
    Assertions.assertThat(proyectoFaseCreado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFase.getTipoFase().getId());
    Assertions.assertThat(proyectoFaseCreado.getProyectoFaseAviso1()).as("getProyectoFaseAviso1()")
        .isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getProyectoFaseAviso2()).as("getProyectoFaseAviso2()")
        .isNull();
  }

  @Test
  void create_WithRangoFechasAnterior_ReturnsProyectoFaseWithGeneraAvisoFalse() {
    // given: Un nuevo ProyectoFase con rango de fechas pasado
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(1L);

    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE);
    TipoFase tipoFase = modeloTipoFase.getTipoFase();

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setFechaInicio(Instant.now().minus(Period.ofDays(2)));
    input.setFechaFin(Instant.now().minus(Period.ofDays(1)));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));

    BDDMockito.given(this.tipoFaseRepository.findById(1L)).willReturn(Optional.of(tipoFase));

    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          return new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
        });

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoFase>any())).willReturn(proyectoFase);

    // when: Creamos el ProyectoFase
    ProyectoFase proyectoFaseCreado = service.create(input);

    // then: El ProyectoFase se crea correctamente
    Assertions.assertThat(proyectoFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoFase.getProyectoId());
    Assertions.assertThat(proyectoFaseCreado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoFase.getFechaInicio());
    Assertions.assertThat(proyectoFaseCreado.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoFase.getFechaFin());
    Assertions.assertThat(proyectoFaseCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoFase.getObservaciones());
    Assertions.assertThat(proyectoFaseCreado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFase.getTipoFase().getId());
  }

  @Test
  void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without ProyectoId
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Proyecto no puede ser nulo");
  }

  @Test
  void create_WithoutTipoFaseId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without TipoFaseId
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setTipoFaseId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as TipoFaseId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo Fase no puede ser nulo");
  }

  @Test
  void create_WithoutFechas_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without Fecha
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setFechaFin(null);
    input.setFechaInicio(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe indicarse al menos una fecha para realizar la acción sobre ProyectoFase");
  }

  @Test
  void create_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: Fecha Inicio > fechaFin
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setFechaInicio(input.getFechaFin().plus(Period.ofDays(1)));
    // when: create ProyectoFase
    // then: throw exception as Inicio > fechaFin
    Assertions.assertThatThrownBy(() -> service.create(input)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de inicio no puede ser superior a la fecha de fin");
  }

  @Test
  void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoFase with non existing Proyecto
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void create_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con Proyecto sin Modelo de Ejecucion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    proyecto.setModeloEjecucion(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Fase no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void create_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con TipoFase no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Fase no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void create_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la proyecto inactiva
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(1L, generarMockProyectoFase(1L), Boolean.FALSE);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Tipo Fase no está activo para el modelo ejecución %s",
            modeloTipoFase.getTipoFase().getNombre(), proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void create_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase TipoFase disabled
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.getTipoFase().setActivo(Boolean.FALSE);
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(input))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Tipo Fase no está activo", proyectoFase.getTipoFase().getNombre());
  }

  @Test
  void create_WithExistingDateOverlap_ThrowsIllegalArgumentException() {
    // given: a existing ProyectoFase with date ranges overlapping
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFaseExistente = generarMockProyectoFase(2L);
    proyectoFaseExistente.setFechaInicio(Instant.parse("2020-10-18T00:00:00Z"));
    proyectoFaseExistente.setFechaFin(Instant.parse("2020-10-22T23:59:59Z"));

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setFechaInicio(Instant.parse("2020-10-18T00:00:00Z"));
    input.setFechaFin(Instant.parse("2020-10-22T23:59:59Z"));

    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          return new PageImpl<>(Arrays.asList(proyectoFaseExistente), pageable, 0);
        });

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(input))
        // then: throw exception as date overlaps
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Proyecto Fase Tipo Fase ya está presente y tiene un periodo de vigencia que se solapa con el indicado");
  }

  @Test
  void update_ReturnsProyectoFase() {
    // given: Un nuevo ProyectoFase con el tipoFase actualizado
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    ProyectoFase proyectoFaseActualizado = generarMockProyectoFase(1L);
    proyectoFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    TipoFase tipoFase = proyectoFase.getTipoFase();
    tipoFase.setId(2L);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setTipoFaseId(2L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFase));

    BDDMockito.given(this.tipoFaseRepository.findById(anyLong())).willReturn(Optional.of(tipoFase));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFaseActualizado, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          return new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
        });

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoFase
    ProyectoFase updated = service.update(1L, input);

    // then: El ProyectoFase se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoFase.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()").isEqualTo(proyectoFase.getProyectoId());
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getObservaciones(), Language.ES))
        .as("getObservaciones()")
        .isEqualTo(I18nHelper.getValueForLanguage(proyectoFaseActualizado.getObservaciones(), Language.ES));
    Assertions.assertThat(updated.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFaseActualizado.getTipoFase().getId());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(input.getFechaInicio());
    Assertions.assertThat(updated.getFechaFin()).as("getFechaFin()").isEqualTo(input.getFechaFin());
  }

  @Test
  void update_WithRangoFechasAnterior_ReturnsProyectoFaseWithGeneraAvisoFalse() {
    // given: Un nuevo ProyectoFase con el tipoFase actualizado
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    ProyectoFase proyectoFaseActualizado = generarMockProyectoFase(1L);
    TipoFase tipoFase = generarMockTipoFase(2L, Boolean.TRUE);

    proyectoFaseActualizado.setTipoFase(tipoFase);
    proyectoFaseActualizado.setFechaInicio(Instant.now().minus(Period.ofDays(2)));
    proyectoFaseActualizado.setFechaFin(Instant.now().minus(Period.ofDays(1)));

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setTipoFaseId(2L);
    input.setFechaInicio(Instant.now().minus(Period.ofDays(2)));
    input.setFechaFin(Instant.now().minus(Period.ofDays(1)));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFase));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(this.tipoFaseRepository.findById(anyLong())).willReturn(Optional.of(tipoFase));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFaseActualizado, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          return new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
        });

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoFase
    ProyectoFase updated = service.update(1L, input);

    // then: El ProyectoFase se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoFase.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()").isEqualTo(proyectoFase.getProyectoId());
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getObservaciones(), Language.ES))
        .as("getObservaciones()")
        .isEqualTo(I18nHelper.getValueForLanguage(input.getObservaciones(), Language.ES));
    Assertions.assertThat(updated.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(input.getTipoFaseId());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(input.getFechaInicio());
    Assertions.assertThat(updated.getFechaFin()).as("getFechaFin()").isEqualTo(input.getFechaFin());
  }

  @Test
  void update_WithIdNotExist_ThrowsProyectoFaseNotFoundException() {
    // given: Un ProyectoFase a actualizar con un id que no existe
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ProyectoFase
    // then: Lanza una excepcion porque el ProyectoFase no existe
    Assertions.assertThatThrownBy(() -> service.update(1L, input)).isInstanceOf(ProyectoFaseNotFoundException.class);
  }

  @Test
  void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without ProyectoId
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    List<I18nFieldValueDto> proyectoFaseObservaciones = new ArrayList<I18nFieldValueDto>();
    proyectoFaseObservaciones.add(new I18nFieldValueDto(Language.ES, "observaciones modificado"));
    input.setObservaciones(proyectoFaseObservaciones);
    input.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Proyecto no puede ser nulo");
  }

  @Test
  void update_WithoutTipoFaseId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without TipoFaseId
    Set<ProyectoFaseObservaciones> proyectoFaseObservaciones = new HashSet<>();
    proyectoFaseObservaciones
        .add(new ProyectoFaseObservaciones(Language.ES,
            "observaciones modificado"));

    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones(proyectoFaseObservaciones);
    proyectoFase.setTipoFase(null);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setTipoFaseId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as TipoFaseId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo Fase no puede ser nulo");
  }

  @Test
  void update_WithoutFecha_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without Fecha
    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setFechaInicio(null);
    input.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe indicarse al menos una fecha para realizar la acción sobre ProyectoFase");
  }

  @Test
  void update_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: Fecha Inicio > fechaFin
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);
    input.setFechaInicio(input.getFechaFin().plus(Period.ofDays(1)));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as Inicio > fechaFin
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de inicio no puede ser superior a la fecha de fin");
  }

  @Test
  void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoFase with non existing Proyecto
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void update_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con Proyecto sin Modelo de Ejecucion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);

    proyecto.setModeloEjecucion(null);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Fase no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void update_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con TipoFase no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Fase no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void update_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la proyecto inactiva
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Tipo Fase no está activo para el modelo ejecución %s",
            proyectoFase.getTipoFase().getNombre(), proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void update_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase TipoFase disabled
    Set<ProyectoFaseObservaciones> proyectoFaseObservaciones = new HashSet<>();
    proyectoFaseObservaciones
        .add(new ProyectoFaseObservaciones(Language.ES,
            "observaciones modificado"));

    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones(proyectoFaseObservaciones);
    proyectoFase.getTipoFase().setActivo(Boolean.FALSE);

    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE);
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);

    ProyectoFaseInput input = generarMockProyectoFaseInput(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Tipo Fase no está activo", proyectoFase.getTipoFase().getNombre());
  }

  @Test
  void update_WithExistingDateOverlap_ThrowsIllegalArgumentException() {
    // given: a existing ProyectoFase with date ranges overlapping
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoFase proyectoFaseExistente = generarMockProyectoFase(2L);
    proyectoFaseExistente.setFechaInicio(Instant.parse("2020-10-18T00:00:00Z"));
    proyectoFaseExistente.setFechaFin(Instant.parse("2020-10-22T23:59:59Z"));

    Set<ProyectoFaseObservaciones> proyectoFaseObservaciones = new HashSet<>();
    proyectoFaseObservaciones
        .add(new ProyectoFaseObservaciones(Language.ES,
            "observaciones modificado"));

    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones(proyectoFaseObservaciones);

    ProyectoFaseInput input = this.generarMockProyectoFaseInput(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          return new PageImpl<>(Arrays.asList(proyectoFaseExistente), pageable, 0);
        });

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(1L, input))
        // then: throw exception as date overlaps
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Proyecto Fase Tipo Fase ya está presente y tiene un periodo de vigencia que se solapa con el indicado");
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoFase
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(this.generarMockProyectoFase(1L)));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoFaseNotFoundException.class);
  }

  @Test
  void findById_ReturnsProyectoFase() {
    // given: Un ProyectoFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockProyectoFase(idBuscado)));

    // when: Buscamos el ProyectoFase por su id
    ProyectoFase proyectoFase = service.findById(idBuscado);

    // then: el ProyectoFase
    Assertions.assertThat(proyectoFase).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoFase.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoFaseNotFoundException() {
    // given: Ningun ProyectoFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoFase por su id
    // then: lanza un ProyectoFaseNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ProyectoFaseNotFoundException.class);
  }

  @Test
  void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoFase para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoFase> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoFase(Long.valueOf(i)));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size() ? proyectosEntidadesConvocantes.size() : toIndex;
          List<ProyectoFase> content = proyectosEntidadesConvocantes.subList(fromIndex, toIndex);
          return new PageImpl<>(content, pageable, proyectosEntidadesConvocantes.size());
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoFase> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoFase del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoFase proyectoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoFase.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-modelo-ejecucion"));

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);
    modeloEjecucion.setNombre(nombreModeloEjecucion);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "PRO" + (id != null ? id : 1)));

    Set<ProyectoObservaciones> observacionesProyecto = new HashSet<>();
    observacionesProyecto
        .add(new ProyectoObservaciones(Language.ES, "observaciones-proyecto-" + String.format("%03d", id)));

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo(tituloProyecto);
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones(observacionesProyecto);
    proyecto.setUnidadGestionRef("2");
    proyecto.setFechaInicio(Instant.now());
    proyecto.setFechaFin(Instant.now());
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setActivo(true);

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    Set<EstadoProyectoComentario> estadoProyectoComentario = new HashSet<>();
    estadoProyectoComentario.add(new EstadoProyectoComentario(Language.ES, "estado-proyecto-" + String.format("%03d", id)));

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario(estadoProyectoComentario);
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    return estadoProyecto;
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id     id del TipoFase
   * @param activo
   * @return el objeto TipoFase
   */
  private TipoFase generarMockTipoFase(Long id, Boolean activo) {
    Set<TipoFaseNombre> nombreTipoFase = new HashSet<>();
    nombreTipoFase.add(new TipoFaseNombre(Language.ES, "nombre-fase-" + String.format("%03d", id)));

    Set<TipoFaseDescripcion> descripcionTipoFase = new HashSet<>();
    descripcionTipoFase.add(new TipoFaseDescripcion(Language.ES, "descripcion-fase-" + String.format("%03d", id)));

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombreTipoFase);
    tipoFase.setDescripcion(descripcionTipoFase);
    tipoFase.setActivo(activo);

    return tipoFase;
  }

  /**
   * Función que genera ModeloTipoFase a partir de un objeto ProyectoFase
   * 
   * @param id
   * @param proyectoFase
   * @param activo
   * @return
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, ProyectoFase proyectoFase, Boolean activo) {

    // @formatter:off
    return ModeloTipoFase.builder()
        .id(id)
        .modeloEjecucion(generarMockProyecto(proyectoFase.getProyectoId()).getModeloEjecucion())
        .tipoFase(proyectoFase.getTipoFase())
        .activo(activo)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ProyectoFase
   * 
   * @param id id del ProyectoFase
   * @return el objeto ProyectoFase
   */
  private ProyectoFase generarMockProyectoFase(Long id) {

    Set<ProyectoFaseObservaciones> proyectoFaseObservaciones = new HashSet<>();
    proyectoFaseObservaciones
        .add(new ProyectoFaseObservaciones(Language.ES,
            "observaciones-proyecto-fase-" + (id == null ? "" : String.format("%03d", id))));

    // @formatter:off
    return ProyectoFase.builder()
        .id(id)
        .proyectoId(1L)
        .fechaInicio(Instant.parse("2020-10-19T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-20T00:00:00Z"))
        .observaciones(proyectoFaseObservaciones)
        .proyectoFaseAviso1(buildMockProyectoFaseAviso(1L))
        .proyectoFaseAviso2(buildMockProyectoFaseAviso(2L))
        .tipoFase(generarMockTipoFase(1L, Boolean.TRUE))
        .build();
    // @formatter:on
  }

  private ProyectoFaseInput generarMockProyectoFaseInput(Long id) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);
    tipoFase.setActivo(true);

    List<I18nFieldValueDto> proyectoFaseObservaciones = new ArrayList<I18nFieldValueDto>();
    proyectoFaseObservaciones.add(new I18nFieldValueDto(Language.ES,
        "observaciones-proyecto-fase-" + String.format("%03d", id)));

    ProyectoFaseInput proyectoFase = new ProyectoFaseInput();
    proyectoFase.setProyectoId(id == null ? 1 : id);
    proyectoFase.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoFase.setFechaFin(Instant.parse("2020-10-20T23:59:59Z"));
    proyectoFase.setObservaciones(proyectoFaseObservaciones);
    proyectoFase.setAviso1(buildMockProyectoFaseAvisoInput());
    proyectoFase.setAviso2(buildMockProyectoFaseAvisoInput());
    proyectoFase.setTipoFaseId(tipoFase.getId());

    return proyectoFase;
  }

  private ProyectoFaseAviso buildMockProyectoFaseAviso(Long id) {
    return ProyectoFaseAviso.builder()
        .comunicadoRef("3333")
        .id(id)
        .tareaProgramadaRef("666")
        .build();
  }

  private ProyectoFaseAvisoInput buildMockProyectoFaseAvisoInput() {
    List<ProyectoFaseAvisoInput.Destinatario> destinatarios = new LinkedList<>();
    destinatarios.add(ProyectoFaseAvisoInput.Destinatario.builder()
        .email("testing@um.com")
        .nombre("manolo")
        .build());

    return ProyectoFaseAvisoInput.builder()
        .asunto("asunto")
        .contenido("contenido mail")
        .destinatarios(destinatarios)
        .incluirIpsProyecto(true)
        .fechaEnvio(Instant.now().plus(15, ChronoUnit.MINUTES))
        .build();
  }
}
