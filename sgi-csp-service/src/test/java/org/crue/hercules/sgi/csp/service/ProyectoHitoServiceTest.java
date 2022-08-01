package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoInput;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoHitoServiceImpl;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
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
 * ProyectoHitoServiceTest
 */

class ProyectoHitoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoHitoRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ModeloTipoHitoRepository modeloTipoHitoRepository;
  @Mock
  private SgiApiComService emailService;
  @Mock
  private SgiApiTpService sgiApiTaskService;
  @Mock
  private ProyectoHitoAvisoRepository proyectoHitoAvisoRepository;
  @Mock
  private SgiApiSgpService personaService;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoReposiotry;
  @Mock
  private TipoHitoRepository tipoHitoRepository;

  private ProyectoHitoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ProyectoHitoServiceImpl(repository,
        proyectoRepository,
        modeloTipoHitoRepository,
        emailService,
        sgiApiTaskService,
        proyectoHitoAvisoRepository,
        personaService,
        proyectoEquipoReposiotry,
        tipoHitoRepository);
  }

  @Test
  void create_ReturnsProyectoHito() {
    // given: Un nuevo ProyectoHito
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    ProyectoHito created = this.generarMockProyectoHito(1L);
    TipoHito tipoHito = this.generarMockTipoHito(1L, Boolean.TRUE);
    ModeloTipoHito modeloTipoHito = this.generarMockModeloTipoHito(1L, created, Boolean.TRUE);
    created.setTipoHito(tipoHito);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any())).willReturn(created);

    // when: Creamos el ProyectoHito
    ProyectoHito proyectoHitoCreado = service.create(proyectoHito);

    // then: El ProyectoHito se crea correctamente
    Assertions.assertThat(proyectoHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoHito.getProyectoId());
    Assertions.assertThat(proyectoHitoCreado.getFecha()).as("getFecha()")
        .isEqualTo(proyectoHito.getFecha());
    Assertions.assertThat(proyectoHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(proyectoHito.getComentario());
    Assertions.assertThat(proyectoHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(proyectoHito.getTipoHitoId());
  }

  @Test
  void create_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ProyectoHito con fecha pasada
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    ProyectoHito created = this.generarMockProyectoHito(1L);
    TipoHito tipoHito = this.generarMockTipoHito(1L, Boolean.TRUE);
    ModeloTipoHito modeloTipoHito = this.generarMockModeloTipoHito(1L, created, Boolean.TRUE);
    created.setTipoHito(tipoHito);
    proyectoHito.setFecha(Instant.now().minus(Period.ofDays(2)));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any())).willReturn(created);

    // when: Creamos el ProyectoHito
    ProyectoHito proyectoHitoCreado = service.create(proyectoHito);

    // then: El ProyectoHito se crea correctamente con GenerarAviso como FALSE
    Assertions.assertThat(proyectoHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(created.getProyectoId());
    Assertions.assertThat(proyectoHitoCreado.getFecha()).as("getFecha()")
        .isEqualTo(created.getFecha());
    Assertions.assertThat(proyectoHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(created.getComentario());
    Assertions.assertThat(proyectoHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(created.getTipoHito().getId());
    Assertions.assertThat(proyectoHitoCreado.getProyectoHitoAviso()).as("getProyectoHitoAviso()")
        .isEqualTo(created.getProyectoHitoAviso());
    Assertions.assertThat(proyectoHitoCreado.getProyectoHitoAviso().getId())
        .as("getProyectoHitoAviso().getId()").isNull();
  }

  @Test
  void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without ProyectoId
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(generarMockProyectoHito()))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void create_WithoutTipoHitoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without TipoHitoId
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    Proyecto proyecto = generarMockProyecto(1L);
    proyectoHito.setProyectoId(proyecto.getId());
    proyectoHito.setTipoHitoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as TipoHitoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Tipo Hito no puede ser null para realizar la acción sobre ProyectoHito");
  }

  @Test
  void create_WithoutFecha_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without Fecha
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setFecha(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha no puede ser null para realizar la acción sobre ProyectoHito");
  }

  @Test
  void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoHito with non existing Proyecto
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(generarMockProyectoHito()))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void create_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con Proyecto sin Modelo de Ejecucion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyecto.setModeloEjecucion(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no disponible para el ModeloEjecucion '%s'",
            proyectoHito.getTipoHitoId(),
            "Proyecto sin modelo asignado");
  }

  @Test
  void create_WithoutModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con TipoHito no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as ModeloTipoHito not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no disponible para el ModeloEjecucion '%s'",
            proyectoHito.getTipoHitoId(),
            "Proyecto sin modelo asignado");
  }

  @Test
  void create_WithDisabledModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con la asignación de TipoHito al Modelo de Ejecucion
    // de la proyecto inactiva
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, proyectoHito, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as ModeloTipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoHito '%s' no está activo para el ModeloEjecucion '%s'",
            null,
            proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void create_WithDisabledTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito TipoHito disabled
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.FALSE);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as TipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no está activo", modeloTipoHito.getTipoHito().getNombre());
  }

  @Test
  void create_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito fecha duplicada
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoExistente = generarMockProyectoHito(2L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito
        .given(repository.findByProyectoIdAndFechaAndTipoHitoId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Instant>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoExistente));

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  void update_ReturnsProyectoHito() {
    // given: Un nuevo ProyectoHito con el tipoHito actualizado
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setProyectoHitoAviso(ProyectoHitoAviso.builder()
        .comunicadoRef("1")
        .tareaProgramadaRef("1").build());
    ProyectoHitoInput proyectoHitoActualizado = generarMockProyectoHito();
    proyectoHitoActualizado.setTipoHitoId(2L);
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);
    SgiApiInstantTaskOutput task = SgiApiInstantTaskOutput.builder()
        .instant(Instant.now().plus(Period.ofDays(3)))
        .id(1L)
        .build();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHito));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(
            modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(
        modeloTipoHito.getTipoHito()));

    BDDMockito.given(sgiApiTaskService
        .findInstantTaskById(anyLong())).willReturn(task);
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoHito
    ProyectoHito updated = service.update(1L, proyectoHitoActualizado);

    // then: El ProyectoHito se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoHito.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoHito.getProyectoId());
    Assertions.assertThat(updated.getComentario()).as("getComentario()")
        .isEqualTo(proyectoHitoActualizado.getComentario());
    Assertions.assertThat(updated.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(proyectoHitoActualizado.getTipoHitoId());
    Assertions.assertThat(updated.getFecha()).as("getFecha()")
        .isEqualTo(proyectoHitoActualizado.getFecha());
  }

  @Test
  void update_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ProyectoHito con el la fecha anterior
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setProyectoHitoAviso(ProyectoHitoAviso.builder()
        .comunicadoRef("1")
        .tareaProgramadaRef("1").build());
    ProyectoHitoInput proyectoHitoActualizado = generarMockProyectoHito();
    proyectoHitoActualizado.setTipoHitoId(2L);
    proyectoHitoActualizado.setFecha(Instant.now().minus(Period.ofDays(2)));
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);
    SgiApiInstantTaskOutput task = SgiApiInstantTaskOutput.builder()
        .instant(Instant.now().plus(Period.ofDays(3)))
        .id(1L)
        .build();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHito));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(
            modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(
        modeloTipoHito.getTipoHito()));

    BDDMockito.given(sgiApiTaskService
        .findInstantTaskById(anyLong())).willReturn(task);
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoHito
    ProyectoHito updated = service.update(1L, proyectoHitoActualizado);

    // then: El ProyectoHito se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoHito.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoHito.getProyectoId());
    Assertions.assertThat(updated.getComentario()).as("getComentario()")
        .isEqualTo(proyectoHito.getComentario());
    Assertions.assertThat(updated.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(proyectoHito.getTipoHito().getId());
    Assertions.assertThat(updated.getFecha()).as("getFecha()")
        .isEqualTo(proyectoHitoActualizado.getFecha());
    Assertions.assertThat(updated.getProyectoHitoAviso()).as("getProyectoHitoAviso()").isNull();
  }

  @Test
  void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without ProyectoId
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    proyectoHito.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoHito");
  }

  @Test
  void update_WithoutTipoHitoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without TipoHitoId
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    proyectoHito.setTipoHitoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as TipoHitoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Tipo Hito no puede ser null para realizar la acción sobre ProyectoHito");
  }

  @Test
  void update_WithoutFecha_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without Fecha
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    proyectoHito.setFecha(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha no puede ser null para realizar la acción sobre ProyectoHito");
  }

  @Test
  void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoHito with non existing Proyecto
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void update_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con Proyecto sin Modelo de Ejecucion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    proyecto.setModeloEjecucion(null);
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no disponible para el ModeloEjecucion '%s'",
            proyectoHito.getTipoHitoId(),
            "Proyecto sin modelo asignado");
  }

  @Test
  void update_WithoutModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con TipoHito no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as ModeloTipoHito not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no disponible para el ModeloEjecucion '%s'",
            proyectoHito.getTipoHitoId(),
            "Proyecto sin modelo asignado");
  }

  @Test
  void update_WithDisabledModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con la asignación de TipoHito al Modelo de Ejecucion
    // de la proyecto inactiva
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, proyectoHito, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as ModeloTipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoHito '%s' no está activo para el ModeloEjecucion '%s'",
            null,
            proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void update_WithDisabledTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito TipoHito disabled
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.FALSE);
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.setTipoHito(tipoHito);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as TipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito '%s' no está activo", modeloTipoHito.getTipoHito().getNombre());
  }

  @Test
  void update_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: Un ProyectoHito a actualizar con fecha duplicada
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoExistente = generarMockProyectoHito(2L);
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario("comentario modificado");
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.setTipoHito(tipoHito);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(repository.findByProyectoIdAndFechaAndTipoHitoId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoExistente));

    // when: Actualizamos el ProyectoHito
    // then: Lanza una excepcion porque la fecha ya existe para ese tipo
    Assertions.assertThatThrownBy(() -> service.update(1L, proyectoHito))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoHito
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
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
        .isInstanceOf(ProyectoHitoNotFoundException.class);
  }

  @Test
  void findById_ReturnsProyectoHito() {
    // given: Un ProyectoHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoHito(idBuscado)));

    // when: Buscamos el ProyectoHito por su id
    ProyectoHito proyectoHito = service.findById(idBuscado);

    // then: el ProyectoHito
    Assertions.assertThat(proyectoHito).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoHito.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoHitoNotFoundException() throws Exception {
    // given: Ningun ProyectoHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoHito por su id
    // then: lanza un ProyectoHitoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoHitoNotFoundException.class);
  }

  @Test
  void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoHito para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoHito> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoHito(Long.valueOf(i)));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoHito>>any(),
                ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size()
              ? proyectosEntidadesConvocantes.size()
              : toIndex;
          List<ProyectoHito> content = proyectosEntidadesConvocantes.subList(fromIndex,
              toIndex);
          Page<ProyectoHito> pageResponse = new PageImpl<>(content, pageable,
              proyectosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoHito> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoHito del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoHito proyectoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoHito.getId()).isEqualTo(Long.valueOf(i));
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

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);
    modeloEjecucion.setNombre("nombre-modelo-ejecucion");

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-proyecto-" + String.format("%03d", id));
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
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario("estado-proyecto-" + String.format("%03d", id));
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    return estadoProyecto;
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id     id del TipoHito
   * @param activo
   * @return el objeto TipoHito
   */
  private TipoHito generarMockTipoHito(Long id, Boolean activo) {

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre("nombre-hito-" + String.format("%03d", id));
    tipoHito.setDescripcion("descripcion-hito-" + String.format("%03d", id));
    tipoHito.setActivo(activo);

    return tipoHito;
  }

  /**
   * Función que genera ModeloTipoHito a partir de un objeto ProyectoHito
   * 
   * @param id
   * @param proyectoHito
   * @param activo
   * @return
   */
  private ModeloTipoHito generarMockModeloTipoHito(Long id, ProyectoHito proyectoHito, Boolean activo) {

    // @formatter:off
    return ModeloTipoHito.builder()
        .id(id)
        .modeloEjecucion(generarMockProyecto(proyectoHito.getProyectoId()).getModeloEjecucion())
        .tipoHito(proyectoHito.getTipoHito())
        .activo(activo)
        .build();
    // @formatter:on
  }

  private ModeloTipoHito generarMockModeloTipoHito(Long id, ProyectoHitoInput proyectoHito, Boolean activo) {

    // @formatter:off
    return ModeloTipoHito.builder()
        .id(id)
        .modeloEjecucion(generarMockProyecto(proyectoHito.getProyectoId()).getModeloEjecucion())
        .tipoHito(TipoHito.builder()
            .id(proyectoHito.getTipoHitoId())
            .build())
        .activo(activo)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ProyectoHito
   * 
   * @param id id del ProyectoHito
   * @return el objeto ProyectoHito
   */
  private ProyectoHito generarMockProyectoHito(Long id) {

    // @formatter:off
        return ProyectoHito.builder()
                .id(id)
                .proyectoId(1L)
                .fecha(Instant.parse("2020-10-19T00:00:00Z"))
                .comentario("comentario-proyecto-hito" + String.format("%03d", id))
                .proyectoHitoAviso(ProyectoHitoAviso.builder().build())
                .tipoHito(generarMockTipoHito(1L, Boolean.TRUE))
                .build();
        // @formatter:on
  }

  private ProyectoHitoInput generarMockProyectoHito() {

    // @formatter:off
        return ProyectoHitoInput.builder()
                .proyectoId(1L)
                .fecha(Instant.parse("2020-10-19T00:00:00Z"))
                .comentario("comentario-proyecto-hito" + String.format("%03d", 1))
                .aviso(ProyectoHitoAvisoInput.builder().build())
                .tipoHitoId(1L)
                .build();
        // @formatter:on
  }
}
