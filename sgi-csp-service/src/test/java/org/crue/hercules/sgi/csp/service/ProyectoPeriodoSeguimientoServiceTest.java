package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoSeguimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoSeguimientoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoSeguimientoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoPeriodoSeguimientoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoPeriodoSeguimientoServiceTest
 */

class ProyectoPeriodoSeguimientoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoPeriodoSeguimientoRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ProyectoPeriodoSeguimientoDocumentoRepository proyectoPeriodoSeguimientoDocumentoRepository;

  private ProyectoPeriodoSeguimientoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ProyectoPeriodoSeguimientoServiceImpl(repository, proyectoRepository,
        proyectoPeriodoSeguimientoDocumentoRepository);
  }

  @Test
  void create_ReturnsProyectoPeriodoSeguimiento() {
    // given: Un nuevo ProyectoPeriodoSeguimiento
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoPeriodoSeguimiento>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(Page.empty());

    BDDMockito.given(repository.save(proyectoPeriodoSeguimiento)).will((InvocationOnMock invocation) -> {
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCreado = invocation.getArgument(0);
      proyectoPeriodoSeguimientoCreado.setId(1L);
      return proyectoPeriodoSeguimientoCreado;
    });

    // when: Creamos el ProyectoPeriodoSeguimiento
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCreado = service.create(proyectoPeriodoSeguimiento);

    // then: El ProyectoPeriodoSeguimiento se crea correctamente
    Assertions.assertThat(proyectoPeriodoSeguimientoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoPeriodoSeguimientoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoPeriodoSeguimientoCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoPeriodoSeguimiento.getProyectoId());
    Assertions.assertThat(proyectoPeriodoSeguimientoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoPeriodoSeguimiento.getObservaciones());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ProyectoPeriodoSeguimiento que ya tiene id
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    // when: Creamos el ProyectoPeriodoSeguimiento
    // then: Lanza una excepcion porque el ProyectoPeriodoSeguimiento ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(proyectoPeriodoSeguimiento))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ProyectoPeriodoSeguimiento id tiene que ser null para crear un nuevo ProyectoPeriodoSeguimiento");
  }

  @Test
  void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without ProyectoId
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);
    proyectoPeriodoSeguimiento.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para crear ProyectoPeriodoSeguimiento");
  }

  @Test
  void create_WithoutFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaInicio
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);
    proyectoPeriodoSeguimiento.setFechaInicio(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaInicio is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaInicio no puede ser null para crear ProyectoPeriodoSeguimiento");
  }

  @Test
  void create_WithoutFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaFin
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);
    proyectoPeriodoSeguimiento.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaFin is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaFin no puede ser null para crear ProyectoPeriodoSeguimiento");
  }

  @Test
  void create_WithoutFechaInicioPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaInicioPresentacion
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);
    proyectoPeriodoSeguimiento.setFechaFinPresentacion(Instant.now());

    Proyecto proyecto = generarMockProyecto(1L);

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(1L);
    estadoProyecto.setComentario("estado-proyecto-" + String.format("%03d", 1));
    estadoProyecto.setEstado(EstadoProyecto.Estado.CONCEDIDO);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    proyecto.setEstado(estadoProyecto);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaInicioPresentacion is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaInicioPresentacion no puede ser null para crear ProyectoPeriodoSeguimiento");
  }

  @Test
  void create_WithoutFechaFinPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaFinPresentacion
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);
    proyectoPeriodoSeguimiento.setFechaInicioPresentacion(Instant.now());

    Proyecto proyecto = generarMockProyecto(1L);

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(1L);
    estadoProyecto.setComentario("estado-proyecto-" + String.format("%03d", 1));
    estadoProyecto.setEstado(EstadoProyecto.Estado.CONCEDIDO);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    proyecto.setEstado(estadoProyecto);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaFinPresentacion is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaFinPresentacion no puede ser null para crear ProyectoPeriodoSeguimiento");
  }

  @Test
  void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoPeriodoSeguimiento with non existing Proyecto
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void create_WithInvalidFechas_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento WithInvalidFechas
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);
    proyectoPeriodoSeguimiento.setFechaInicio(Instant.parse("2020-10-20T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFin(Instant.parse("2020-10-10T23:59:59Z"));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception WithInvalidFechas
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");

  }

  @Test
  void create_WithInvalidFechasPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento WithInvalidFechas
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setId(null);
    proyectoPeriodoSeguimiento.setFechaInicioPresentacion(Instant.parse("2020-10-20T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFinPresentacion(Instant.parse("2020-10-10T23:59:59Z"));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimiento
        () -> service.create(proyectoPeriodoSeguimiento))
        // then: throw exception WithInvalidFechas
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin de presentación debe ser posterior a la fecha de inicio de presentación");

  }

  @Test
  void update_ReturnsProyectoPeriodoSeguimiento() {
    // given: Un nuevo ProyectoPeriodoSeguimiento con el tipoHito actualizado
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoActualizado = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimientoActualizado.setObservaciones("obs actualizadas");

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimiento));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoPeriodoSeguimiento>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(Page.empty());

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoPeriodoSeguimiento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoPeriodoSeguimiento
    ProyectoPeriodoSeguimiento updated = service.update(proyectoPeriodoSeguimientoActualizado);

    // then: El ProyectoPeriodoSeguimiento se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoPeriodoSeguimiento.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoPeriodoSeguimiento.getProyectoId());
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoPeriodoSeguimientoActualizado.getObservaciones());
  }

  @Test
  void update_WithIdNotExist_ThrowsProyectoPeriodoSeguimientoNotFoundException() {
    // given: Un ProyectoPeriodoSeguimiento a actualizar con un id que no existe
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ProyectoPeriodoSeguimiento
    // then: Lanza una excepcion porque el ProyectoPeriodoSeguimiento no existe
    Assertions.assertThatThrownBy(() -> service.update(proyectoPeriodoSeguimiento))
        .isInstanceOf(ProyectoPeriodoSeguimientoNotFoundException.class);
  }

  @Test
  void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without ProyectoId
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");
    proyectoPeriodoSeguimiento.setProyectoId(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para actualizar ProyectoPeriodoSeguimiento");
  }

  @Test
  void update_WithoutFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaInicio
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");
    proyectoPeriodoSeguimiento.setFechaInicio(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaInicio is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaInicio no puede ser null para actualizar ProyectoPeriodoSeguimiento");
  }

  @Test
  void update_WithoutFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaFin
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");
    proyectoPeriodoSeguimiento.setFechaFin(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaFin is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaFin no puede ser null para actualizar ProyectoPeriodoSeguimiento");
  }

  @Test
  void update_WithoutFechaInicioPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaInicioPresentacion
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");
    proyectoPeriodoSeguimiento.setFechaFinPresentacion(Instant.now());

    Proyecto proyecto = generarMockProyecto(1L);

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(1L);
    estadoProyecto.setComentario("estado-proyecto-" + String.format("%03d", 1));
    estadoProyecto.setEstado(EstadoProyecto.Estado.CONCEDIDO);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    proyecto.setEstado(estadoProyecto);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaInicioPresentacion is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaInicioPresentacion no puede ser null para actualizar ProyectoPeriodoSeguimiento");
  }

  @Test
  void update_WithoutFechaFinPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento without FechaFinPresentacion
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");
    proyectoPeriodoSeguimiento.setFechaInicioPresentacion(Instant.now());

    Proyecto proyecto = generarMockProyecto(1L);

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(1L);
    estadoProyecto.setComentario("estado-proyecto-" + String.format("%03d", 1));
    estadoProyecto.setEstado(EstadoProyecto.Estado.CONCEDIDO);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    proyecto.setEstado(estadoProyecto);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception as FechaFinPresentacion is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("FechaFinPresentacion no puede ser null para actualizar ProyectoPeriodoSeguimiento");
  }

  @Test
  void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoPeriodoSeguimiento with non existing Proyecto
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void update_WithInvalidFechas_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento WithInvalidFechas
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");
    proyectoPeriodoSeguimiento.setFechaInicio(Instant.parse("2020-10-20T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFin(Instant.parse("2020-10-10T23:59:59Z"));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception WithInvalidFechas
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");

  }

  @Test
  void update_WithInvalidFechasPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimiento WithInvalidFechas
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoOriginal = generarMockProyectoPeriodoSeguimiento(1L);
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = generarMockProyectoPeriodoSeguimiento(1L);
    proyectoPeriodoSeguimiento.setObservaciones("observaciones actualizar");
    proyectoPeriodoSeguimiento.setFechaInicioPresentacion(Instant.parse("2020-10-20T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFinPresentacion(Instant.parse("2020-10-10T23:59:59Z"));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimiento
        () -> service.update(proyectoPeriodoSeguimiento))
        // then: throw exception WithInvalidFechas
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin de presentación debe ser posterior a la fecha de inicio de presentación");

  }

  @Test
  void updateFechaPresentacionDocumentacion_WithIdNull_ThrowsIllegalArgumentException() {
    // given: a proyectoPeriodoSeguimientoId null
    Long proyectoPeriodoSeguimientoId = null;

    Assertions.assertThatThrownBy(
        // when: updateFechaPresentacionDocumentacion ProyectoPeriodoSeguimiento
        () -> service.updateFechaPresentacionDocumentacion(proyectoPeriodoSeguimientoId, null))
        // then: throw exception WithIdNull
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void updateFechaPresentacionDocumentacion_WithNotExistingId_ThrowsIllegalArgumentException() {
    // given: a not existing proyectoPeriodoSeguimientoId
    Long proyectoPeriodoSeguimientoId = 999L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: updateFechaPresentacionDocumentacion ProyectoPeriodoSeguimiento
        () -> service.updateFechaPresentacionDocumentacion(proyectoPeriodoSeguimientoId, null))
        // then: throw exception ProyectoPeriodoSeguimientoNotFoundException
        .isInstanceOf(ProyectoPeriodoSeguimientoNotFoundException.class);
  }

  @Test
  void updateFechaPresentacionDocumentacion_ReturnsProyectoPeriodoSeguimiento() {
    // given: a existing proyectoPeriodoSeguimientoId
    Long proyectoPeriodoSeguimientoId = 1L;
    Instant fechaPresentacionDocumentacionToUpdate = Instant.parse("2020-10-10T23:59:59Z");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoPeriodoSeguimiento(proyectoPeriodoSeguimientoId)));
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoPeriodoSeguimiento>any()))
        .will((InvocationOnMock invocation) -> {
          ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoUpdated = invocation.getArgument(0,
              ProyectoPeriodoSeguimiento.class);
          return proyectoPeriodoSeguimientoUpdated;
        });

    // when: updateFechaPresentacionDocumentacion ProyectoPeriodoSeguimiento
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoUpdated = service
        .updateFechaPresentacionDocumentacion(proyectoPeriodoSeguimientoId, fechaPresentacionDocumentacionToUpdate);
    // then: ProyectoPeriodoSeguimiento fechaPresentacionDocumentacion updated
    Assertions.assertThat(proyectoPeriodoSeguimientoUpdated).isNotNull();
    Assertions.assertThat(proyectoPeriodoSeguimientoUpdated.getFechaPresentacionDocumentacion())
        .as("getFechaPresentacionDocumentacion()").isEqualTo(fechaPresentacionDocumentacionToUpdate);
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoPeriodoSeguimiento
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository
        .existsByProyectoPeriodoSeguimientoId(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(proyectoPeriodoSeguimientoDocumentoRepository)
        .deleteByProyectoPeriodoSeguimientoId(ArgumentMatchers.<Long>any());
    BDDMockito.given(repository.findById(id)).willReturn(Optional.of(generarMockProyectoPeriodoSeguimiento(id)));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoPeriodoSeguimientoNotFoundException.class);
  }

  @Test
  void existsById_WithExistingId_ReturnsTRUE() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  void existsById_WithNoExistingId_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  void findById_ReturnsProyectoPeriodoSeguimiento() {
    // given: Un ProyectoPeriodoSeguimiento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoPeriodoSeguimiento(idBuscado)));

    // when: Buscamos el ProyectoPeriodoSeguimiento por su id
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = service.findById(idBuscado);

    // then: el ProyectoPeriodoSeguimiento
    Assertions.assertThat(proyectoPeriodoSeguimiento).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoPeriodoSeguimiento.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoPeriodoSeguimientoNotFoundException() throws Exception {
    // given: Ningun ProyectoPeriodoSeguimiento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoPeriodoSeguimiento por su id
    // then: lanza un ProyectoPeriodoSeguimientoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoPeriodoSeguimientoNotFoundException.class);
  }

  @Test
  void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoPeriodoSeguimiento para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoPeriodoSeguimiento> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoPeriodoSeguimiento(Long.valueOf(i)));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoPeriodoSeguimiento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size() ? proyectosEntidadesConvocantes.size() : toIndex;
          List<ProyectoPeriodoSeguimiento> content = proyectosEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ProyectoPeriodoSeguimiento> pageResponse = new PageImpl<>(content, pageable,
              proyectosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoPeriodoSeguimiento> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoPeriodoSeguimiento del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoPeriodoSeguimiento.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  void findAllByProyectoSgeRef_ReturnsPage() {
    // given: Una lista con 37 ProyectoPeriodoSeguimiento
    String proyectosSgeRef = "1";
    List<ProyectoPeriodoSeguimiento> periodosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      periodosJustificacion.add(generarMockProyectoPeriodoSeguimiento(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<ProyectoPeriodoSeguimiento>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimiento>>() {
          @Override
          public Page<ProyectoPeriodoSeguimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > periodosJustificacion.size() ? periodosJustificacion.size() : toIndex;
            List<ProyectoPeriodoSeguimiento> content = periodosJustificacion.subList(fromIndex, toIndex);
            Page<ProyectoPeriodoSeguimiento> page = new PageImpl<>(content, pageable, periodosJustificacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoPeriodoSeguimiento> page = service.findAllByProyectoSgeRef(proyectosSgeRef, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().hasSize()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoSeguimiento proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("obs-" + String.format("%03d", i));
    }
  }

  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id == null ? 1 : id);
    estadoProyecto.setComentario("estado-proyecto-" + String.format("%03d", id == null ? 1 : id));
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id == null ? 1 : id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-" + String.format("%03d", id));
    proyecto.setUnidadGestionRef("2");
    proyecto.setFechaInicio(Instant.parse("2020-01-01T00:00:00Z"));
    proyecto.setFechaFin(Instant.parse("2021-01-01T23:59:59Z"));
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setActivo(true);
    proyecto.setEstado(estadoProyecto);

    return proyecto;
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimiento
   * 
   * @param id id del ProyectoPeriodoSeguimiento
   * @return el objeto ProyectoPeriodoSeguimiento
   */
  private ProyectoPeriodoSeguimiento generarMockProyectoPeriodoSeguimiento(Long id) {
    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = new ProyectoPeriodoSeguimiento();
    proyectoPeriodoSeguimiento.setId(id);
    proyectoPeriodoSeguimiento.setProyectoId(id == null ? 1 : id);
    proyectoPeriodoSeguimiento.setNumPeriodo(1);
    proyectoPeriodoSeguimiento.setFechaInicio(Instant.parse("2020-10-19T00:00:00Z"));
    proyectoPeriodoSeguimiento.setFechaFin(Instant.parse("2020-12-19T23:59:59Z"));
    proyectoPeriodoSeguimiento.setObservaciones("obs-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimiento.setTipoSeguimiento(TipoSeguimiento.FINAL);

    return proyectoPeriodoSeguimiento;
  }
}
